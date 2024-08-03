package com.analysetool.api;


import com.analysetool.services.GeoService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/geo", "/0wB4P2mly-xaRmeeDOj0_g/geo"}, method = RequestMethod.GET, produces = "application/json")
public class GeoController {

    @Autowired
    GeoService geoService;

    /**
     * @param date       ein Datum im Format yyyy-MM-dd.
     * @param bundesland der ISO-Code eines Bundeslands.
     * @return Anzahl der Clicks aus diesem Bundesland am angegebenen Tag.
     * @throws ParseException Falls Datum falsches Format. Schande.
     */
    @GetMapping("/getClicksByDayAndBundesland")
    public int getClicksByDayAndBundesland(String date, String bundesland) throws ParseException {return geoService.getClicksByDayAndBundesland(date, bundesland);}

    /**
     * @param date    ein Datum im Format yyyy-MM-dd.
     * @param country der ISO-Code eines Bundeslands.
     * @return Anzahl der Clicks aus diesem Bundesland am angegebenen Tag.
     * @throws ParseException Falls Datum falsches Format. Schande.
     */
    @GetMapping("/getClicksByDayAndCountry")
    public int getClicksByDayAndCountry(String date, String country) throws ParseException {return geoService.getClicksByDayAndCountry(date, country);}

    /**
     * Endpoint for retrieval of a Post Geolocation-stats by their id, only using data gathered between Date-start and Date-end.
     * @param id the posts id you want stats for.
     * @param start a String containing an ISO-Format Date, marking the starting date (inclusive).
     * @param end a String containing an ISO-Format Date, marking the ending date. (inclusive)
     * @return a List of Geolocation Data in a specific order, marked for change.
     */
    @GetMapping("/getPostGeoByIDAndDay")
    public List<Integer> getPostGeoByIDAndDay(long id, String start, String end) {return geoService.getPostGeoByIDAndDay(id, start, end);}

    /**
     * Endpoint for retrieval of all Global Geolocation Data gathered between the two Dates for the DACH region.
     * @param start a String representing the start date of calculation format: YYYY-MM-DD (inclusive).
     * @param end   a String representing the end date of calculation format: YYYY-MM-DD, (inclusive).
     * @return a json-string containing the clicks of each bundesland and adjacent country of interest, labeled by their ISO-Code for bundesland and english name for countries.
     * @throws JSONException if something unexpected happened.
     */
    @GetMapping("/getTotalGermanGeoByDay")
    public String getTotalGermanGeoByDay(String start, String end) throws JSONException {return geoService.getTotalGermanGeoByDay(start, end);}

    /**
     * Endpoint for retrieval of all Global Geolocation Data gathered for the DACH region.
     * @return a json-string containing the clicks of each bundesland and adjacent country of interest, labeled by their ISO-Code for bundesland and english name for countries.
     * @throws JSONException .
     */
    @GetMapping("/getTotalGermanGeoAllTime")
    public String getTotalGermanGeoAllTime() throws JSONException {return geoService.getTotalGermanGeoAllTime();}

    /**
     * Endpoint for retrieval of all German Geolocation Data gathered in detail.
     * @param region the region to fetch detailed data for.
     * @return a JSON-String containing cities as labels and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoAllTime")
    public String getRegionGermanGeoAllTime(String region) throws JSONException {return geoService.getRegionGermanGeoAllTime(region);}

    /**
     * Endpoint for retrieval of all German Geolocation Data gathered in detail, for a specific timespan.
     * @param region the region to fetch detailed data for.
     * @param start the inclusive start of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @param end the inclusive end of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @return a JSON-String containing cities as labels and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoByDate")
    public String getRegionGermanGeoByDate(String region, String start, String end) throws JSONException {return geoService.getRegionGermanGeoByDate(region, start, end);}

    /**
     * Fetches average clicks per city for a specific region of germany of all time.
     * @param region the region to fetch for.
     * @return a JSON-String containing cities as labels and their average clicks per day as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoAllTimeAverages")
    public String getRegionGermanGeoAllTimeAverage(String region) throws JSONException {return geoService.getRegionGermanGeoAllTimeAverage(region);}

    /**
     * Fetches average clicks per city for a specific region of germany of a specific timespan.
     * @param region the region to fetch for.
     * @param start the inclusive start of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @param end the inclusive end of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @return a JSON-String containing cities as labels and their average clicks per day as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoByDaysAverages")
    public String getRegionGermanGeoByDaysAverages(String region, String start, String end) throws JSONException {return geoService.getRegionGermanGeoByDaysAverages(region, start, end);}

    /**
     * Fetches the beginning and end of our geolocation tracking.
     * @return a String-Array containing dates.
     */
    @GetMapping("/geoRange")
    public String[] getDateRange() {return geoService.getDateRange();}

    /**
     * Endpoint for retrieval of a regions Geolocation Data with dates.
     * @param region the region or DACH country you want stats for.
     * @param start the Date to start gathering Geo-Data (inclusive). In Format yyyy-MM-dd.
     * @param end the Date to end gathering Geo-Data (inclusive). In Format yyyy-MM-dd.
     * @return a JSON String with "dates" on containing the date data was gathered for, a matching List in "data"
     * containing the respective stats.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoByDateAsList")
    public String getRegionalGeoAsListByDates(String region, String start, String end) throws JSONException {return geoService.getRegionalGeoAsListByDates(region, start, end);}

    /**
     * Fetches Geolocation-Data for a user of all time.
     * @param userId the id of the user to fetch for.
     * @return a JSON-String containing ISO-Codes of german Region Codes as labels and clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getUserGeoWithPostsAllTime")
    public String getUserGeoTotalAllTime(int userId) throws JSONException {return geoService.getUserGeoTotalAllTime(userId);}

    /**
     * Fetches Geolocation-Data for a user in a specific timespan.
     * @param userId the id of the user to fetch for.
     * @param start the inclusive start of the timespan. In Format yyyy-MM-dd.
     * @param end the inclusive end of the timespan. In Format yyyy-MM-dd.
     * @return a JSON-String containing ISO-Codes of german Region Codes as labels and clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getUserGeoWithPostsByDays")
    public String getUserGeoTotalByDays(int userId, String start, String end) throws JSONException {return geoService.getUserGeoTotalByDays(userId, start, end);}

    @GetMapping("/getTop5CitiesByClicks")
    public String getTop5CitiesByClicks() {return geoService.getTop5CitiesByClicks();}
}

