package com.analysetool.api;

import com.analysetool.services.UniService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value =  {"/bericht", "/0wB4P2mly-xaRmeeDOj0_g/bericht"}, method = RequestMethod.GET, produces = "application/json")
public class UniStatController {

    @Autowired
    UniService uniService;

    /**
     *
     * @return gibt aus, wie viele Admins es aktuell gibt.
     */
    public int getAdminCount() {return uniService.getAdminCount();}

    /**
     *
     * @param days  if 1, get hourly stats (for the last 24 hours) - if higher get Daily.
     * @return a JSON-String containing a lot of universal-stat data.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping(value = "/callups")
    public String getCallupsByTime(@RequestParam() int days) throws JSONException, ParseException {return uniService.getCallupsByTime(days);}

    /**
     *
     * @return ein HTML Code, der eine Tabelle mit Statistiken enthält.
     * @throws JSONException .
     */
    @GetMapping(value = "/today", produces = MediaType.TEXT_HTML_VALUE)
    public String getLetzte() throws JSONException {return uniService.getLetzte();}


    /**
     *
     * @param date : das Datum, für das die UniStatsNach Kategorie ausgegeben werden sollen.
     * @param hour : die Stunde, für die ausgegeben werden sollen.
     * @return eine Liste der Anzahl von Clicks und Besucher nach Category des letzten abgeschlossenen Tages.
     *
     */
    @GetMapping("getCallupByCategoryDateAndHour")
    public String getCallupByCategoryHourly(String date, int hour) throws ParseException, JSONException {return uniService.getCallupByCategoryHourly(date, hour);}

    /**
     *
     * @param date : Das Datum, für die zusammengefasste Stats ausgegeben werden sollen.
     * @return eine Liste der Anzahl von Clicks und Besucher nach Category des letzten abgeschlossenen Tages.
     *
     */
    @GetMapping("getCallupByCategoryDate")
    public String getCallupByCategoryDaily(String date) throws ParseException, JSONException {return uniService.getCallupByCategoryDaily(date);}

    /**
     * Fetches a callup of all categories since beginning of tracking.
     * @return a JSON-String containing categories, clicks in the category and visitors in the category.
     * @throws JSONException -
     */
    @GetMapping("getCallupByCategoryAllTime")
    public String getCallupByCategoryAllTime() throws JSONException {return uniService.getCallupByCategoryAllTime();}

    /**
     * Fetch detailed data for Ratgeber views.
     * @param date the date to fetch details for.
     * @return a JSON-String containing detailed Ratgeber_Views.
     * @throws ParseException .
     * @throws JSONException .
     */
    @GetMapping("/getRatgeberDetailedDaily")
    public String getRatgeberDetailedByDate(String date) throws ParseException, JSONException {return uniService.getRatgeberDetailedByDate(date);}

    /**
     * Fetch detailed data for Ratgeber views since beginning of tracking.
     * @return a JSON-String containing detailed Ratgeber_Views since beginning of tracking.
     * @throws JSONException .
     */
    @GetMapping("/getRatgeberDetailedAllTime")
    public String getRatgeberDetailedAllTime() throws JSONException {return uniService.getRatgeberDetailedAllTime();}

    /**
     *
     * @return eine HTML-Seite, die den Bericht wie in der Methode getLetzte, nur für die letzten 7 Tage erstellt.
     * @throws JSONException .
     */
    @GetMapping(value = "/letzte7Tage", produces = MediaType.TEXT_HTML_VALUE)
    public String getLast7Days() throws JSONException {return uniService.getLast7Days();}

    /**
     *
     * @param id  die ID des Posts, für den die Clicks ermittelt werden sollen.
     * @param daysBack wie viele Tage zurückgeschaut werden soll.
     * @return  JSONObject, dass die ID des Posts, den Namen und die Clicks des gewünschten Tages enthält.
     * @throws JSONException .
     */
    public JSONObject getClickOfDayAsJson(long id,int daysBack) throws JSONException {return uniService.getClickOfDayAsJson(id, daysBack);}

    /**
     *
     * @param type der Typ Post, für den eine Top5 erstellt werden soll ("blog" | "artikel" | "news" | "whitepaper" | "podcast" | "videos")
     * @param daysBack how many days to look back.
     * @return a JSON String.
     * @throws JSONException .
     */
    @GetMapping("/getTop5ByClicksAndDaysBackAndType")
    public String getTop5ByClicks(@RequestParam String type, @RequestParam int daysBack) throws JSONException {return uniService.getTop5ByClicks(type, daysBack);}

