package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ForumService {

    @Autowired
    ForumDiskussionsthemenClicksByHourRepository clicksByHourRepo;
    @Autowired
    ForumTopicsClicksByHourRepository topicClicksByHourRepo;
    @Autowired
    universalStatsRepository uniRepo;
    @Autowired
    WPWPForoForumRepository forumRepo;
    @Autowired
    WPWPForoTopicsRepository topicRepo;
    @Autowired
    WPWPForoPostsRepository forumPostsRepo;
    @Autowired
    ForumSearchRepository searchRepo;
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
    private ForumModLogRepository forumModLogRepo;
    @Autowired
    private ModLockRepository modLockRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private WPWPForoModsModsRepositoryRepo wpForoModsRepo;
    @Autowired
    private WPWPForoTrashcanRepository wpTrashRepo;
    @Autowired
    private WPWPForoTopicsTrashRepository wpTopicTrashRepo;
    @Autowired
    private LoginService loginService;
    @Autowired
    private LastPingRepository lpRepo;


    /**
     * Fetches all Unmoderated Forum posts.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
    public String getAllUnmoderated() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoPosts post : wpForoPostRepo.getUnmoderatedPosts()) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }

    /**
     * Fetches a page of unmoderated forum posts.
     * @param userId the user that is accessing the forum (acts as a filter).
     * @param filterForum the forum to search in (if applicable to user).
     * @param filterCat the category to search in (if applicable to forum and user).
     * @param filterTopic the topic to search in (if applicable to category, forum and user).
     * @param search searchs in titles emails etc.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
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

    /**
     * Fetches all Moderated Forum posts.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
    public String getAllModerated() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoPosts post : wpForoPostRepo.getModeratedPosts()) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }

    /**
     * Fetches a page of Moderated forum posts.
     * @param userId the user that is accessing the forum (acts as a filter).
     * @param filterForum the forum to search in (if applicable to user).
     * @param filterCat the category to search in (if applicable to forum and user).
     * @param filterTopic the topic to search in (if applicable to category, forum and user).
     * @param search searchs in titles emails etc.
     * @return a JSON-String collection of said posts.
     * @throws JSONException .
     */
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
     * Utility that checks whether a user is an admin.
     * @param userId the user to check for.
     * @return true for admin.
     */
    public boolean isAdmin(int userId){
        return userService.getType(userId).equals("admin");
    }

    /**
     * Fetches a JSON representation of a post's data, edited towards moderation purpose
     * @param post the post to fetch for.
     * @param needsRating whether this post needs a content-rating.
     * @return a JSONObject.
     * @throws JSONException .
     */
    public JSONObject getSinglePostData(WPWPForoPosts post, boolean needsRating) throws JSONException {
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
    public JSONObject getSinglePostData(WPWPForoTrashcan post, boolean needsRating) throws JSONException {
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

    /**
     * Fetches a posts values by id.
     * @param id the id of the post.
     * @return JSON-String containing values of it.
     * @throws JSONException .
     */
    public String getPostById(long id) throws JSONException {
        return wpForoPostRepo.existsById(id) ? getSinglePostData(wpForoPostRepo.findById(id).get(), true).toString() : "no";
    }

    /**
     * Sets the moderation status of the selected post, using the given users' authority.
     * @param id the id of the post.
     * @param status the status to set (0 | 1)
     * @param userId the id of the user.
     * @return whether the method worked as intended.
     */
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

    /**
     * Fetches all forum posts that are currently in the trash-can.
     * @return a JSON-String representation of all trashed posts.
     * @throws JSONException .
     */
    public String getAllTrashed() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoTrashcan post : wpTrashRepo.findAll()) {
            array.put(getSinglePostData(post, true));
        }

        return array.toString();
    }

    /**
     * Deletes the specified forum post by the authority of the given user.
     * @param id the id of the forum post to delete.
     * @param userId the userId exercising their authority.
     * @return whether the deletion worked as intended.
     */
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
     * Restores a post from trashcan.
     * @param postId the postId to restore.
     * @return whether the restoring worked as intended.
     */
    public boolean restoreById(int postId) {
        try {
            restore(wpTrashRepo.findById((long) postId).get());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fetches all Forums that the specified user has access to, and their children.
     * @param userId the user to fetch for.
     * @return a List of Forum-Ids.
     */
    public List<Integer> getAllForumsWithChildrenForUser(int userId) {
        if(isAdmin(userId)) {
            return wpForoForumRepo.getAllForumIds();
        }
        List<Integer> filterForums = new ArrayList<>(wpForoModsRepo.getAllForumByUser(userId));
        for(Integer forum : wpForoModsRepo.getAllForumByUser(userId)) {
            filterForums.addAll(wpForoForumRepo.getAllChildrenOfIds(forum));
        }

        return filterForums;
    }

    /**
     * Checks whether a posts email emulates a users specified email.
     * @param post the post to check for.
     * @return whether a posts given email was specified without harmful impersonation.
     */
    public boolean isUserMailFake(WPWPForoPosts post) {
        return userRepo.getAllEmails().contains(post.getEmail()) && post.getUserId() == 0;
    }

    /**
     * Rates an Email-Address for legit-ness and swearing.
     * @param post the post to check the mail address for.
     * @return a String containg "badEmail" or "good".
     */
    public String getRatingEmail(WPWPForoPosts post) {
        if(isUserMailFake(post)) return "badEmail";

        return "good";
    }

    /**
     * Fetches a JSON-String representation of all bad words from table.
     * @return a JSON-String.
     */
    public String getAllBadWords() {
        JSONArray array = new JSONArray();
        for(Badwords bad : badWordRepo.findAll()) {
            array.put(bad.getBadWord());
        }
        return array.toString();
    }

    /**
     * Adds a bad word to table.
     * @param word the word to add.
     * @return whether the word was correctly added.
     */
    public boolean addBadWord(String word) {
        if(badWordRepo.getByWord(word).isEmpty() && !word.isBlank()) {
            Badwords badWordNew = new Badwords();
            badWordNew.setBadWord(word.toLowerCase());
            badWordRepo.save(badWordNew);
            return true;
        }

        return false;
    }

    /**
     * Removes a bad word from table.
     * @param word the word to remove.
     * @return whether the word was found and removed.
     */
    public boolean removeBadWord(String word) {
        if(badWordRepo.getByWord(word.toLowerCase()).isPresent()) {
            badWordRepo.delete(badWordRepo.getByWord(word.toLowerCase()).get());
            return true;
        }
        return false;
    }

    /**
     * Fetches a rating and adds b tags to all found profanities <b>in place</b>.
     * @param post the post to rate / change.
     * @return a rating.
     */
    public String getRatingSwearing(WPWPForoPosts post) {

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


    /**
     * Deletes post from wp_wpforo_posts, and adds it to trashcan
     * @param post to toss away.
     */
    public void throwTrashcan(WPWPForoPosts post) {
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

    /**
     * Deletes topic from wp_wpforo_topics and adds it to trashcan.
     * @param topic the topic to toss.
     */
    public void throwTrashcan(WPWPForoTopics topic) {
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

    /**
     * Restores a post from the trashcan.
     * @param trash the post to restore from trash.
     */
    public void restore(WPWPForoTrashcan trash) {
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

    /**
     * Restores a topic from the trashcan.
     * @param trash the topic to restore from trash.
     */
    public void restore(WPWPForoTopicsTrash trash) {
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

    /**
     * Unlocks all forum posts, making them open to any editing.
     * @return whether unlock worked correctly.
     */
    public boolean unlockAll() {
        try {
            modLockRepo.deleteAll(modLockRepo.findAll());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Saves edits to a posts contents.
     * @param hson the entire, edited posts as a JSON-String.
     * @param accepted whether the post is now shown.
     * @param userId the userId exercising their authority.
     * @return whether the post was updated.
     * @throws JSONException .
     */
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

    /**
     * Fetches the link to the forum-post on the website, for the specified post.
     * @param postId the post to fetch for.
     * @return the URL to the specified post.
     */
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

    /**
     * Fetches the link to the forum-topic on the website, for the specified post.
     * @param postId the post to fetch for.
     * @return the URL to the specified posts' topic.
     */
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

    /**
     * Fetches the link to the forum on the website, for the specified post.
     * @param postId the post to fetch for.
     * @return the URL to the specified posts' forum.
     */
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

    /**
     * Fetches links to a posts forum, topic and post on the forum itself.
     * @param id the id of the post.
     * @return a JSON-Array-String containing links to forum, topic and post in order.
     */
    public String getLinksAll(long id) {
        JSONArray array = new JSONArray();

        array.put(getLinkToForum(id));
        array.put(getLinkToTopic(id));
        array.put(getLinkToPost(id));

        return array.toString();
    }

    /**
     * Fetches overarching, general statistics of the forum.
     * @return a JSON-Object-String containing the amount of posts in different kinds of areas.
     * @throws JSONException .
     */
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

    /**
     * Locks a post from edit for all except the specified user.
     * @param postId the post to lock.
     * @param userId the user to remain access to the post.
     * @return whether the post was correctly locked.
     */
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

    /**
     * Unlocks a post, making it open to edit for everyone.
     * @param postId the post to unlock.
     * @param userId the user to unlock the post with.
     * @return whether the post was correctly unlocked.
     */
    public boolean unlock(int postId, int userId) {
        if(modLockRepo.findByPostId(postId).isPresent() && modLockRepo.findByPostId(postId).get().getByUserId() == userId) {
            ModLock modLock = modLockRepo.findByPostId(postId).get();
            modLock.setLocked(0);
            modLockRepo.save(modLock);
            return true;
        }
        return false;
    }

    /**
     * Checks whether a post is locked.
     * @param postId the postId to check for.
     * @return whether the post is locked (True means locked).
     */
    public boolean isLocked(int postId) {
        if(modLockRepo.findByPostId(postId).isPresent()) {
            return modLockRepo.findByPostId(postId).get().getLocked() == 1;
        }
        return false;
    }

    /**
     * Checks whether a post is inaccessible to the given user.
     * @param postId the post to check for.
     * @param userId the user to check for.
     * @return whether the post is inaccessible for the user (true meaning no access).
     */
    public boolean isLockedForUser(int postId, int userId) {
        if(isLocked(postId)) {
            return modLockRepo.findByPostId(postId).get().getByUserId() != userId;
        }
        return false;
    }

    /**
     * Unlocks all posts currently locked by the user.
     * @param userId the user to unlock all for.
     * @return whether the posts were correctly unlocked.
     */
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

    /**
     * Fetches the amount of unmoderated posts in all forums and all their subcategories.
     * @param userId the user to fetch for (also acts as a filter, users only see their forums).
     * @return a JSON-String.
     * @throws JSONException .
     */
    public String getModCounts(Integer userId) throws JSONException {

        JSONObject obj = new JSONObject();
        JSONArray forumList = new JSONArray();
        JSONArray topicList = new JSONArray();
        JSONArray catList = new JSONArray();

        List<WPWPForoForum> forums;
        if(userService.getType(userId).equals("admin")) {
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

    /**
     * Saves all ForumDiscussionsClicks in the corresponding table.
     * @param forumDiskussionsClicksMap the map to save.
     */
    @Transactional
    public void persistAllForumDiscussionsClicksHour(Map<Integer, ForumDiskussionsthemenClicksByHour> forumDiskussionsClicksMap) {
        if (!forumDiskussionsClicksMap.isEmpty()) {
            clicksByHourRepo.saveAll(forumDiskussionsClicksMap.values());
        }
    }

    /**
     * Saves all ForumTopicsClicks in the corresponding table.
     * @param forumTopicsClicksMap the map to save.
     */
    @Transactional
    public void persistAllForumTopicsClicksHour(Map<Integer, ForumTopicsClicksByHour> forumTopicsClicksMap) {
        if (!forumTopicsClicksMap.isEmpty()) {
            topicClicksByHourRepo.saveAll(forumTopicsClicksMap.values());
        }
    }

    /**
     * Saves a ForumSearch to table.
     * @param forumSearch the search to save.
     */
    public void saveSearchData(ForumSearch forumSearch) {
        searchRepo.save(forumSearch);
    }

    /**
     * Fetches a forum by its slug.
     * @param slug the slug to fetch for.
     * @return the forums id, if found, else null.
     */
    public Integer getForumIdBySlug(String slug){
        return forumRepo.getForumIdBySlug(slug);
    }

    /**
     * Fetches a topics id by its slug.
     * @param slug the slug to fetch for.
     * @return the TopicsId if found, else null.
     */
    public Integer getTopicIdBySlug(String slug){
        return topicRepo.getTopicIdBySlug(slug);
    }

    /**
     * Get ranked forum discussions based on clicks.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked forum discussions
     * @throws JSONException if a JSON error occurs
     */
    public String getRankedDiscussion(int page, int size) throws JSONException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("clicks").descending());
        Page<ForumDiskussionsthemenClicksByHour> forumClicksPage = clicksByHourRepo.findAll(pageable);

        JSONArray jsonArray = new JSONArray();

        for (ForumDiskussionsthemenClicksByHour forumClicks : forumClicksPage) {
            WPWPForoForum forum = forumRepo.findById(forumClicks.getForumId()).orElse(null);
            if (forum != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", forum.getTitle());
                jsonObject.put("clicks", forumClicks.getClicks());
                jsonObject.put("lastPostDate", forum.getLastPostDate().toString());

                jsonArray.put(jsonObject);
            }
        }

        return jsonArray.toString();
    }

    /**
     * Get ranked forum topics based on clicks.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked forum topics
     * @throws JSONException if a JSON error occurs
     */
    public String getRankedTopic(int page, int size) throws JSONException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("clicks").descending());
        Page<ForumTopicsClicksByHour> forumClicksPage = topicClicksByHourRepo.findAll(pageable);

        JSONArray jsonArray = new JSONArray();

        for (ForumTopicsClicksByHour topicClicks : forumClicksPage) {
            WPWPForoTopics topic = topicRepo.findById(topicClicks.getTopicId()).orElse(null);
            WPWPForoForum forum = forumRepo.findById((long)topic.getForumId()).get();
            if (topic != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", topic.getTitle());
                jsonObject.put("clicks", topicClicks.getClicks());
                jsonObject.put("diskussionstitel", forum.getTitle());

                jsonArray.put(jsonObject);
            }
        }

        return jsonArray.toString();
    }

    /**
     * Get ranked search terms based on frequency.
     *
     * @param page the page number
     * @param size the size of the page
     * @return a JSON string of ranked search terms
     * @throws JSONException if a JSON error occurs
     */
    public String getRankedSearchTerms(int page, int size) throws JSONException {
        Pageable pageable = PageRequest.of(page, size);
        List<Object[]> results = searchRepo.findRankedSearchTerms(pageable);

        JSONArray jsonArray = new JSONArray();
        if (results != null && !results.isEmpty()) {
            for (Object[] result : results) {
                String suchbegriff = (String) result[0];
                Long count = (Long) result[1];

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("suchbegriff", suchbegriff);
                jsonObject.put("count", count);

                jsonArray.put(jsonObject);
            }
        }

        return jsonArray.toString();
    }

    /**
     * Fetches the Top 15 searched terms.
     * @return a JSON-String containing the values of the fetched terms.
     * @throws JSONException .
     */
    public String getRankedSearchTop15(){
        List<ForumSearch> allsearches = searchRepo.findAll();
        Map<String,Long> termCount = new ConcurrentHashMap<>();
        for(ForumSearch f:allsearches){
           Long count = termCount.getOrDefault(f.getSuchbegriff(),0L);
           count++;
           termCount.put(f.getSuchbegriff(),count);
        }
        // Sort the terms by count in descending order and limit to top 15
        List<Map.Entry<String, Long>> top15Terms = termCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(15)
                .collect(Collectors.toList());

        // Convert to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(top15Terms);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; // return an empty JSON object in case of an error
        }
    }

    /**
     * Checks whether the given user has moderator rights on the given forum.
     * @param userId the users id.
     * @param forumId the forums id.
     * @return true if user has rights to forum.
     */
    public boolean isUserModOfForum(int userId, int forumId) {

        return wpForoModsRepo.getAllForumByUser(userId).contains(forumId) || userService.getType(userId).equals("admin");
    }

    /**
     * Attempts to add a new moderator to a forum, respecting role rights.
     * @param modName the displayName of the mod to add.
     * @param forumName the forums name to allow the moderator on.
     * @param request automatic, for user-right scanning.
     * @return true on success.
     */
    public boolean addModToForum(String modName, String forumName, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }

        System.out.println(userid);

        Integer newModId;
        newModId = userRepo.findByDisplayName(modName).isPresent() ? Math.toIntExact(userRepo.findByDisplayName(modName).get().getId()) : null;
        if(newModId == null) return false;

        System.out.println(newModId);

        if(forumName == null || forumName.equals("-1")) {
            for(String forum : wpForoForumRepo.getAllForumNames()) {
                if(isUserModOfForum(userid, wpForoForumRepo.getForumIdByTitle(forum))) {
                    addModToForum(modName, forumName, request);
                }
            }
            return true;
        }

        int forumId = wpForoForumRepo.getForumIdByTitle(forumName);
        System.out.println(forumId);

        if(isUserModOfForum(userid, forumId)) {
            //User has the right to add a moderator to this forum.
            if(userRepo.findById((long) newModId).isPresent()) {
                if(wpForoModsRepo.getAllForumByUser(newModId).contains(forumId)) return true;
                else {
                    try {
                        //Add the new capability to the given user.
                        WPWPForoModsMods moderator = new WPWPForoModsMods();
                        moderator.setForum_id(forumId);
                        moderator.setUserId(newModId);
                        wpForoModsRepo.save(moderator);
                        return true;
                    } catch (Exception e) {
                        //Something went wrong idk shouldn't happen.
                        System.out.println("Waddafak");
                        e.printStackTrace();
                        return false;
                    }
                }
            } else {
                //New moderator does not exist.
                System.out.println("Mod doesnt exist");
                return false;
            }

        } else {
            //User does not have the right to give access.
            System.out.println("You hold no power here");
            return false;
        }

    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkForUnlock() {
        try {
            checkLastPingTimer();
        } catch (Exception e) {
            System.out.println("FEHLER AT checkLastPingTimer");
            e.printStackTrace();
        }
    }

    private void checkLastPingTimer() {
        if(lpRepo.findById(1L).isPresent()) {
            LastPing ping = lpRepo.findById(1L).get();

            if(ping.getTimestamp().toLocalDateTime().plusMinutes(15).isBefore(LocalDateTime.now())) {
                unlockAll();
            }
        }

    }


    public String getModSuggestions(String start) {
        return new JSONArray(wpForoModsRepo.fetchAllModeratorSuggestions(start)).toString();
    }

    public String getForumSuggestions(String start, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return "";
        }

        return new JSONArray(wpForoForumRepo.getAllForumNamesStartingWith(start, userid)).toString();
    }

}
