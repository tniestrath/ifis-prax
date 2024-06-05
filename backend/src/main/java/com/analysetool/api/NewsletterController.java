package com.analysetool.api;

import com.analysetool.modells.Newsletter;
import com.analysetool.modells.NewsletterEmails;
import com.analysetool.modells.NewsletterStats;
import com.analysetool.repositories.*;
import com.analysetool.services.UniqueUserService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/newsletter", "/0wB4P2mly-xaRmeeDOj0_g/newsletter"})
public class NewsletterController {

    @Autowired
    NewsletterRepository newsRepo;
    @Autowired
    NewsletterEmailsRepository newsEmailsRepo;
    @Autowired
    NewsletterStatsRepository newsStatsRepo;
    @Autowired
    NewsletterSentRepository newsSentRepo;
    @Autowired
    universalStatsRepository uniRepo;
    @Autowired
    UniqueUserService uniqueUserService;
    @Autowired
    TrackingBlacklistRepository trackBlackRepo;
    /**
     * Gets a Users status.
     * @param id the user's id.
     * @return the user's status, represented with a char.
     */
    @GetMapping("/getStatusById")
    public char getStatusById(Long id) {
        return newsRepo.getStatusById(id);
    }

    /**
     * Gets a Users status.
     * @param mail the user's mail-address.
     * @return the user's status, represented with a char.
     */
    @GetMapping("/getStatusByMail")
    public char getStatusByMail(String mail){
        return newsRepo.getStatusByMail(mail);
    }

    /**
     * Fetches all User-Status chars.
     * @return a list of chars.
     */
    @GetMapping("/getStatusAll")
    public List<Character> getStatusAll () {
        return newsRepo.getStatusAll();
    }

    /**
     * Fetches all mails that match a certain status.
     * @param c a char representing the status.
     * @return a List of email-addresses.
     */
    @GetMapping("/getMailByStatus")
    public List<String> getMailbyStatus(char c) {
        return newsRepo.getMailsByStatus(c);
    }

    /**
     * Fetches all Email-Addresses with their respective status.
     * @return a Map of String (email) to character (status).
     */
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

    @GetMapping("/getAmountOfSubsToday")
    public Integer getAmountofSubsToday() {
        List<Character> subs = new ArrayList<>();
        List<Newsletter> allSubs = newsRepo.findAll();
        LocalDate today = LocalDate.now();

        for (Newsletter n : allSubs) {
            LocalDate createdDate = n.getCreated().toLocalDate();

            if (!createdDate.isBefore(today)) {
                subs.add(n.getStatus());

            }
        }
        return subs.size();
    }

    /**
     * Berechnet die Konversionsrate für Newsletter-Abonnements am aktuellen Tag.
     * Die Konversionsrate wird als Verhältnis der Anzahl der Abonnements heute
     * zur Anzahl der einzigartigen Benutzer (nach Entfernung blockierter IPs) definiert.
     *
     * @return Die Konversionsrate für Newsletter-Abonnements heute in Prozent
     */
    @GetMapping("/getNewsletterKonversionRateToday")
    public double getConversionRateTodayForNewsletter(){
        Integer subsToday = getAmountofSubsToday();
        List<String> blockedIps = trackBlackRepo.getAllIps();
        List<String> uniqueUsersIpsToday = uniqueUserService.getIpsToday();
        uniqueUsersIpsToday.removeAll(blockedIps);

        return (double)subsToday/uniqueUsersIpsToday.size();
    }

    /**
     * Fetches a detailed JSON-String containing a lot of data for the latest newsletter.
     * @return a JSON-String with keys: totalOpens, OR, subject, interactions, problems, interactionTimes, id, date.
     * @throws JSONException .
     */
    @GetMapping("/getLatestNewsletterCallup")
    public String getLatestNewsletterCallup() throws JSONException {
        return getNewsletterCallup(Math.toIntExact(newsEmailsRepo.getLatestNewsletter().getId()));
    }

