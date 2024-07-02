package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.ForumService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    @Autowired
    private UserController userController;
    @Autowired
    private WPWPForoModsModsRepositoryRepo wpForoModsRepo;
    @Autowired
    private WPWPForoTrashcanRepository wpTrashRepo;
    @Autowired
    private WPWPForoTopicsTrashRepository wpTopicTrashRepo;

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


    /*
     * admin = admin
     * */
    private boolean isAdmin(int userId){
        return userController.getType(userId).equals("admin");
    }

    @GetMapping("/getUnmoderatedWithFilter")
    public String getUnmoderatedWithFilter(int userId, int filterForum, int filterCat, int filterTopic, String search) throws JSONException {
        JSONArray array = new JSONArray();

        List<WPWPForoPosts> list;
        List<Integer> filterForums;

        boolean isAdmin = isAdmin(userId);


        //Set all forums that are allowed for user
        if(userId != 0 && !isAdmin) {
            filterForums = wpForoModsRepo.getAllForumByUser(userId);
        } else if(isAdmin) {
            filterForums = wpForoModsRepo.getAllForumForAdmin();
        } else {
            filterForums = new ArrayList<>();
        }

        //If a filter was set, and it was allowed, add only that one and all children to allow-list
        if(filterForum != 0 && filterForums.contains(filterForum)) {
            filterForums = new ArrayList<>();
            filterForums.add(filterForum);
            filterForums.addAll(wpForoForumRepo.getAllChildrenOfIds(filterForum));
        }

        //Fetch all that meet the specifics
        if(filterForum == 0) {
            list = wpForoPostRepo.getUnmoderatedPosts();
        } else if(filterCat == 0) {
            list = wpForoPostRepo.geUnmoderatedWithFilter(filterForums, search);
        } else if(filterTopic == 0) {
            list = wpForoPostRepo.geUnmoderatedWithFilters2(filterForums, filterCat, search);
        } else {
            list = wpForoPostRepo.geUnmoderatedWithFilters3(filterForums, filterTopic, search);
        }

        //Fetch data
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

        boolean isAdmin = isAdmin(userId);

        //Set all forums that are allowed for user
        if(userId != 0 && !isAdmin) {
            filterForums = wpForoModsRepo.getAllForumByUser(userId);
        } else if(isAdmin) {
            filterForums = wpForoModsRepo.getAllForumForAdmin();
        } else {
            filterForums = new ArrayList<>();
        }

        //If a filter was set, and it was allowed, add only that one and all children to allow-list
        if(filterForum != 0 && filterForums.contains(filterForum)) {
            filterForums = new ArrayList<>();
            filterForums.add(filterForum);
            filterForums.addAll(wpForoForumRepo.getAllChildrenOfIds(filterForum));
        }

        if(filterForum == 0) {
            list = wpForoPostRepo.getModeratedPosts();
        } else if(filterCat == 0) {
            list = wpForoPostRepo.geModeratedWithFilter(filterForums, search);
        } else if(filterTopic == 0) {
            list = wpForoPostRepo.geModeratedWithFilters2(filterForums, filterCat, search);
        } else {
            list = wpForoPostRepo.getModeratedWithFilters3(filterForums, filterTopic, search);
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

    /**
     * Fetches a JSON representation of a post's data, edited towards moderation purpose
     * @param post the post to fetch for.
     * @param needsRating whether this post needs a content-rating.
     * @return a JSONObject.
     * @throws JSONException .
     */
    private JSONObject getSinglePostData(WPWPForoTrashcan post, boolean needsRating) throws JSONException {
        JSONObject json = new JSONObject();

        String postBody = post.getBody();

        json.put("forum", wpForoForumRepo.findById((long) post.getForumId()).isPresent() ? wpForoForumRepo.findById((long) post.getForumId()).get().getTitle() : "none");
        json.put("topic", wpForoTopicsRepo.findById((long) post.getTopicId()).isPresent() ? wpForoTopicsRepo.findById((long) post.getTopicId()).get().getTitle() : "none");
        json.put("id", post.getPostId());
        json.put("date", post.getCreated().toString());
        json.put("email", post.getEmail());

        json.put("body", post.getBody());
        json.put("title", post.getTitle());
        json.put("userName", post.getName());
        json.put("isQuestion", post.getIsFirstPost());

        return json;
    }

    private List<Integer> getAllForumsWithChildrenForUser(int userId) {

        if(isAdmin(userId)) {
            return wpForoForumRepo.getAllForumIds();
        }
        List<Integer> filterForums = new ArrayList<>(wpForoModsRepo.getAllForumByUser(userId));
        for(Integer forum : wpForoModsRepo.getAllForumByUser(userId)) {
            filterForums.addAll(wpForoForumRepo.getAllChildrenOfIds(forum));
        }

        return filterForums;
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

        if(wpForoPostRepo.findById((long) id).isPresent() && !isLockedForUser(id, userId) && getAllForumsWithChildrenForUser(userId).contains(wpForoPostRepo.findById((long) id).get().getForumId())) {
            WPWPForoPosts post = wpForoPostRepo.findById((long) id).get();

            throwTrashcan(post);

            unlock(id, userId);
            return true;
        }
        return false;
    }

    /**
     * Deletes post from wp_wpforo_posts, and adds it to trashcan
     * @param post to toss away.
     */
    private void throwTrashcan(WPWPForoPosts post) {
        WPWPForoTrashcan trash = new WPWPForoTrashcan();
        trash.setCreated(post.getCreated());
        trash.setForumId(post.getForumId());
        trash.setIsAnswer(post.getIsAnswer());
        trash.setLikes(post.getLikes());
        trash.setModified(post.getModified());
        trash.setRoot(post.getRoot());
        trash.setIsPrivate(post.getIsPrivate());
        trash.setParentId(post.getParentId());
        trash.setStatus(post.getStatus());
        trash.setBody(post.getBody());
        trash.setIsFirstPost(post.getIsFirstPost());
        trash.setVotes(post.getVotes());
        trash.setEmail(post.getEmail());
        trash.setName(post.getName());
        trash.setUserId(post.getUserId());
        trash.setTopicId(post.getTopicId());
        trash.setTitle(post.getTitle());
        trash.setPostId(post.getPostId());
        wpTrashRepo.save(trash);


        if(post.getStatus() == 0) {
            WPWPForoTopics topic = wpForoTopicsRepo.findById((long) post.getTopicId()).get();
            topic.setPosts(topic.getPosts() - 1);
            topic.setLastPost(wpForoPostRepo.getPostInTopic(topic.getTopicId(), PageRequest.of(1, 1)).get(0).getPostId());
            wpForoTopicsRepo.save(topic);


            WPWPForoForum forum = wpForoForumRepo.findById((long) post.getForumId()).get();
            forum.setPosts(forum.getPosts() - 1);
            wpForoForumRepo.save(forum);
        }

        if(post.getIsFirstPost() == 1) {
            throwTrashcan(wpForoTopicsRepo.findById((long) post.getTopicId()).get());
        }


        wpForoPostRepo.delete(post);
    }

    private void throwTrashcan(WPWPForoTopics topic) {
        WPWPForoTopicsTrash trash = new WPWPForoTopicsTrash();
        trash.setAnswers(topic.getAnswers());
        trash.setClosed(topic.getClosed());
        trash.setHasAttach(topic.getHasAttach());
        trash.setFirstPostId(topic.getFirstPostId());
        trash.setMetaDesc(topic.getMetaDesc());
        trash.setLastPost(topic.getLastPost());
        trash.setMetaKey(topic.getMetaKey());
        trash.setPrefix(topic.getPrefix());
        trash.setTopicId(topic.getTopicId());
        trash.setForumId(topic.getForumId());
        trash.setSlug(topic.getSlug());
        trash.setPosts(topic.getPosts());
        trash.setViews(topic.getViews());
        trash.setVotes(topic.getVotes());
        trash.setUserId(topic.getUserId());
        trash.setTitle(topic.getTitle());
        trash.setType(topic.getType());
        trash.setStatus(topic.getStatus());
        trash.setSolved(topic.getSolved());
        trash.setName(topic.getName());
        trash.setModified(topic.getModified());
        trash.setIsPrivate(topic.getIsPrivate());
        trash.setEmail(topic.getEmail());
        trash.setCreated(topic.getCreated());
        trash.setTags(topic.getTags());


        WPWPForoForum forum = wpForoForumRepo.findById((long) topic.getForumId()).get();
        forum.setTopics(forum.getPosts() - 1);
        wpForoForumRepo.save(forum);

        wpTopicTrashRepo.save(trash);
        wpForoTopicsRepo.delete(topic);
    }


    private void restore(WPWPForoTrashcan trash) {
        WPWPForoPosts post = new WPWPForoPosts();
        post.setCreated(trash.getCreated());
        post.setForumId(trash.getForumId());
        post.setIsAnswer(trash.getIsAnswer());
        post.setLikes(trash.getLikes());
        post.setModified(trash.getModified());
        post.setRoot(trash.getRoot());
        post.setIsPrivate(trash.getIsPrivate());
        post.setParentId(trash.getParentId());
        post.setStatus(trash.getStatus());
        post.setBody(trash.getBody());
        post.setIsFirstPost(trash.getIsFirstPost());
        post.setVotes(trash.getVotes());
        post.setPostId(trash.getPostId());
        post.setEmail(trash.getEmail());
        post.setName(trash.getName());
        post.setUserId(trash.getUserId());
        post.setTopicId(trash.getTopicId());
        post.setTitle(trash.getTitle());
        wpForoPostRepo.save(post);



        if(trash.getIsFirstPost() == 1) {
            restore(wpTopicTrashRepo.findById((long) post.getTopicId()).get());
        }


        if (post.getStatus() == 0) {
            try {
                WPWPForoTopics topic = wpForoTopicsRepo.findById((long) post.getTopicId()).get();
                topic.setPosts(topic.getPosts() + 1);
                topic.setLastPost(post.getPostId());
                wpForoTopicsRepo.save(topic);

                WPWPForoForum forum = wpForoForumRepo.findById((long) post.getForumId()).get();
                forum.setPosts(forum.getPosts() + 1);
                wpForoForumRepo.save(forum);
            } catch (Exception e) {
                System.out.println("SMALL ERROR RESTORING FORUM_POST: " + trash.getPostId());
            }
        }


        wpTrashRepo.delete(trash);
    }

    private void restore(WPWPForoTopicsTrash trash) {
        WPWPForoTopics topic = new WPWPForoTopics();
        topic.setAnswers(trash.getAnswers());
        topic.setClosed(trash.getClosed());
        topic.setHasAttach(trash.getHasAttach());
        topic.setFirstPostId(trash.getFirstPostId());
        topic.setMetaDesc(trash.getMetaDesc());
        topic.setLastPost(trash.getLastPost());
        topic.setMetaKey(trash.getMetaKey());
        topic.setPrefix(trash.getPrefix());
        topic.setTopicId(trash.getTopicId());
        topic.setForumId(trash.getForumId());
        topic.setSlug(trash.getSlug());
        topic.setPosts(trash.getPosts());
        topic.setViews(trash.getViews());
        topic.setVotes(trash.getVotes());
        topic.setUserId(trash.getUserId());
        topic.setTitle(trash.getTitle());
        topic.setType(trash.getType());
        topic.setStatus(trash.getStatus());
        topic.setSolved(trash.getSolved());
        topic.setName(trash.getName());
        topic.setModified(trash.getModified());
        topic.setIsPrivate(trash.getIsPrivate());
        topic.setEmail(trash.getEmail());
        topic.setCreated(trash.getCreated());
        topic.setTags(trash.getTags());

        wpForoTopicsRepo.save(topic);


        if (topic.getStatus() == 0) {
            WPWPForoForum forum = wpForoForumRepo.findById((long) topic.getForumId()).get();
            forum.setTopics(forum.getTopics() + 1);
            wpForoForumRepo.save(forum);
        }


        wpTopicTrashRepo.delete(trash);
    }

    @GetMapping("/getAllTrashed")
    public String getAllTrashed() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoTrashcan post : wpTrashRepo.findAll()) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }

    @GetMapping("/restore")
    public boolean restoreById(int postId) {
        try {
            restore(wpTrashRepo.findById((long) postId).get());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/setStatusById")
    public boolean setStatus(int id, int status, int userId) {
        
        if(wpForoPostRepo.findById((long) id).isPresent() && !isLockedForUser(id, userId)) {
            WPWPForoPosts post = wpForoPostRepo.findById((long) id).get();
            post.setStatus(status);
            wpForoPostRepo.save(post);

            if(post.getIsFirstPost() == 1 && post.getTopicId() != 0) {
                try {
                    WPWPForoTopics topic = wpForoTopicsRepo.findById((long) post.getTopicId()).get();
                    topic.setStatus(status);
                    wpForoTopicsRepo.save(topic);
                } catch (Exception e) {
                    System.out.println("Couldn't set status for topic: " + post.getTopicId());
                }

            }

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

    public boolean unlockAll() {
        try {
            modLockRepo.deleteAll(modLockRepo.findAll());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/getModCounts")
    public String getModCounts(Integer userId) throws JSONException {

        JSONObject obj = new JSONObject();
        JSONArray forumList = new JSONArray();
        JSONArray topicList = new JSONArray();
        JSONArray catList = new JSONArray();

        List<WPWPForoForum> forums;
        if(userController.getType(userId).equals("admin")) {
            forums = wpForoForumRepo.getAllNotCatAdmin();
        } else {
            forums = wpForoForumRepo.getAllNotCat(userId);
        }

        for(WPWPForoForum forum : forums) {
            JSONObject json = new JSONObject();
            json.put("name", forum.getTitle());
            json.put("forumId", forum.getForumId());
            json.put("topicId", 0);
            json.put("catId", 0);
            json.put("count", wpForoPostRepo.getCountUnmoderatedInForum(forum.getForumId()));
            forumList.put(json);

            for(WPWPForoTopics topic : wpForoTopicsRepo.getAllTopicsInForum(forum.getForumId())) {
                JSONObject topicJson = new JSONObject();
                topicJson.put("name", topic.getTitle());
                topicJson.put("topicId", topic.getTopicId());
                topicJson.put("forumId", forum.getForumId());
                topicJson.put("catId", 0);
                topicJson.put("count", wpForoPostRepo.getCountUnmoderatedInTopic(topic.getTopicId()));
                topicList.put(topicJson);
            }

            for(WPWPForoForum cat : wpForoForumRepo.getAllChildrenOf(forum.getForumId())) {
                JSONObject catJson = new JSONObject();

                catJson.put("name", cat.getTitle());
                catJson.put("forumId", forum.getForumId());
                catJson.put("topicId", 0);
                catJson.put("catId", cat.getForumId());
                catJson.put("count", wpForoPostRepo.getCountUnmoderatedInForum(cat.getForumId()));
                catList.put(catJson);

                for(WPWPForoTopics topic : wpForoTopicsRepo.getAllTopicsInForum(cat.getForumId())) {
                    JSONObject topicJson = new JSONObject();
                    topicJson.put("name", topic.getTitle());
                    topicJson.put("topicId", topic.getTopicId());
                    topicJson.put("forumId", forum.getForumId());
                    topicJson.put("catId", 0);
                    topicJson.put("count", wpForoPostRepo.getCountUnmoderatedInTopic(topic.getTopicId()));
                    topicList.put(topicJson);
                }
            }
        }

        obj.put("forums", forumList);
        obj.put("topics", topicList);
        obj.put("cats", catList);

        return obj.toString();
    }

}
