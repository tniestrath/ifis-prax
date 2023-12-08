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



/*
    @GetMapping("/average-time-spent-range")
    public String getAverageTimeSpentInRange(@RequestParam("daysBackFrom") int daysBackFrom, @RequestParam("daysBackTo") int daysBackTo) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(daysBackFrom).toLocalDate().atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now().minusDays(daysBackTo).toLocalDate().atTime(23, 59, 59);

        Double averageTimeSpent = uniqueUserRepo.getAverageTimeSpentBetweenDates(startDate, endDate);
        return averageTimeSpent != null ? String.format("%.2f", averageTimeSpent) : "Daten nicht verfügbar";
    }
*/

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
        Pageable topLimit = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        List<UniqueUser> users = uniqueUserRepo.findTopByMoreThanTwoClicks(topLimit);

        return users.stream()
                .map(user -> uniqueUserService.reconstructClickPath(user))
                .collect(Collectors.joining(", "));
    }

    @GetMapping("/all-paths")
    public String getAllUserPaths() {
        List<UniqueUser> users = uniqueUserRepo.findAllByMoreThanTwoClicks();

        return users.stream()
                .map(user -> uniqueUserService.reconstructClickPath(user))
                .collect(Collectors.joining(", "));
    }


    /**
     * Retrieves a list of users who are potentially bots based on their click patterns.
     *
     * This method scans through all users in the database and applies a filtering logic to identify
     * potential bots. A user is considered a potential bot if their click behavior shows a pattern of
     * repeatedly clicking on the same category in a short sequence, e.g., 'main, main, main...'.
     * The method uses a threshold of three consecutive clicks on the same category as a criterion for
     * suspicious behavior, which can be adjusted as per requirements.
     *
     * @return List<UniqueUser> containing users who are identified as potential bots based on the defined criteria.
     */
    @GetMapping("/possible-bots")
    public List<UniqueUser> getPossibleBots() {
        List<UniqueUser> users = uniqueUserRepo.findAll();
        return users.stream()
                .filter(uniqueUserService::isPotentialBot)
                .collect(Collectors.toList());
    }


}
