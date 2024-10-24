package com.analysetool.api;

import com.analysetool.modells.Newsletter;
import com.analysetool.repositories.NewsletterRepository;
import com.analysetool.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/newsletter")
public class NewsletterController {

    @Autowired
    NewsletterRepository newsRepo;

    String viewsPerHourString = "{\"0\":0,\"1\":0,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0,\"13\":0,\"14\":0,\"15\":0,\"16\":0,\"17\":0,\"18\":0,\"19\":0,\"20\":0,\"21\":0,\"22\":0,\"23\":0}";
    public Map<String,Long> setJson(){
        String temp ="";
        //temp = viewsPerDay.substring(1, viewsPerDay.length() - 1);
        temp = viewsPerHourString.substring(1, viewsPerHourString.length() - 1);
        // Teile den String an den Kommas auf, um die einzelnen Schlüssel-Wert-Paare zu erhalten
        String[] keyValuePairs = temp.split(",");

        // Erstelle eine HashMap, um die Schlüssel-Wert-Paare zu speichern
        HashMap<String, Long> map = new HashMap<>();

        // Iteriere über die einzelnen Schlüssel-Wert-Paare und füge sie der HashMap hinzu
        for (String pair : keyValuePairs) {
            // Teile den Schlüssel-Wert-Paar-String an den Doppelpunkten auf
            String[] entry = pair.split(":");

            // Entferne führende und abschließende Anführungszeichen von Schlüssel und Wert
            String key = entry[0].trim().replaceAll("\"", "");
            long value = Long.parseLong(entry[1].trim());

            // Füge das Schlüssel-Wert-Paar der HashMap hinzu
            map.put(key, value);
        }
        return (Map) map;
    }
    @GetMapping("/getStatusById")
    public char getStatusById(Long id) {
        return newsRepo.getStatusById(id);
    }

    @GetMapping("/getStatusByMail")
    public char getStatusByMail(String mail){
        return newsRepo.getStatusByMail(mail);
    }

    @GetMapping("/getStatusAll")
    public List<Character> getStatusAll () {
        return newsRepo.getStatusAll();
    }

    @GetMapping("/getMailByStatus")
    public List<String> getMailbyStatus(char c) {
        return newsRepo.getMailsByStatus(c);
    }

    @GetMapping("/getAllMailsWithStatus")
    public Map<String, Character> getAllMailsWithStatus() {
        return newsRepo.getMailAndStatusAll();
    }


    /**
     * Retrieves the distribution of subscriptions by hour within a specific date range.
     *
     * <p>This method fetches all subscribers from the Newsletter repository and filters them
     * based on their creation dates. Subscribers whose creation date falls within the specified
     * date range are then processed to count the number of subscriptions per hour.</p>
     *
     * <p>The date range is defined by two parameters that specify the number of days back from today:
     * <ul>
     *     <li><code>daysBackTo</code> sets the start of the date range, with a higher numerical value
     *     indicating a date further in the past.</li>
     *     <li><code>daysBackFrom</code> sets the end of the date range, with a lower numerical value
     *     indicating a date closer to today.</li>
     * </ul>
     * Both boundary dates are inclusive.</p>
     *
     * @param daysBackTo The end of the date range, specified as the number of days back from today.
     * @param daysBackFrom The start of the date range, specified as the number of days back from today.
     * @return A string representation of the distribution of subscriptions by hour.
     */
    @GetMapping("/getTimesOfSubsByDateRange")
    public String getTimesOfSubsByDateRange(@RequestParam int daysBackFrom, @RequestParam int daysBackTo) {
        if (daysBackTo < daysBackFrom) {
            throw new IllegalArgumentException("daysBackTo should be greater or equal to daysBackFrom");
        }

        Map<String, Long> subsPerHour = setJson();
        List<Newsletter> allSubs = newsRepo.findAll();

        LocalDate fromDate = LocalDate.now().minusDays(daysBackFrom); // Startdatum
        LocalDate toDate = LocalDate.now().minusDays(daysBackTo); // Enddatum

        for (Newsletter n : allSubs) {
            if (n.getCreated() == null) {
                continue;
            }

            LocalDate createdDate = n.getCreated().atZone(ZoneId.systemDefault()).toLocalDate();

            if (!createdDate.isBefore(fromDate) && !createdDate.isAfter(toDate)) {
                subsPerHour = LogService.erhoeheViewsPerHour2(subsPerHour, n.getCreated().atZone(ZoneId.systemDefault()).toLocalTime());
            }
        }

        return subsPerHour.toString();
    }

    /**
     * Fetches the number of subscriptions in a given date range.
     *
     * <p>This method fetches all newsletter subscriptions from the database and filters
     * them by their creation date to count the number of subscriptions between two specific dates.</p>
     *
     * @param daysBackTo The number of days from today to the nearest date back in time for which to consider subscriptions.
     *                   E.g., if today is 2021-09-14 and daysBackTo is 1, then the furthest date back will be 2021-09-13.
     *
     * @param daysBackFrom The number of days from today to the furthest date back in time for which to consider subscriptions.
     *                     E.g., if today is 2021-09-14 and daysBackFrom is 10, then the nearest date will be 2021-09-04.
     *
     * @return A long value representing the number of subscriptions between the fromDate and toDate, inclusive.
     *
     * @throws IllegalArgumentException (if applicable)
     *
     * Example usage:
     * <pre>{@code
     *   long numberOfSubs = getAmountofSubsByDateRange(10, 1);
     * }</pre>
     */
    @GetMapping("/getAmountOfSubsByDateRange")
    public long getAmountofSubsByDateRange(@RequestParam int daysBackFrom, @RequestParam int daysBackTo) {
        if (daysBackTo < daysBackFrom) {
            throw new IllegalArgumentException("daysBackTo should be greater or equal to daysBackFrom");
        }

        LocalDate fromDate = LocalDate.now().minusDays(daysBackFrom); // Startdatum
        LocalDate toDate = LocalDate.now().minusDays(daysBackTo); // Enddatum


        long counter = 0;
        List<Newsletter> allSubs = newsRepo.findAll();
        for (Newsletter n : allSubs) {
            if (n.getCreated() == null) {
                continue;
            }
            LocalDate createdDate = n.getCreated().atZone(ZoneId.systemDefault()).toLocalDate();

            if (!createdDate.isBefore(fromDate) && !createdDate.isAfter(toDate)) {
                counter++;
            }
        }
        return counter;
    }

    @GetMapping("/getAmountOfSubsYesterday")
    public List<Character> getAmountofSubsYesterday() {
        List<Character> subs = new ArrayList<>();
        List<Newsletter> allSubs = newsRepo.findAll();
        LocalDate today = LocalDate.now();

        for (Newsletter n : allSubs) {
            LocalDate createdDate = n.getCreated().toLocalDate();

            if (createdDate.isBefore(today)) {
                subs.add(n.getStatus());

            }
        }
        return subs;
    }


}
