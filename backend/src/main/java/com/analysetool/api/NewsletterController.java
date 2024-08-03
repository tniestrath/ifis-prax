package com.analysetool.api;


import com.analysetool.services.NewsletterService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/newsletter", "/0wB4P2mly-xaRmeeDOj0_g/newsletter"}, method = RequestMethod.GET, produces = "application/json")
public class NewsletterController {

    @Autowired
    NewsletterService newsletterService;

    /**
     * Gets a Users status.
     * @param id the user's id.
     * @return the user's status, represented with a char.
     */
    @GetMapping("/getStatusById")
    public char getStatusById(Long id) {
        return newsletterService.getStatusById(id);
    }

    /**
     * Gets a Users status.
     * @param mail the user's mail-address.
     * @return the user's status, represented with a char.
     */
    @GetMapping("/getStatusByMail")
    public char getStatusByMail(String mail){
        return newsletterService.getStatusByMail(mail);
    }

    /**
     * Fetches all User-Status chars.
     * @return a list of chars.
     */
    @GetMapping("/getStatusAll")
    public List<Character> getStatusAll() {return newsletterService.getStatusAll();}

    /**
     * Fetches all mails that match a certain status.
     * @param c a char representing the status.
     * @return a List of email-addresses.
     */
    @GetMapping("/getMailByStatus")
    public List<String> getMailbyStatus(char c) {
        return newsletterService.getMailbyStatus(c);
    }

    /**
     * Fetches all Email-Addresses with their respective status.
     * @return a Map of String (email) to character (status).
     */
    @GetMapping("/getAllMailsWithStatus")
    public Map<String, Character> getAllMailsWithStatus() {
        return newsletterService.getAllMailsWithStatus();
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
    public long getAmountofSubsByDateRange(@RequestParam int daysBackFrom, @RequestParam int daysBackTo) {return newsletterService.getAmountofSubsByDateRange(daysBackFrom, daysBackTo);}

    /**
     * Fetches the amount of new subscribers since yesterday.
     * @return a List of Characters, corresponding to their confirmed-status.
     */
    @GetMapping("/getAmountOfSubsYesterday")
    public List<Character> getAmountofSubsYesterday() {return newsletterService.getAmountofSubsYesterday();}

    /**
     * Fetches the amount of subscribers since today at 00:00.
     * @return the number of new subscribers.
     */
    @GetMapping("/getAmountOfSubsToday")
    public Integer getAmountofSubsToday() {return newsletterService.getAmountofSubsToday();}

    /**
     * Berechnet die Konversionsrate für Newsletter-Abonnements am aktuellen Tag.
     * Die Konversionsrate wird als Verhältnis der Anzahl der Abonnements heute
     * zur Anzahl der einzigartigen Benutzer (nach Entfernung blockierter IPs) definiert.
     *
     * @return Die Konversionsrate für Newsletter-Abonnements heute in Prozent
     */
    @GetMapping("/getNewsletterKonversionRateToday")
    public double getConversionRateTodayForNewsletter(){return newsletterService.getConversionRateTodayForNewsletter();}

    /**
     * Fetches a detailed JSON-String containing a lot of data for the latest newsletter.
     * @return a JSON-String with keys: totalOpens, OR, subject, interactions, problems, interactionTimes, id, date.
     * @throws JSONException .
     */
    @GetMapping("/getLatestNewsletterCallup")
    public String getLatestNewsletterCallup() throws JSONException {return newsletterService.getLatestNewsletterCallup();}

    /**
     * Fetches a detailed JSON-String containing a lot of data for the chosen newsletter.
     * @param emailId the id of the chosen, sent newsletter.
     * @return a JSON-String with keys: totalOpens, OR, subject, interactions, problems, interactionTimes, id, date.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterCallup")
    public String getNewsletterCallup(int emailId) throws JSONException {return newsletterService.getNewsletterCallup(emailId);}

    /**
     * Fetches Geolocation-Data for a single Newsletter.
     * @param emailId id of the newsletter to fetch for.
     * @return a JSON-String with regional ISO-Codes as keys and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterGeoSingle")
    public String getNewsletterGeo(int emailId) throws JSONException {return newsletterService.getNewsletterGeo(emailId);}

    /**
     * Fetches Geolocation-Data for all Newsletters combined.
     * @return a JSON-String with regional ISO-Codes as keys and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterGeo")
    public String getNewsletterGeoTotal() throws JSONException {return newsletterService.getNewsletterGeoTotal();}

    /**
     * Fetches detailed, standard data of all Newsletters.
     * @return a JSON-String containing totalOpens, OR (OpenRate), problems, interactions and interactionTimes as keys.
     * Their respective values are calculated from ALL Newsletters.
     * @throws JSONException .
     */
    @GetMapping("/getNewsletterCallupGlobal")
    public String getNewsletterCallupGlobal() throws JSONException {return newsletterService.getNewsletterCallupGlobal();}


    /**
     * Fetches the percentage of ALL Newsletter-Mails that have been opened, compared to how many were sent.
     * @return a double, to be used as a percentage.
     */
    @GetMapping("/getGlobalOR")
    public double getGlobalOR() {return newsletterService.getGlobalOR();}

    /**
     * Fetches hourly Interactions of ALL Newsletters.
     * @return a JSON-String containing a 24-size JSON-Array.
     */
    @GetMapping("/getGlobalHourly")
    public String getGlobalHourly() {
        return newsletterService.getGlobalHourly();
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
    public String getNewsletterList(Integer page, Integer size) throws JSONException {return newsletterService.getNewsletterList(page, size);}


}
