package com.analysetool.api;

import com.analysetool.modells.Badwords;
import com.analysetool.modells.WPWPForoPosts;
import com.analysetool.repositories.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/forum")
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

        json.put("body", post.getBody());
        json.put("title", post.getTitle());
        json.put("userName", post.getName());

        return json;
    }

    @GetMapping("/getPostById")
    public String getPostById(long id) throws JSONException {
        return wpForoPostRepo.existsById(id) ? getSinglePostData(wpForoPostRepo.findById(id).get(), false).toString() : "no";
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

        boolean body = false, title = false, user = false;
        ArrayList<String> bodyList = new ArrayList<>(), titleList = new ArrayList<>(), userList = new ArrayList<>();

        for(String badWord : badWordRepo.getAllBadWords()) {
            if(post.getBody().contains(badWord)) {
                body = true;
                bodyList.add(badWord);
            }
            if(post.getTitle().contains(badWord)) {
                title = true;
                titleList.add(badWord);
            }
            if(post.getName().contains(badWord)) {
                user = true;
                userList.add(badWord);
            }
        }

        //Sort lists to get to the longest words first, to avoid doubly replacing things
        bodyList.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
        titleList.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
        userList.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));

        for(String badWord : bodyList) {
            String regex = "(?<!title=)\b" + badWord + "\b";
            post.setBody(post.getBody().replaceAll(regex, "<b title=" + badWord + ">****</b>"));
        }
        for(String badWord : titleList) {
            String regex = "(?<!title=)\b" + badWord + "\b";
            post.setTitle(post.getTitle().replaceAll(regex, "<b title=" + badWord + ">****</b>"));
        }
        for(String badWord : userList) {
            String regex = "(?<!title=)\b" + badWord + "\b";
            post.setName(post.getName().replaceAll(regex, "<b title=" + badWord + ">****</b>"));
        }

        if(!body && !title && !user) {
            return "good";
        } else {
            String returnal = "bad";
            if(body) returnal+="Body";
            if(title) returnal+="Title";
            if(user) returnal+="User";
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
    public boolean deleteById(int id) {
        if(wpForoPostRepo.existsById((long) id)) {
            wpForoPostRepo.deleteById((long) id);
            return true;
        }
        return false;
    }

    @PostMapping("/setStatusById")
    public boolean setStatus(int id, int status) {
        if(wpForoPostRepo.findById((long) id).isPresent()) {
            WPWPForoPosts post = wpForoPostRepo.findById((long) id).get();
            post.setStatus(status);
            wpForoPostRepo.save(post);
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
            badWordNew.setBadWord(word);
            badWordRepo.save(badWordNew);
            return true;
        }

        return false;
    }

}
