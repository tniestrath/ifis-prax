package com.analysetool.api;

import com.analysetool.modells.UniqueUser;
import com.analysetool.repositories.UniqueUserRepository;
import com.analysetool.services.UniqueUserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping("/uniqueusers")
public class UniqueUserController {

    @Autowired
    UniqueUserRepository uniqueUserRepo;
    @Autowired
    UniqueUserService uniqueUserService;


    // Endpoint, um die durchschnittliche Verweildauer aller Nutzer als String zurückzugeben
    @GetMapping("/average-time-spent")
    public String getAverageTimeSpent() {
        Double averageTimeSpent = uniqueUserRepo.getAverageTimeSpent();
        return averageTimeSpent != null ? String.format("%.2f", averageTimeSpent) : "Daten nicht verfügbar";
    }


    // Endpoint, um die durchschnittliche Verweildauer der Nutzer für den heutigen Tag als String zurückzugeben
    @GetMapping("/average-time-spent-today")
    public String getTodayAverageTimeSpent() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        Double todayAverageTimeSpent = uniqueUserRepo.getAverageTimeSpentBetweenDates(startOfDay, endOfDay);
        return todayAverageTimeSpent != null ? String.format("%.2f", todayAverageTimeSpent) : "Daten nicht verfügbar";
    }



    /**
     * Retrieves the click paths of users who have more than two clicks.
     * This method returns the click paths as a single string, with each path separated by commas.
     * Each path represents the sequence of categories clicked by a user.
     * The number of users' paths returned is limited by the provided 'limit' parameter.
     *
     * @param limit The maximum number of user paths to return. If not specified, defaults to 10.
     *              This limits the number of users to be considered for generating click paths.
     * @return A String containing the click paths of users, separated by commas. -> "main,blog,news"
     *         Each click path is a sequence of category names representing the order of clicks.
     *         Returns an empty string if no valid paths are found or if an error occurs.
     */
    @GetMapping("/paths")
    public String getUserPaths(@RequestParam(defaultValue = "10") int limit) {
        return uniqueUserService.getUserPaths(limit);
    }

    @GetMapping("/all-paths")
    public String getAllUserPaths() {
        return uniqueUserService.getAllUserPaths();
    }





}
