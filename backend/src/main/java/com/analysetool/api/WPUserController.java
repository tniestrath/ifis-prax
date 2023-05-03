package com.analysetool.api;
import com.analysetool.modells.WPUser;
import com.analysetool.repositories.WPUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
