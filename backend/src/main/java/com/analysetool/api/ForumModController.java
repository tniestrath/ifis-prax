package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.ForumService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/forum", "/0wB4P2mly-xaRmeeDOj0_g/forum"}, method = RequestMethod.GET, produces = "application/json")
public class ForumModController {

    @Autowired
    private WPWPForoPostsRepository wpForoPostRepo;
    @Autowired
    private WPWPForoForumRepository wpForoForumRepo;
    @Autowired
    private WPUserRepository userRepo;
    @Autowired
    private WPWPForoTopicsRepository wpForoTopicsRepo;
    @Autowired
    private BadWordRepository badWordRepo;
    @Autowired
    private ForumService forumService;
    @Autowired
    private ForumModLogRepository forumModLogRepo;
    @Autowired
    private ModLockRepository modLockRepo;

    @GetMapping("/getAllUnmoderated")
    public String getAllUnmoderated() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoPosts post : wpForoPostRepo.getUnmoderatedPosts()) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }

    @GetMapping("/getAllModerated")
    public String getAllModerated() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoPosts post : wpForoPostRepo.getModeratedPosts()) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }


    @GetMapping("/getUnmoderatedWithFilter")
    public String getUnmoderatedWithFilter(int userId, int filterForum, int filterCat, int filterTopic, String search) throws JSONException {
        JSONArray array = new JSONArray();

        List<WPWPForoPosts> list;
        List<Integer> filterForums;

        if(userId != 0) {
            //ToDo: Add DB Table to give mods a list of forums, then add forums to list
            filterForums = new ArrayList<>();
        } else {
            filterForums = new ArrayList<>();
            filterForums.add(filterForum);
        }

        if(filterForum == 0) {
            list = wpForoPostRepo.getUnmoderatedPosts();
        } else if(filterCat == 0) {
            list = wpForoPostRepo.geUnmoderatedWithFilter(filterForums, search);
        } else if(filterTopic == 0) {
            list = wpForoPostRepo.geUnmoderatedWithFilters2(filterForums, filterCat, search);
        } else {
            list = wpForoPostRepo.geUnmoderatedWithFilters3(filterForums, filterCat, filterTopic, search);
        }




        for (WPWPForoPosts post : list) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }


    @GetMapping("/getModeratedWithFilter")
    public String getModeratedWithFilter(int userId, int filterForum, int filterCat, int filterTopic, String search) throws JSONException {
        JSONArray array = new JSONArray();

        List<WPWPForoPosts> list;
        List<Integer> filterForums;

        if(userId != 0) {
            //ToDo: Add DB Table to give mods a list of forums, then add forums to list
            filterForums = new ArrayList<>();
        } else {
            filterForums = new ArrayList<>();
            filterForums.add(filterForum);
        }

        if(filterForum == 0) {
            list = wpForoPostRepo.getModeratedPosts();
        } else if(filterCat == 0) {
            list = wpForoPostRepo.geModeratedWithFilter(filterForums, search);
        } else if(filterTopic == 0) {
            list = wpForoPostRepo.geModeratedWithFilters2(filterForums, filterCat, search);
        } else {
            list = wpForoPostRepo.getModeratedWithFilters3(filterForums, filterCat, filterTopic, search);
        }




        for (WPWPForoPosts post : list) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }


    /**
     * Fetches a JSON representation of a post's data, edited towards moderation purpose
     * @param post the post to fetch for.
     * @param needsRating whether this post needs a content-rating.
     * @return a JSONObject.
     * @throws JSONException .
     */
    private JSONObject getSinglePostData(WPWPForoPosts post, boolean needsRating) throws JSONException {
        JSONObject json = new JSONObject();

        String postBody = post.getBody();

        json.put("forum", wpForoForumRepo.findById((long) post.getForumId()).isPresent() ? wpForoForumRepo.findById((long) post.getForumId()).get().getTitle() : "none");
        json.put("topic", wpForoTopicsRepo.findById((long) post.getTopicId()).isPresent() ? wpForoTopicsRepo.findById((long) post.getTopicId()).get().getTitle() : "none");
        json.put("id", post.getPostId());
        json.put("date", post.getCreated().toString());
        json.put("email", post.getEmail());


        if(needsRating) {
            json.put("preRatingEmail", getRatingEmail(post));
            json.put("preRatingSwearing", getRatingSwearing(post));
        }


        json.put("isLocked", isLocked(post.getPostId()));
        json.put("body", post.getBody());
        json.put("title", post.getTitle());
        json.put("userName", post.getName());
        json.put("isQuestion", post.getIsFirstPost());

        return json;
    }

    @GetMapping("/getPostById")
    public String getPostById(long id) throws JSONException {
        return wpForoPostRepo.existsById(id) ? getSinglePostData(wpForoPostRepo.findById(id).get(), true).toString() : "no";
    }

    private boolean isUserMailFake(WPWPForoPosts post) {
        return userRepo.getAllEmails().contains(post.getEmail()) && post.getUserId() == 0;
    }

    private String getRatingEmail(WPWPForoPosts post) {
        if(isUserMailFake(post)) return "badEmail";

        return "good";
    }

    /**
     * Fetches a rating and adds b tags to all found profanities <b>in place</b>.
     * @param post the post to rate / change.
     * @return a rating.
     */
    private String getRatingSwearing(WPWPForoPosts post) {

        boolean body = false, title = false, user = false, email = false;
        ArrayList<String> bodyList = new ArrayList<>(), titleList = new ArrayList<>(), userList = new ArrayList<>(), emailList = new ArrayList<>();

        for(String badWord : badWordRepo.getAllBadWords()) {
            if(post.getBody().toUpperCase().contains(badWord.toUpperCase())) {
                body = true;
                bodyList.add(badWord);
            }
            if(post.getTitle().toUpperCase().contains(badWord.toUpperCase())) {
                title = true;
                titleList.add(badWord);
            }
            if(post.getName().toUpperCase().contains(badWord.toUpperCase())) {
                user = true;
                userList.add(badWord);
            }
            if(!post.getEmail().isBlank() && post.getEmail().toUpperCase().contains(badWord.toUpperCase())) {
                email = true;
                emailList.add(badWord);
            }
        }

        //Sort lists to get to the longest words first, to avoid doubly replacing things
        bodyList.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
        titleList.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
        userList.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
        emailList.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));

        for(String badWord : bodyList) {
            String regex = "(?<!title=\")" + "(?i)" + badWord;
            post.setBody(post.getBody().replaceAll(regex, "<b title=\"" + badWord + "\"" + ">****</b>"));
        }
        for(String badWord : titleList) {
            String regex = "(?<!title=\")" + "(?i)" + badWord;
            post.setTitle(post.getTitle().replaceAll(regex, "<b title=\"" + badWord + "\"" + ">****</b>"));
        }
        for(String badWord : userList) {
            String regex = "(?<!title=\")" + "(?i)" + badWord;
            post.setName(post.getName().replaceAll(regex, "<b title=\"" + badWord + "\"" + ">****</b>"));
        }
        for(String badWord : emailList) {
            String regex = "(?<!title=\")" + "(?i)" + badWord;
            post.setEmail(post.getEmail().replaceAll(regex, "<b title=\"" + badWord + "\"" + ">****</b>"));
        }

        if(!body && !title && !user) {
            return "good";
        } else {
            String returnal = "bad";
            if(body) returnal+="Body";
            if(title) returnal+="Title";
            if(user) returnal+="User";
            if(email) returnal+="Email";
            return returnal;
        }
    }

    private String getUserName(WPWPForoPosts post) {
        if(!post.getName().isBlank()) {
            return post.getName();
        } else if(post.getUserId() != 0) {
            return userRepo.findById((long) post.getUserId()).isPresent() ? userRepo.findById((long) post.getUserId()).get().getDisplayName() : "none";
        }
        return "Anonym";
    }

    @PostMapping("/deleteById")
    public boolean deleteById(int id, int userId) {
        if(wpForoPostRepo.findById((long) id).isPresent() && !isLockedForUser(id, userId)) {
            if(wpForoPostRepo.findById((long) id).get().getIsFirstPost() == 1) {
                wpForoTopicsRepo.deleteById((long) wpForoTopicsRepo.getTopicByFirstPost(id));
            }
            wpForoPostRepo.deleteById((long) id);
            unlock(id, userId);
            return true;
        }
        return false;
    }

    @PostMapping("/setStatusById")
    public boolean setStatus(int id, int status, int userId) {
        if(wpForoPostRepo.findById((long) id).isPresent() && !isLockedForUser(id, userId)) {
            WPWPForoPosts post = wpForoPostRepo.findById((long) id).get();
            post.setStatus(status);
            wpForoPostRepo.save(post);
            unlock(id, userId);
            return true;
        }
        return false;
    }

    @GetMapping("/getAllBadWords")
    public String getAllBadWords() {
        JSONArray array = new JSONArray();
        for(Badwords bad : badWordRepo.findAll()) {
            array.put(bad.getBadWord());
        }
        return array.toString();
    }

    @PostMapping("/addBadWord")
    public boolean addBadWord(String word) {
        if(badWordRepo.getByWord(word).isEmpty() && !word.isBlank()) {
            Badwords badWordNew = new Badwords();
            badWordNew.setBadWord(word.toLowerCase());
            badWordRepo.save(badWordNew);
            return true;
        }

        return false;
    }

    @PostMapping("/removeBadWord")
    public boolean removeBadWord(String word) {
        if(badWordRepo.getByWord(word.toLowerCase()).isPresent()) {
            badWordRepo.delete(badWordRepo.getByWord(word.toLowerCase()).get());
            return true;
        }
        return false;
    }

    /**
     * Get ranked forum discussions based on clicks.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked forum discussions
     * @throws JSONException if a JSON error occurs
     */
    @GetMapping("/rankedDiscussions")
    public String getRankedDiscussions(@RequestParam int page, @RequestParam int size)  {
        try{
        return forumService.getRankedDiscussion(page, size);}catch(Exception e){
            e.printStackTrace();
            return "buggy bug bug";
        }
    }

    /**
     * Get ranked forum topics based on clicks.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked forum topics
     * @throws JSONException if a JSON error occurs
     */
    @GetMapping("/rankedTopics")
    public String getRankedTopics(@RequestParam int page, @RequestParam int size) {
        try{
        return forumService.getRankedTopic(page, size);}catch(Exception e){
            e.printStackTrace();
            return "sir bugs-alot";
        }
    }

    /**
     * Get ranked search terms based on frequency.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked search terms
     * @throws JSONException if a JSON error occurs
     */
    @GetMapping("/rankedSearchTerms")
    public String getRankedSearchTerms(@RequestParam int page, @RequestParam int size)  {
        try{
        return forumService.getRankedSearchTerms(page, size);}catch(Exception e){
            e.printStackTrace();
            return "a bug a day keeps happiness away";
        }
    }

    @GetMapping("/rankedSearchTermsTop15")
    public String getRankedSearchTermsTop15() throws JSONException {
        try{
            return forumService.getRankedSearchTop15();}catch(Exception e){
            e.printStackTrace();
            return "a bug a day keeps happiness away";
        }
    }

    @PostMapping("/updatePost")
    public boolean updatePost(@RequestBody String hson, boolean accepted, int userId) throws JSONException {
        JSONObject json = new JSONObject(hson);
        try {
            if(wpForoPostRepo.findById((long) json.getInt("id")).isPresent() && !isLockedForUser(json.getInt("id"), userId)) {

                WPWPForoPosts post = wpForoPostRepo.findById((long) json.getInt("id")).get();

                post.setEmail(json.getString("email"));
                post.setBody(json.getString("body"));
                post.setTitle(json.getString("title"));
                post.setName(json.getString("userName"));
                post.setStatus(accepted ? 0 : 1);

                wpForoPostRepo.save(post);

                ForumModLog log = new ForumModLog();
                log.setPostId(json.getInt("id"));
                log.setUserId(userId);
                log.setTime(Timestamp.from(LocalDateTime.now().atZone(ZoneId.of("Europe/Paris")).toInstant()));
                forumModLogRepo.save(log);

                unlock(json.getInt("id"), userId);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }


    }

    @GetMapping("/getLinkToPost")
    public String getLinkToPost(long postId) {
        String link = "https://it-sicherheit.de/forum-it-sicherheit/";

        if(wpForoPostRepo.findById(postId).isPresent()) {
            WPWPForoPosts post = wpForoPostRepo.findById(postId).get();
            try {
                link += "postid/" + post.getPostId();
            } catch (NoSuchElementException e) {
                return "kein derartiges Element";
            }
        }

        return link;
    }

    @GetMapping("/getLinkToTopic")
    public String getLinkToTopic(long postId) {
        String link = "https://it-sicherheit.de/forum-it-sicherheit/";

        if(wpForoPostRepo.findById(postId).isPresent()) {
            WPWPForoPosts post = wpForoPostRepo.findById(postId).get();
            try {
                WPWPForoTopics topic = wpForoTopicsRepo.findById((long) post.getTopicId()).orElseThrow();
                WPWPForoForum forum = wpForoForumRepo.findById((long) topic.getForumId()).orElseThrow();
                link += forum.getSlug() + "/";
                link += topic.getSlug() + "/";
            } catch (NoSuchElementException e) {
                return "kein derartiges Element";
            }


        }

        return link;
    }

    @GetMapping("/getLinkToForum")
    public String getLinkToForum(long postId) {
        String link = "https://it-sicherheit.de/forum-it-sicherheit/";

        if(wpForoPostRepo.findById(postId).isPresent()) {
            WPWPForoPosts post = wpForoPostRepo.findById(postId).get();
            try {
                WPWPForoTopics topic = wpForoTopicsRepo.findById((long) post.getTopicId()).orElseThrow();
                WPWPForoForum forum = wpForoForumRepo.findById((long) topic.getForumId()).orElseThrow();
                link += forum.getSlug() + "/";
            } catch (NoSuchElementException e) {
                return "kein derartiges Element";
            }
        }

        return link;
    }

    @GetMapping("/getLinks")
    public String getLinksAll(long id) {
        JSONArray array = new JSONArray();

        array.put(getLinkToForum(id));
        array.put(getLinkToTopic(id));
        array.put(getLinkToPost(id));

        return array.toString();
    }

    @GetMapping("/getCounts")
    public String getCounts() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("questions", wpForoPostRepo.getCountQuestions());
        json.put("answers", wpForoPostRepo.getCountAnswers());
        json.put("topics", wpForoTopicsRepo.getCountTopicsTotal());
        json.put("topicsClosed", wpForoTopicsRepo.getCountTopicsClosed());
        json.put("topicsAnswered", wpForoTopicsRepo.getCountTopicsAnswered());
        json.put("forums", wpForoForumRepo.getCountForums() - 1);

        return json.toString();

    }

    @GetMapping("/isLocked")
    public boolean lock(int postId, int userId) {
        //If Post is locked and not locked by this user, tell user to fk off
        if(modLockRepo.findByPostId(postId).isPresent() && (modLockRepo.findByPostId(postId).get().getLocked() == 1 && modLockRepo.findByPostId(postId).get().getByUserId() != userId)) {
            return true;
        } //If post was unlocked, lock it for this user
        else if(modLockRepo.findByPostId(postId).isEmpty() || modLockRepo.findByPostId(postId).get().getLocked() != 1){
            ModLock modLock;
            modLock = modLockRepo.findByPostId(postId).isEmpty() ? new ModLock() : modLockRepo.findByPostId(postId).get();
            modLock.setLocked(1);
            modLock.setByUserId(userId);
            modLock.setPostId(postId);
            modLockRepo.save(modLock);
            return false;
        } //This user locked this post, so allow him.
        else {
            return false;
        }
    }

    @GetMapping("/unlock")
    public boolean unlock(int postId, int userId) {
        if(modLockRepo.findByPostId(postId).isPresent() && modLockRepo.findByPostId(postId).get().getByUserId() == userId) {
            ModLock modLock = modLockRepo.findByPostId(postId).get();
            modLock.setLocked(0);
            modLockRepo.save(modLock);
            return true;
        }
        return false;
    }

    private boolean isLocked(int postId) {
        if(modLockRepo.findByPostId(postId).isPresent()) {
            return modLockRepo.findByPostId(postId).get().getLocked() == 1;
        }
        return false;
    }

    private boolean isLockedForUser(int postId, int userId) {
        if(isLocked(postId)) {
            return modLockRepo.findByPostId(postId).get().getByUserId() != userId;
        }
        return false;
    }

    @GetMapping("/unlockAll")
    public boolean unlockAllForUser(int userId) {
        try {
            for (ModLock modLock : modLockRepo.findByUserId(userId)) {
                modLock.setLocked(0);
                modLockRepo.save(modLock);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
