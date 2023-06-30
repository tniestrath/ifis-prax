package com.analysetool.api;
import com.analysetool.Application;
import com.analysetool.modells.WPUser;
import com.analysetool.modells.UserStats;
import com.analysetool.repositories.WPUserMetaRepository;
import com.analysetool.repositories.WPUserRepository;
import com.analysetool.repositories.UserStatsRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private WPUserRepository userRepository;
    @Autowired
    private UserStatsRepository userStatsRepository;
    @Autowired
    private PostController StatsController;
    @Autowired
    private WPUserMetaRepository wpUserMetaRepository;




    @GetMapping("/getAllNew")
    public String getAllNew() throws JSONException {
        List<WPUser> list = userRepository.findAll();

        JSONArray response = new JSONArray();
        for (WPUser i : list) {
            JSONObject obj = new JSONObject();
            if(userStatsRepository.existsByUserId(i.getId())){
                UserStats statsUser = userStatsRepository.findByUserId(i.getId());
                obj.put("id",i.getId());
                obj.put("email",i.getEmail());
                obj.put("displayName",i.getDisplayName());
                obj.put("profileViews ", statsUser.getProfileView());
                obj.put("postViews",StatsController.getViewsOfUserById(i.getId()));
                obj.put("postCount",StatsController.getPostCountOfUserById(i.getId()));
                obj.put ("performance",statsUser.getAveragePerformance());

            }
            if (wpUserMetaRepository.existsByUserId(i.getId())){
                String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId(i.getId());
                if (wpUserMeta.contains("customer")) obj.put("accountType", "?customer?");
                if (wpUserMeta.contains("administrator")) obj.put("accountType", "admin");
                if (wpUserMeta.contains("anbieter")) obj.put("accountType", "basic");
                if (wpUserMeta.contains("plus-anbieter")) obj.put("accountType", "plus");
                if (wpUserMeta.contains("premium-anbieter")) obj.put("accountType", "premium");
            }
            else {obj.put("id",i.getId());
                obj.put("email",i.getEmail());
                obj.put("displayName",i.getDisplayName());
                obj.put( "accountType" ,"undefined");
                obj.put("profileViews ", 0);
                obj.put("postViews",0);
                obj.put("postCount",0);
                obj.put ("performance",0);

            }
            response.put(obj);
        }
        return response.toString();
    }

    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) throws IOException, URISyntaxException {
        String path = Paths.get(Objects.requireNonNull(Application.class.getClassLoader().getResource("user_img")).toURI()).toString() +"/"+id+"_profile_photo.jpg";
        File cutePic = new File(path);
        if (!cutePic.exists()) {
            cutePic = new File(Paths.get(Objects.requireNonNull(Application.class.getClassLoader().getResource("user_img/404_img.jpg")).toURI()).toUri());
        }
        byte[] imageBytes = Files.readAllBytes(cutePic.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    //STATS



    @GetMapping("/{userId}")
    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userStatsRepository.findByUserId(userId);
    }

    @GetMapping("/getUserStats")
    public String getUserStat(@RequestParam Long id) throws JSONException {
        JSONObject obj = new JSONObject();
        UserStats user = userStatsRepository.findByUserId(id);
        obj.put("Interaktionsrate",user.getInteractionRate());
        obj.put("Average Performance",user.getAveragePerformance());
        obj.put("Average Relevance",user.getAverageRelevance());
        obj.put("Postfrequenz",user.getPostFrequence());
        obj.put("Profilaufrufe",user.getProfileView());
        return obj.toString();
    }

}