    /**
     * Fetches a detailed JSON-String containing a lot of data for the chosen newsletter.
     * @param emailId the id of the chosen, sent newsletter.
     * @return a JSON-String with keys: totalOpens, OR, subject, interactions, problems, interactionTimes, id, date.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterCallup")
    public String getNewsletterCallup(int emailId) throws JSONException {
        if(newsEmailsRepo.findById((long) emailId).isPresent()) {
            JSONObject json = new JSONObject();
            json.put("totalOpens", newsSentRepo.getSumOpenedForEmail(emailId));
            if(newsSentRepo.getAmountOpenedBy(emailId).isPresent() && newsSentRepo.getAmountOpenedBy(emailId).get() > 0 && newsSentRepo.getAmountSentOfEmail(emailId).isPresent()) {
                json.put("OR", newsSentRepo.getAmountOpenedBy(emailId).get() / newsSentRepo.getAmountSentOfEmail(emailId).get());
            } else {
                json.put("OR", 0);
            }
            json.put("subject", newsEmailsRepo.findById((long) emailId).get().getSubject());
            json.put("interactions", newsStatsRepo.getCountInteractionsForEmail(String.valueOf(emailId)));
            json.put("problems", newsSentRepo.getAmountErrorsForEmail(emailId));

            List<Integer> hourlyInteractions = new ArrayList<>(Collections.nCopies(24, 0));
            for(NewsletterStats n : newsStatsRepo.getAllNewsletterStatsOfEmail(String.valueOf(emailId))) {
                int hour = n.getCreated().toLocalDateTime().getHour();
                if(hourlyInteractions.size() >= hour) {
                    hourlyInteractions.set(hour, hourlyInteractions.get(hour) + 1);
                } else {
                    hourlyInteractions.set(hour, 1);
                }
            }
            json.put("interactionTimes", new JSONArray(hourlyInteractions));
            json.put("id", emailId);
            json.put("date", newsEmailsRepo.findById((long) emailId).get().getCreated().toString());
            return json.toString();
        } else {
            return "email id invalid";
        }

    }

    /**
     * Fetches Geolocation-Data for a single Newsletter.
     * @param emailId id of the newsletter to fetch for.
     * @return a JSON-String with regional ISO-Codes as keys and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterGeoSingle")
    public String getNewsletterGeo(int emailId) throws JSONException {
       JSONObject json = new JSONObject();
       int total = 0;
       int totalDACH = 0;

       for(NewsletterStats n : newsStatsRepo.getAllNewsletterStatsOfEmail(String.valueOf(emailId))) {
           String country, county;
           country = IPHelper.getCountryName(n.getIp());
           county = IPHelper.getSubISO(n.getIp());
           total++;

           switch (country) {

               case "Belgium" -> {
                   try {
                       json.put("BG", json.getInt("BG") + 1);
                   } catch (JSONException e) {
                       json.put("BG",1);
                   }
                   totalDACH++;
               }
               case "Netherlands", "Switzerland", "Austria", "Luxembourg" -> {
                   String countryISO = IPHelper.getCountryISO(n.getIp()) == null ? "XD" : IPHelper.getCountryISO(n.getIp());
                   try {
                       json.put(countryISO, json.getInt(countryISO) + 1);
                   } catch (JSONException e) {
                       json.put(countryISO,1);
                   }
                   totalDACH++;
               }

               case "Germany" -> {
                   county = county == null ? "DX" : county;
                   try {
                       json.put(county, json.getInt(county) + 1);
                   } catch (JSONException e) {
                       json.put(county,1);
                   }
                   totalDACH++;
               }
               default -> {
                   try {
                       json.put("XX", json.getInt("XX") + 1);
                   } catch (JSONException e) {
                       json.put("XX",1);
                   }
               }
           }
       }
       json.put("total", total);
        json.put("totalDACH", totalDACH);
       return json.toString();

    }

    /**
     * Fetches Geolocation-Data for all Newsletters combined.
     * @return a JSON-String with regional ISO-Codes as keys and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterGeo")
    public String getNewsletterGeoTotal() throws JSONException {
        JSONObject json = new JSONObject();
        int total = 0;
        int totalDACH = 0;

        for(NewsletterStats n : newsStatsRepo.findAll()) {
            String country, county;
            country = IPHelper.getCountryName(n.getIp());
            county = IPHelper.getSubISO(n.getIp());
            total++;

            switch (country) {

                case "Belgium" -> {
                    try {
                        json.put("BG", json.getInt("BG") + 1);
                    } catch (JSONException e) {
                        json.put("BG",1);
                    }
                    totalDACH++;
                }
                case "Netherlands", "Switzerland", "Austria", "Luxembourg" -> {
                    String countryISO = IPHelper.getCountryISO(n.getIp()) == null ? "XD" : IPHelper.getCountryISO(n.getIp());
                    try {
                        json.put(countryISO, json.getInt(IPHelper.getCountryISO(n.getIp())) + 1);
                    } catch (JSONException e) {
                        json.put(countryISO,1);
                    }
                    totalDACH++;
                }
                case "Germany" -> {
                    county = county == null ? "DX" : county;
                    try {
                        json.put(county, json.getInt(county) + 1);
                    } catch (JSONException e) {
                        json.put(county,1);
                    }
                    totalDACH++;
                }
                default -> {
                    try {
                        json.put("XX", json.getInt("XX") + 1);
                    } catch (JSONException e) {
                        json.put("XX",1);
                    }
                }
            }
        }
        json.put("total", total);
        json.put("totalDACH", totalDACH);
        return json.toString();
    }

    /**
     * Fetches detailed, standard data of all Newsletters.
     * @return a JSON-String containing totalOpens, OR (OpenRate), problems, interactions and interactionTimes as keys.
     * Their respective values are calculated from ALL Newsletters.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterCallupGlobal")
    public String getNewsletterCallupGlobal() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("totalOpens", newsSentRepo.getSumOpened());
        if(newsSentRepo.getAmountOpenedTotal().isPresent() && newsSentRepo.getAmountOpenedTotal().get() > 0 && newsSentRepo.getAmountSent().isPresent()) {
            json.put("OR", newsSentRepo.getAmountOpenedTotal().get() / newsSentRepo.getAmountSent().get());
        } else {
            json.put("OR", 0);
        }

        json.put("problems", newsSentRepo.getAmountErrors());
        json.put("interactions", newsStatsRepo.getCountInteractions());
        json.put("interactionTimes", buildHourlyInteractions());

        return json.toString();
    }

    /**
     * Builds a List for ALL Newsletters, containing their Interaction in each hour in the day.
     * @return a 24-size list containing the number of interactions during the respective hour.
     */
    private List<Integer> buildHourlyInteractions() {
        List<Integer> hourlyInteractions = new ArrayList<>(Collections.nCopies(24, 0));
        for(NewsletterStats n : newsStatsRepo.findAll()) {
            int hour = n.getCreated().toLocalDateTime().getHour();
            if(hourlyInteractions.size() >= hour) {
                hourlyInteractions.set(hour, hourlyInteractions.get(hour) + 1);
            } else {
                hourlyInteractions.set(hour, 1);
            }
        }
        return hourlyInteractions;
    }