    /**
     * Fetch posts mapped by their type.
     * @return a JSON-String mapping posts to their types.
     */
    @GetMapping("/getPostsByType")
    public String getPostsByType() throws JSONException {return uniService.getPostsByType();}

    /**
     * Fetch posts mapped by their type for yesterday.
     * @return a JSON-String mapping posts to their types.
     */
    @GetMapping("/getPostsByTypeYesterday")
    public String getPostsByTypeYesterday() throws JSONException {return uniService.getPostsByTypeYesterday();}


    /**
     * Calculates and returns the conversion rate for non-subscribers.
     *
     * @return the conversion rate for non-subscribers as a double. It's calculated by subtracting the number of non-subscribers yesterday from today, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRateNoSub")
    public double getConversionRateNoSub(){return uniService.getConversionRateNoSub();}

    /**
     * Calculates and returns the conversion rate for basic subscribers.
     *
     * @return the conversion rate for basic subscribers as a double. It's determined by the difference in basic subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRateBasicSub")
    public double getConversionRateBasicSub(){return uniService.getConversionRateBasicSub();}

    /**
     * Calculates and returns the conversion rate for basic plus subscribers.
     *
     * @return the conversion rate for basic plus subscribers as a double. It's computed by the difference in basic plus subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRateBasicPlusSub")
    public double getConversionRateBasicPlusSub(){return uniService.getConversionRateBasicPlusSub();}


    /**
     * Calculates and returns the conversion rate for plus subscribers.
     *
     * @return the conversion rate for plus subscribers as a double. This rate is calculated by subtracting yesterday's plus subscriber count from today's, divided by the count of today's unique, non-blocked IPs.
     */
    @GetMapping("/getConversionRatePlusSub")
    public double getConversionRatePlusSub(){return uniService.getConversionRatePlusSub();}

    /**
     * Calculates and returns the conversion rate for premium subscribers.
     *
     * @return the conversion rate for premium subscribers as a double. It's the difference in premium subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRatePremiumSub")
    public double getConversionRatePremiumSub(){return uniService.getConversionRatePremiumSub();}

    /**
     * Calculates and returns the conversion rate for premium sponsor subscribers.
     *
     * @return the conversion rate for premium sponsor subscribers as a double. This is based on the difference in premium sponsor subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRatePremiumSponsorSub")
    public double getConversionRatePremiumSponsorSub(){return uniService.getConversionRatePremiumSponsorSub();}

    /**
     * Calculates and returns the overall conversion rate across all subscription types.
     *
     * @return the overall conversion rate as a double.
     */
    @GetMapping("/getTotalConversionRateMembership")
    public double getTotalConversionRateMembership() {return uniService.getTotalConversionRateMembership();}

    /**
     * Returns a JSON string representing the ranking of subscription types by their conversion rates.
     * Each subscription type is represented as a JSON object with its name, rate, and rank.
     * These objects are added to a JSON array, which is then converted to a string.
     *
     * @return A JSON string that represents the conversion rate ranking of subscription types.
     */
    @GetMapping("/getSubscriptionRateRanking")
    public String getSubscriptionRateRanking() throws JSONException {return uniService.getSubscriptionRateRanking();}


    /**
     * Retrieves the hourly server error ranking for a given universal statistic ID.
     *
     * @param uniStatId the universal statistic ID for which the server error ranking is to be retrieved
     * @return a JSON string containing the hourly server error ranking
     */
    @GetMapping("/server-error-ranking")
    public String getHourlyServerErrorRanking(@RequestParam int uniStatId) {return uniService.getHourlyServerErrorRanking(uniStatId);}

    /**
     * Retrieves the hourly server error ranking for today.
     * @return a JSON string containing the hourly server error ranking
     */
    @GetMapping("/server-error-ranking-today")
    public String getHourlyServerErrorRankingToday() {return uniService.getHourlyServerErrorRankingToday();}

    /**
     * Retrieves the error rate for the current day.
     *
     * @return a JSON string containing the total clicks, total errors, and error rate for the current day
     */
    @GetMapping("/error-rate-today")
    public String getErrorRateForToday() {return uniService.getErrorRateForToday();}

    @GetMapping("/bounce")
    public String getBounce() throws JSONException {return uniService.getBounce();}

}
