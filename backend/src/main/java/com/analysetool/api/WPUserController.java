package com.analysetool.api;
import com.analysetool.modells.WPUser;
import com.analysetool.repositories.WPUserRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@CrossOrigin
@RestController
@RequestMapping("/users")
public class WPUserController {

    @Autowired
    private WPUserRepository userRepository;

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
        }
    }



    // weitere REST-Endpunkte, falls benötigt
    @GetMapping("/profilePic")
    public File getProfilePic(@RequestParam long id){
        String path = "C:/Users/timni/"+id+"/profile_photo.jpg";
        File cutePic = new File(path);
        return cutePic;
    }

    @GetMapping("/getAllNew")
    public List<WPUser> getAllNew(){
        List<WPUser> list = userRepository.findAll();
        List<WPUser> li = new ArrayList<>();
        for(WPUser i : list){
            li.add(new WPUser(i.getId(), i.getEmail(), i.getDisplayName(), //getProfilePic(i.getId()).toString()
            "test"
            ));

        }
        return li;
    }

}
