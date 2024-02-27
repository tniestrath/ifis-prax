package com.analysetool.api;

import com.analysetool.modells.Newsletter;
import com.analysetool.modells.NewsletterEmails;
import com.analysetool.modells.NewsletterStats;
import com.analysetool.repositories.NewsletterEmailsRepository;
import com.analysetool.repositories.NewsletterRepository;
import com.analysetool.repositories.NewsletterSentRepository;
import com.analysetool.repositories.NewsletterStatsRepository;
import com.analysetool.util.IPHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/newsletter")
public class NewsletterController {

    @Autowired
    NewsletterRepository newsRepo;
    @Autowired
    NewsletterEmailsRepository newsEmailsRepo;
    @Autowired
    NewsletterStatsRepository newsStatsRepo;
    @Autowired
    NewsletterSentRepository newsSentRepo;

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

    @GetMapping("/getLatestNewsletterCallup")
    public String getLatestNewsletterCallup() throws JSONException {
        return getNewsletterCallup(Math.toIntExact(newsEmailsRepo.getLatestNewsletter().getId()));
    }

    @GetMapping("/getNewsletterCallup")
    public String getNewsletterCallup(int emailId) throws JSONException {
        if(newsEmailsRepo.findById((long) emailId).isPresent()) {
            JSONObject json = new JSONObject();
            json.put("totalOpens", newsSentRepo.getSumOpenedForEmail(emailId));
            json.put("OR", newsSentRepo.getAmountSentOfEmail(emailId) / newsSentRepo.getAmountOpenedBy(emailId));
            json.put("subject", newsEmailsRepo.findById((long) emailId).get().getSubject());
            json.put("interactions", newsStatsRepo.getCountInteractionsForEmail(String.valueOf(emailId)));
            json.put("problems", newsSentRepo.getAmountErrorsForEmail(emailId));

            List<Integer> hourlyInteractions = new ArrayList<>();
            for(NewsletterStats n : newsStatsRepo.getAllNewsletterStatsOfEmail(String.valueOf(emailId))) {
                int hour = n.getCreated().toLocalDateTime().getHour();
                if(hourlyInteractions.size() >= hour) {
                    hourlyInteractions.set(hour, hourlyInteractions.get(hour));
                } else {
                    hourlyInteractions.set(hour, 1);
                }
            }
            json.put("interactionTimes", hourlyInteractions);

            return json.toString();
        } else {
            return "email id invalid";
        }

    }

    @GetMapping("/getNewsletterGeoSingle")
    public String getNewsletterGeo(int emailId) throws JSONException {
       JSONObject json = new JSONObject();

       for(NewsletterStats n : newsStatsRepo.getAllNewsletterStatsOfEmail(String.valueOf(emailId))) {
           String country, county;
           country = IPHelper.getCountryName(n.getIp());
           county = IPHelper.getSubISO(n.getIp());

           switch (country) {

               case "Belgium" -> {
                   try {
                       json.put("BG", json.getInt("BG") + 1);
                   } catch (JSONException e) {
                       json.put("BG",1);
                   }
               }
               case "Netherlands", "Switzerland", "Austria", "Luxembourg" -> {
                   try {
                       json.put(IPHelper.getCountryISO(n.getIp()), json.getInt(IPHelper.getCountryISO(n.getIp()) + 1));
                   } catch (JSONException e) {
                       json.put(IPHelper.getCountryISO(n.getIp()),1);
                   }
               }

               default -> {
                   try {
                       json.put(county, json.getInt(county) + 1);
                   } catch (JSONException e) {
                       json.put(county,1);
                   }
               }
           }
       }
       return json.toString();

    }

    @GetMapping("/getNewsletterGeo")
    public String getNewsletterGeoTotal() throws JSONException {
        JSONObject json = new JSONObject();

        for(NewsletterStats n : newsStatsRepo.findAll()) {
            String country, county;
            country = IPHelper.getCountryName(n.getIp());
            county = IPHelper.getSubISO(n.getIp());

            switch (country) {

                case "Belgium" -> {
                    try {
                        json.put("BG", json.getInt("BG") + 1);
                    } catch (JSONException e) {
                        json.put("BG",1);
                    }
                }
                case "Netherlands", "Switzerland", "Austria", "Luxembourg" -> {
                    try {
                        json.put(IPHelper.getCountryISO(n.getIp()), json.getInt(IPHelper.getCountryISO(n.getIp()) + 1));
                    } catch (JSONException e) {
                        json.put(IPHelper.getCountryISO(n.getIp()),1);
                    }
                }

                default -> {
                    try {
                        json.put(county, json.getInt(county) + 1);
                    } catch (JSONException e) {
                        json.put(county,1);
                    }
                }
            }
        }
        return json.toString();
    }

    @GetMapping("/getNewsletterList")
    public String getNewsletterList(int page, int size) throws JSONException {
        JSONArray array = new JSONArray();
        for(NewsletterEmails n : newsEmailsRepo.getAllSortedByDate(PageRequest.of(page, size))) {
            array.put(new JSONObject(getNewsletterCallup(Math.toIntExact(n.getId()))));
        }
        return array.toString();
    }


}
