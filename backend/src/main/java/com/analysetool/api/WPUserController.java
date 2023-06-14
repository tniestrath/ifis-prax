package com.analysetool.api;
import com.analysetool.Application;
import com.analysetool.modells.WPUser;
import com.analysetool.modells.UserStats;
import com.analysetool.repositories.WPUserRepository;
import com.analysetool.repositories.UserStatsRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.analysetool.modells.userWp;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@CrossOrigin
@RestController
@RequestMapping("/users")
public class WPUserController {

    @Autowired
    private WPUserRepository userRepository;
    @Autowired
    private UserStatsRepository userStatsRepo;
    @Autowired
    private statsController StatsController;
/*
    @GetMapping("/{id}")
    public ResponseEntity<WPUser> getUserById(@PathVariable Long id) {
        Optional<WPUser> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/getAll")
    public List<WPUser> getAll(){return userRepository.findAll();}

    @GetMapping(params = "login")
    public ResponseEntity<WPUser> getUserByLogin(@RequestParam String login) {
        Optional<WPUser> user = userRepository.findByLogin(login);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(params = "email")
    public ResponseEntity<WPUser> getUserByEmail(@RequestParam String email) {
        Optional<WPUser> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }*/




    // weitere REST-Endpunkte, falls benötigt
    /*@GetMapping("/profilePic")
    public File getProfilePic(@RequestParam long id){
        String path = "../../assets/user_img/"+id+"_profile_photo.jpg";
        File cutePic = new File(path);
        return cutePic;
    }

    @GetMapping("/getAllNew")
    public List<userWp> getAllNew(){
        List<WPUser> list = userRepository.findAll();
        List<userWp> li = new ArrayList<>();
        for(WPUser i : list){
            li.add(new userWp(i.getId(), i.getEmail(), i.getDisplayName(), getProfilePic(i.getId()).toString()
            //"test"
            ));

        }
        return li;
    }*/


    @GetMapping("/getAllNew")
    public String getAllNew() throws IOException, URISyntaxException, JSONException {
        List<WPUser> list = userRepository.findAll();

        JSONArray response = new JSONArray();
        //JSONObject obj = new JSONObject();
        for (WPUser i : list) {
            JSONObject obj = new JSONObject();
            if(userStatsRepo.existsByUserId(i.getId())){
                UserStats statsUser = userStatsRepo.findByUserId(i.getId());
                obj.put("id",i.getId());
                obj.put("email",i.getEmail());
                obj.put("displayName",i.getDisplayName());
                obj.put( "accountType" ,"extra Premium ultra User");
                obj.put("profileViews ", statsUser.getProfileView());
                obj.put("postViews",StatsController.getViewsOfUserById(i.getId()));
                obj.put ("performance",statsUser.getAveragePerformance());

            }
            else {obj.put("id",i.getId());
                obj.put("email",i.getEmail());
                obj.put("displayName",i.getDisplayName());
                obj.put( "accountType" ,"extra Premium ultra User");
                obj.put("profileViews ", 0);
                obj.put("postViews",0);
                obj.put ("performance",0);

            }
            response.put(obj);
        }
        return response.toString();
    }

    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) throws IOException, URISyntaxException {
        String path = Paths.get(Application.class.getClassLoader().getResource("user_img").toURI()).toString() +"/"+id+"_profile_photo.jpg";
        File cutePic = new File(path);
        if (!cutePic.exists()) {
            return ResponseEntity.notFound().build();
        }
        byte[] imageBytes = Files.readAllBytes(cutePic.toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

}
