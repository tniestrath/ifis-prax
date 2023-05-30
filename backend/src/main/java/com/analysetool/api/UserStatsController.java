package com.analysetool.api;

import com.analysetool.modells.UserStats;
import com.analysetool.modells.WPUser;
import com.analysetool.repositories.UserStatsRepository;
import com.analysetool.repositories.WPUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-stats")
public class UserStatsController {

    private final UserStatsRepository userStatsRepository;
    private final WPUserRepository wpUserRepository;

    @Autowired
    public UserStatsController(UserStatsRepository userStatsRepository, WPUserRepository wpUserRepository) {
        this.userStatsRepository = userStatsRepository;
        this.wpUserRepository = wpUserRepository;
    }

    @GetMapping("/{userId}")
    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userStatsRepository.findByUserId(userId);
    }
/*
    @PostMapping("/{userId}/update")
    public UserStats updateUserStats(@PathVariable("userId") Long userId) {
        WPUser user = wpUserRepository.findById(userId).orElse(null);
        if (user != null) {
            if (userStatsRepository.existsByUserId(userId)) {
                UserStats stats = userStatsRepository.findByUserId(userId);
                // Update the stats as per your requirements
                // ...
                return userStatsRepository.save(stats);
            } else {
                UserStats stats = new UserStats();
                stats.setUser(user);
                // Set initial stats values
                // ...
                return userStatsRepository.save(stats);
            }
        }
        return null;
    }*/
}