    /**
     * Fetches the percentage of ALL Newsletter-Mails that have been opened, compared to how many were sent.
     * @return a double, to be used as a percentage.
     */
    @GetMapping("/getGlobalOR")
    public double getGlobalOR() {
        if(newsSentRepo.getAmountOpenedTotal().isPresent() && newsSentRepo.getAmountOpenedTotal().get() > 0 && newsSentRepo.getAmountSent().isPresent()) {
            return newsSentRepo.getAmountOpenedTotal().get() / newsSentRepo.getAmountSent().get();
        } else {
            return 0;
        }
    }

    /**
     * Fetches hourly Interactions of ALL Newsletters.
     * @return a JSON-String containing a 24-size JSON-Array.
     */
    @GetMapping("/getGlobalHourly")
    public String getGlobalHourly() {
        return new JSONArray(buildHourlyInteractions()).toString();
    }

    /**
     * Fetches a page of Newsletters with their respective data.
     * @param page the page of the size you want
     * @param size the size of the page.
     * @return a JSON-String containing a JSON-Array of JSON-Objects.
     * Page 0 of size 5 would be 1-5, Page 1 of size 5 would be 6-10 and so on.
     * @throws JSONException .
     */
    @GetMapping("/getAll")
    public String getNewsletterList(Integer page, Integer size) throws JSONException {
        JSONArray array = new JSONArray();
        for(NewsletterEmails n : newsEmailsRepo.getAllSortedByDate(PageRequest.of(page, size))) {
            array.put(new JSONObject(getNewsletterCallup(Math.toIntExact(n.getId()))));
        }
        return array.toString();
    }


}
