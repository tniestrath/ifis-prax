package com.analysetool.api;

import com.analysetool.modells.WPWPForoPosts;
import com.analysetool.repositories.WPUserRepository;
import com.analysetool.repositories.WPWPForoForumRepository;
import com.analysetool.repositories.WPWPForoPostsRepository;
import com.analysetool.repositories.WPWPForoTopicsRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getAllUnmoderated")
    public String getAllUnmoderated() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoPosts post : wpForoPostRepo.getUnmoderatedPosts()) {
            array.put(getSinglePostData(post));
        }

        return array.toString();
    }

    @GetMapping("/getAllModerated")
    public String getAllModerated() throws JSONException {
        JSONArray array = new JSONArray();
        for (WPWPForoPosts post : wpForoPostRepo.getModeratedPosts()) {
            array.put(getSinglePostData(post));
        }

        return array.toString();
    }



    private JSONObject getSinglePostData(WPWPForoPosts post, boolean isParent) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("forum", wpForoForumRepo.findById((long) post.getForumId()).isPresent() ? wpForoForumRepo.findById((long) post.getForumId()).get().getTitle() : "none");
        json.put("topic", wpForoTopicsRepo.findById((long) post.getTopicId()).isPresent() ? wpForoTopicsRepo.findById((long) post.getTopicId()).get().getTitle() : "none");
        json.put("id", post.getPostId());
        json.put("body", post.getBody());
        json.put("title", post.getTitle());
        json.put("date", post.getCreated().toString());
        json.put("userName", getUserName(post));
        json.put("email", post.getEmail());

        if (post.getParentId() != 0 && wpForoPostRepo.existsById((long) post.getParentId())) {
            if (wpForoPostRepo.findById((long) post.getParentId()).isPresent()) {
                json.put("parent", getSinglePostData(wpForoPostRepo.findById((long) post.getParentId()).get(), true));
            }
        }

        if(!isParent) {
            json.put("preRating", getRating(post));
        }

        return json;
    }

    private JSONObject getSinglePostData(WPWPForoPosts post) throws JSONException {
        return getSinglePostData(post, false);
    }

    private boolean isUserMailFake(WPWPForoPosts post) {
        return userRepo.getAllEmails().contains(post.getEmail()) && post.getUserId() == 0;
    }

    private String getRating(WPWPForoPosts post) {
        if(isUserMailFake(post)) return "bad";

        return "good";
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

}
