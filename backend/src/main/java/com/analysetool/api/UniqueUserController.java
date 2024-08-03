package com.analysetool.api;

import com.analysetool.repositories.UniqueUserRepository;
import com.analysetool.services.UniqueUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping(value = {"/uniqueusers", "/0wB4P2mly-xaRmeeDOj0_g/uniqueusers"}, method = RequestMethod.GET, produces = "application/json")
public class UniqueUserController {

    @Autowired
    UniqueUserRepository uniqueUserRepo;
    @Autowired
    UniqueUserService uniqueUserService;


    /**
     * Fetch the average time spent by users in table.
     * @return a String representation of the average time spent.
     */
    @GetMapping("/average-time-spent")
    public String getAverageTimeSpent() {return uniqueUserService.getAverageTimeSpent();}


    /**
     * Fetch the average time spent by users in table today.
     * @return a String representation of the average time spent.
     */
    @GetMapping("/average-time-spent-today")
    public String getTodayAverageTimeSpent() {return uniqueUserService.getTodayAverageTimeSpent();}



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

    /**
     * Fetches all user click-paths.
     * @return a String representation of all user-click-paths.
     */
    @GetMapping("/all-paths")
    public String getAllUserPaths() {
        return uniqueUserService.getAllUserPaths();
    }

    /**
     * Fetch the bounce-rate in the current day.
     * @return a String representation of the bounce-rate today.
     */
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

    /**
     * Fetch click depths of users in segments, mapped to the amount of users in the depth-segment.
     * @return a JSON-Object mapping depth-segments to amount of users in depth-segment.
     */
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
