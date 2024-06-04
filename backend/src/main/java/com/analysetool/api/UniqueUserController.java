package com.analysetool.api;

import com.analysetool.repositories.UniqueUserRepository;
import com.analysetool.services.UniqueUserService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @GetMapping("/getBounceRateToday")
    public String getBounceRate(){
        return String.valueOf(uniqueUserService.getBounceRateToday());
    }

    /**
     * Gliedert die Verweildauer aller nicht blockierten UniqueUser in definierte Zeitsegmente
     * und zählt die Anzahl der Benutzer in jedem Segment. Die Ergebnisse werden in einem JSON-Objekt gespeichert.
     *
     * @return Ein String im JSON-Format, der die Zeitsegmente als Schlüssel und die Anzahl der Benutzer als Werte enthält.
     *         Beispiel: {"0-10 Sekunden": 5, "11-30 Sekunden": 15, "31-60 Sekunden": 20, "61-180 Sekunden": 7, "181-600 Sekunden": 2, "601-1800 Sekunden": 1, "1800+ Sekunden": 0}
     */
    @GetMapping("/getTimeSpendInSegments")
    public String getTimeSpendInSegments(){
        try {
            return uniqueUserService.getSessionTimeInTimeSegments();
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler Mehler";
        }
    }


    @GetMapping("/getClickDepthSegmented")
    public String getClickDepthSegmented(){
        try {
            return uniqueUserService.getVisitorsDepthInSegments();
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler Mehler";
        }
    }

}
