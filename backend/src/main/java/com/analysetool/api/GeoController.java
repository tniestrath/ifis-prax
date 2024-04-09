package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/geo")
public class GeoController {

    @Autowired
    private IPsByPostRepository iPsByPostRepository;

    @Autowired
    private IPsByUserRepository iPsByUserRepository;

    @Autowired
    private ClicksByCountryRepository clicksByCountryRepo;

    @Autowired
    private ClicksByBundeslandRepository clicksByBundeslandRepo;

    @Autowired
    private ClicksByBundeslandCitiesDLCRepository clicksByBundeslandCitiesDLCRepo;

    @Autowired
    private PostGeoRepository postGeoRepo;

    @Autowired
    private UserGeoRepository userGeoRepo;

    @Autowired
    private universalStatsRepository uniStatRepo;

    @Autowired
    private PostRepository postRepo;

    /**
     * @param date       ein Datum im Format yyyy-MM-dd.
     * @param bundesland der ISO-Code eines Bundeslands.
     * @return Anzahl der Clicks aus diesem Bundesland am angegebenen Tag.
     * @throws ParseException Falls Datum falsches Format. Schande.
     */
    @GetMapping("/getClicksByDayAndBundesland")
    public int getClicksByDayAndBundesland(String date, String bundesland) throws ParseException {
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = uniStatRepo.findByDatum(new SimpleDateFormat("yyyy-MM-dd").parse(date)).get().getId();
        return clicksByBundeslandRepo.getByUniIDAndBundesland(id, bundesland).getClicks();
    }

    /**
     * @param date    ein Datum im Format yyyy-MM-dd.
     * @param country der ISO-Code eines Bundeslands.
     * @return Anzahl der Clicks aus diesem Bundesland am angegebenen Tag.
     * @throws ParseException Falls Datum falsches Format. Schande.
     */
    @GetMapping("/getClicksByDayAndCountry")
    public int getClicksByDayAndCountry(String date, String country) throws ParseException {
        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = uniStatRepo.findByDatum(new SimpleDateFormat("yyyy-MM-dd").parse(date)).get().getId();
        return clicksByCountryRepo.getByUniIDAndCountry(id, country).getClicks();
    }

    /**
     * Endpoint for retrieval of a Posts Geolocation-stats by their id, only using data gathered between Date start and Date end.
     * @param id the posts id you want stats for.
     * @param start a String containing an ISO-Format Date, marking the starting date (inclusive).
     * @param end a String containing an ISO-Format Date, marking the ending date. (inclusive)
     * @return a List of Geolocation Data in a specific order, marked for change.
     */
    @GetMapping("/getPostGeoByIDAndDay")
    public List<Integer> getPostGeoByIDAndDay(long id, String start, String end) {
        List<Integer> liste = new ArrayList<>();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);

        @SuppressWarnings("OptionalGetWithoutIsPresent") PostGeo geo = postGeoRepo.findByPostIdAndUniStatId(id, uniStatRepo.findByDatum(dateStart).get().getId());
        if (geo != null) {
            liste.add(geo.getHh());
            liste.add(geo.getHb());
            liste.add(geo.getBe());
            liste.add(geo.getMv());
            liste.add(geo.getBb());
            liste.add(geo.getSn());
            liste.add(geo.getSt());
            liste.add(geo.getBye());
            liste.add(geo.getSl());
            liste.add(geo.getRp());
            liste.add(geo.getSh());
            liste.add(geo.getTh());
            liste.add(geo.getNb());
            liste.add(geo.getHe());
            liste.add(geo.getBW());
            liste.add(geo.getNW());
            liste.add(geo.getAusland());
        }
        for (LocalDate date : dateStart.toLocalDate().plusDays(1).datesUntil(dateEnd.toLocalDate()).toList()) {
            boolean isGeo = false;
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                geo = postGeoRepo.findByPostIdAndUniStatId(id, uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId());
                isGeo = true;
            }
            if (geo != null && isGeo) {
                liste.set(0, geo.getHh() + liste.get(0));
                liste.set(1, geo.getHb() + liste.get(1));
                liste.set(2, geo.getBe() + liste.get(2));
                liste.set(3, geo.getMv() + liste.get(3));
                liste.set(4, geo.getBb() + liste.get(4));
                liste.set(5, geo.getSn() + liste.get(5));
                liste.set(6, geo.getSt() + liste.get(6));
                liste.set(7, geo.getBye() + liste.get(7));
                liste.set(8, geo.getSl() + liste.get(8));
                liste.set(9, geo.getRp() + liste.get(9));
                liste.set(10, geo.getSh() + liste.get(10));
                liste.set(11, geo.getTh() + liste.get(11));
                liste.set(12, geo.getNb() + liste.get(12));
                liste.set(14, geo.getHe() + liste.get(13));
                liste.set(15, geo.getBW() + liste.get(14));
                liste.set(16, geo.getNW() + liste.get(15));
                liste.set(17, geo.getAusland() + liste.get(16));
            }

        }
        return liste;
    }

    /**
     * Endpoint for retrieval of a Users Geolocation Data gathered between two Dates.
     * @param id the users' id.
     * @param start the starting date as a string in iso-format (inclusive).
     * @param end the ending date as a string in iso-format. (inclusive)
     * @return a List of Geolocation Data for the given user in a specific order.
     */
    @GetMapping("/getUserGeoByIdAndDay")
    public List<Integer> getUserGeoByIDAndDay(long id, String start, String end) {
        List<Integer> liste = new ArrayList<>();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);

        @SuppressWarnings("OptionalGetWithoutIsPresent") UserGeo geo = userGeoRepo.findByUserIdAndUniStatId(id, uniStatRepo.findByDatum(dateStart).get().getId());
        if (geo != null) {
            liste.add(geo.getHh());
            liste.add(geo.getHb());
            liste.add(geo.getBe());
            liste.add(geo.getMv());
            liste.add(geo.getBb());
            liste.add(geo.getSn());
            liste.add(geo.getSt());
            liste.add(geo.getBye());
            liste.add(geo.getSl());
            liste.add(geo.getRp());
            liste.add(geo.getSh());
            liste.add(geo.getTh());
            liste.add(geo.getNb());
            liste.add(geo.getHe());
            liste.add(geo.getBW());
            liste.add(geo.getNW());
            liste.add(geo.getAusland());

            for (LocalDate date : dateStart.toLocalDate().plusDays(1).datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
                boolean isGeo = false;
                if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                    geo = userGeoRepo.findByUserIdAndUniStatId(id, uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId());
                    isGeo = true;
                }
                if (geo != null && isGeo) {
                    liste.set(0, geo.getHh() + liste.get(0));
                    liste.set(1, geo.getHb() + liste.get(1));
                    liste.set(2, geo.getBe() + liste.get(2));
                    liste.set(3, geo.getMv() + liste.get(3));
                    liste.set(4, geo.getBb() + liste.get(4));
                    liste.set(5, geo.getSn() + liste.get(5));
                    liste.set(6, geo.getSt() + liste.get(6));
                    liste.set(7, geo.getBye() + liste.get(7));
                    liste.set(8, geo.getSl() + liste.get(8));
                    liste.set(9, geo.getRp() + liste.get(9));
                    liste.set(10, geo.getSh() + liste.get(10));
                    liste.set(11, geo.getTh() + liste.get(11));
                    liste.set(12, geo.getNb() + liste.get(12));
                    liste.set(13, geo.getHe() + liste.get(13));
                    liste.set(14, geo.getBW() + liste.get(14));
                    liste.set(15, geo.getNW() + liste.get(15));
                    liste.set(16, geo.getAusland() + liste.get(16));
                }

            }
        }
        return liste;
    }

    /**
     * Endpoint for retrieval of all Global Geolocation Data gathered between the two Dates for the DACH region.
     * @param start a String representing the start date of calculation format: YYYY-MM-DD (inclusive).
     * @param end   a String representing the end date of calculation format: YYYY-MM-DD, (inclusive).
     * @return a json-string containing the clicks of each bundesland and adjacent country of interest, labeled by their ISO-Code for bundesland and english name for countries.
     * @throws JSONException if something unexpected happened.
     */
    @GetMapping("/getTotalGermanGeoByDay")
    public String getTotalGermanGeoByDay(String start, String end) throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);

        if (dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }

        double total = 0;
        int count = 0;

        json.put("totalDACH", 0);
        json.put("total", 0);

        //Iterate over all days in the interval.
        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            //If we do have stats, put stats for the day into the json.
            if (uniId != 0 && !clicksByBundeslandRepo.getByUniID(uniId).isEmpty() && !clicksByCountryRepo.getByUniID(uniId).isEmpty()) {
                count++;

                //Add all stats from ClicksByBundesland
                for (ClicksByBundesland clicksByB : clicksByBundeslandRepo.getByUniID(uniId)) {
                    try {
                        json.put(clicksByB.getBundesland(), clicksByB.getClicks() + Integer.parseInt(json.get(clicksByB.getBundesland()).toString()));
                    } catch (JSONException e) {
                        json.put(clicksByB.getBundesland(), clicksByB.getClicks());
                    }
                    total += clicksByB.getClicks();
                }
                //..For Belgium
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium") != null) {
                    try {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks() + Integer.parseInt(json.get("BG").toString()));
                    } catch (JSONException e) {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks();
                }
                //..For the Netherlands
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands") != null) {
                    try {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks() + Integer.parseInt(json.get("NL").toString()));
                    } catch (JSONException e) {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks();
                }
                //..For Austria
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria") != null) {
                    try {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks() + Integer.parseInt(json.get("AT").toString()));
                    } catch (JSONException e) {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks();
                }
                //..For Luxembourg
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg") != null) {
                    try {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks() + Integer.parseInt(json.get("LU").toString()));
                    } catch (JSONException e) {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks();
                }
                //..and for Switzerland.
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland") != null) {
                    try {
                        json.put("CH", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks() + Integer.parseInt(json.get("CH").toString()));
                    } catch (JSONException e) {
                        json.put("CH", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks();
                }

                json.put("totalDACH", total);
                json.put("total", (clicksByCountryRepo.getClicksAusland(uniId)) + json.getDouble("total"));

            }

        }
        return json.toString();
    }

    /**
     * Endpoint for retrieval of all Global Geolocation Data gathered for the DACH region.
     * @return a json-string containing the clicks of each bundesland and adjacent country of interest, labeled by their ISO-Code for bundesland and english name for countries.
     * @throws JSONException .
     */
    @GetMapping("/getTotalGermanGeoAllTime")
    public String getTotalGermanGeoAllTime() throws JSONException {
        JSONObject json = new JSONObject();
        @SuppressWarnings("OptionalGetWithoutIsPresent") Date dateStart = new Date(uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getFirstEntry()).get().getDatum().getTime());
        @SuppressWarnings("OptionalGetWithoutIsPresent") Date dateEnd = new Date(uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getLastEntry()).get().getDatum().getTime());
        if (dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }
        double total = 0;
        int count = 0;
        json.put("totalDACH", 0);
        json.put("total", 0);

        //Iterate over all days in the interval.
        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            //If we do have stats, put stats for the day into the json.
            if (uniId != 0 && !clicksByBundeslandRepo.getByUniID(uniId).isEmpty() && !clicksByCountryRepo.getByUniID(uniId).isEmpty()) {
                count++;
                //Add all stats from ClicksByBundesland
                for (ClicksByBundesland clicksByB : clicksByBundeslandRepo.getByUniID(uniId)) {
                    try {
                        json.put(clicksByB.getBundesland(), clicksByB.getClicks() + Integer.parseInt(json.get(clicksByB.getBundesland()).toString()));
                    } catch (JSONException e) {
                        json.put(clicksByB.getBundesland(), clicksByB.getClicks());
                    }
                    total += clicksByB.getClicks();
                }
                //..For Belgium
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium") != null) {
                    try {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks() + Integer.parseInt(json.get("BG").toString()));
                    } catch (JSONException e) {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks();
                }
                //..For the Netherlands
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands") != null) {
                    try {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks() + Integer.parseInt(json.get("NL").toString()));
                    } catch (JSONException e) {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks();
                }
                //..For Austria
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria") != null) {
                    try {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks() + Integer.parseInt(json.get("AT").toString()));
                    } catch (JSONException e) {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks();
                }
                //..For Luxembourg
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg") != null) {
                    try {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks() + Integer.parseInt(json.get("LU").toString()));
                    } catch (JSONException e) {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks();
                }
                //..and for Switzerland.
                if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland") != null) {
                    try {
                        json.put("CH", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks() + Integer.parseInt(json.get("CH").toString()));
                    } catch (JSONException e) {
                        json.put("CH", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks();
                }

                json.put("totalDACH", total);
                json.put("total", (clicksByCountryRepo.getClicksAusland(uniId)) + json.getDouble("total"));
            }

        }
        return json.toString();
    }

    /**
     * Endpoint for retrieval of all German Geolocation Data gathered in detail.
     * @param region the region to fetch detailed data for.
     * @return a JSON-String containing cities as labels and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoAllTime")
    public String getRegionGermanGeoAllTime(String region) throws JSONException {
        JSONObject json = new JSONObject();
        for (ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByBundesland(region)) {
            try {
                json.put(cityClicks.getCity(), cityClicks.getClicks() + Integer.parseInt(json.get(cityClicks.getCity()).toString()));
            } catch (JSONException e) {
                json.put(cityClicks.getCity(), cityClicks.getClicks());
            }
        }
        return json.toString();
    }

    /**
     * Endpoint for retrieval of all German Geolocation Data gathered in detail, for a specific timespan.
     * @param region the region to fetch detailed data for.
     * @param start the inclusive start of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @param end the inclusive end of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @return a JSON-String containing cities as labels and their clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoByDate")
    public String getRegionGermanGeoByDate(String region, String start, String end) throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);
        if (dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }


        //Iterate over all days in the interval.
        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }
            if (uniId != 0 && !clicksByBundeslandRepo.getByUniID(uniId).isEmpty() && !clicksByCountryRepo.getByUniID(uniId).isEmpty()) {
                for (ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByUniIDAndBundesland(uniId, region)) {
                    try {
                        json.put(cityClicks.getCity(), (cityClicks.getClicks()) + Integer.parseInt(json.get(cityClicks.getCity()).toString()));
                    } catch (JSONException e) {
                        json.put(cityClicks.getCity(), cityClicks.getClicks());
                    }
                }
            }

        }
        return json.toString();
    }

    /**
     * Fetches average clicks per city for a specific region of germany of all time.
     * @param region the region to fetch for.
     * @return a JSON-String containing cities as labels and their average clicks per day as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoAllTimeAverages")
    public String getRegionGermanGeoAllTimeAverage(String region) throws JSONException {
        JSONObject json = new JSONObject();
        int countDays = clicksByBundeslandCitiesDLCRepo.getCountDays();
        for (ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByBundesland(region)) {

            try {
                json.put(cityClicks.getCity(), (cityClicks.getClicks() / countDays) + Integer.parseInt(json.get(cityClicks.getCity()).toString()));
            } catch (JSONException e) {
                json.put(cityClicks.getCity(), cityClicks.getClicks() / countDays);
            }
        }
        return json.toString();
    }

    /**
     * Fetches average clicks per city for a specific region of germany of a specific timespan.
     * @param region the region to fetch for.
     * @param start the inclusive start of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @param end the inclusive end of the timespan to fetch data for as a String. In Format yyyy-MM-dd.
     * @return a JSON-String containing cities as labels and their average clicks per day as values.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoByDaysAverages")
    public String getRegionGermanGeoByDaysAverages(String region, String start, String end) throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);
        if (dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }

        int countDays = (int) dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).count();

        //Iterate over all days in the interval.
        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }
            if (uniId != 0 && !clicksByBundeslandRepo.getByUniID(uniId).isEmpty()) {
                for (ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByUniIDAndBundesland(uniId, region)) {
                    //
                    try {
                        json.put(cityClicks.getCity(), (cityClicks.getClicks() / countDays) + Integer.parseInt(json.get(cityClicks.getCity()).toString()));
                    } catch (JSONException e) {
                        json.put(cityClicks.getCity(), cityClicks.getClicks() / countDays);
                    }
                }
            }

        }
        return json.toString();
    }

    /**
     * Fetches the beginning and end of our geolocation tracking.
     * @return a String-Array containing dates.
     */
    @GetMapping("/geoRange")
    public String[] getDateRange() {
        String[] string;
        //noinspection OptionalGetWithoutIsPresent
        string = new String[]{uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getFirstEntry()).get().getDatum().toInstant().plusSeconds(8000).toString(), uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getLastEntry()).get().getDatum().toInstant().plusSeconds(8000).toString()};
        return string;
    }

    /**
     * Endpoint for retrieval of a regions Geolocation Data with dates.
     * @param region the region or DACH country you want stats for.
     * @param start the Date to start gathering Geo-Data (inclusive). In Format yyyy-MM-dd.
     * @param end the Date to end gathering Geo-Data (inclusive). In Format yyyy-MM-dd.
     * @return a JSON String with "dates" on containing the dates data was gathered for, a matching List in "data" containing the respective stats.
     * @throws JSONException .
     */
    @GetMapping("/getRegionGermanGeoByDateAsList")
    public String getRegionalGeoAsListByDates(String region, String start, String end) throws JSONException {
        JSONObject jsonResponse = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);
        List<String> listOfDates = new ArrayList<>();
        List<Integer> listOfData = new ArrayList<>();


        if (dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }
        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            if (uniId != 0 && !clicksByBundeslandRepo.getByUniID(uniId).isEmpty() && !clicksByCountryRepo.getByUniID(uniId).isEmpty()) {
                listOfDates.add(date.toString());
                if (region.equals("NL") || region.equals("AT") || region.equals("CH") || region.equals("LU")) {
                    switch (region) {
                        case "NL" -> {
                            if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands") != null) {
                                listOfData.add(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks());
                            } else {
                                listOfData.add(0);
                            }
                        }
                        case "AT" -> {
                            if (clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria") != null) {
                                listOfData.add(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks());
                            } else {
                                listOfData.add(0);
                            }
                        }
                        case "LU" -> {
                            if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg") != null) {
                                listOfData.add(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks());
                            } else {
                                listOfData.add(0);
                            }
                        }
                    }
                } else if (region.equals("BG")) {
                    if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium") != null) {
                        listOfData.add(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks());
                    } else {
                        listOfData.add(0);
                    }
                } else {
                    if(clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, region) != null) {
                        listOfData.add(clicksByBundeslandRepo.getByUniIDAndBundesland(uniId, region).getClicks());
                    } else {
                        listOfData.add(0);
                    }
                }

            } else {
                listOfDates.add(date.toString());
                listOfData.add(null);
            }

        }

        jsonResponse.put("dates", new JSONArray(listOfDates));
        jsonResponse.put("data", new JSONArray(listOfData));
        return jsonResponse.toString();

    }

    /**
     * Fetches Geolocation-Data for a user of all time.
     * @param userId the id of the user to fetch for.
     * @return a JSON-String containing ISO-Codes of german Region Codes as labels and clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getUserGeoWithPostsAllTime")
    public String getUserGeoTotalAllTime(int userId) throws JSONException {
        JSONObject json = new JSONObject();
        @SuppressWarnings("OptionalGetWithoutIsPresent") Date dateStart = new Date(uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getFirstEntry()).get().getDatum().getTime());
        @SuppressWarnings("OptionalGetWithoutIsPresent") Date dateEnd = new Date(uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getLastEntry()).get().getDatum().getTime());

        int total = 0;

        json.put("HH", 0);
        json.put("HB", 0);
        json.put("BE", 0);
        json.put("MV", 0);
        json.put("BB", 0);
        json.put("SN", 0);
        json.put("ST", 0);
        json.put("BY", 0);
        json.put("SL", 0);
        json.put("RP", 0);
        json.put("SH", 0);
        json.put("TH", 0);
        json.put("NI", 0);
        json.put("HE", 0);
        json.put("BW", 0);
        json.put("NW", 0);
        json.put("Ausland", 0);

        //Iterate over all days in the interval.
        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            //If we do have stats, put stats for the day into the json.

            if (uniId != 0) {
                UserGeo user = userGeoRepo.findByUserIdAndUniStatId(userId, uniId);
                if(user != null) {
                    updateRegionals(json, user.getHh(), user.getHb(), user.getBe(), user.getMv(), user.getBb(), user.getSn(), user.getSt(), user.getBye(), user.getSl(), user.getRp(), user.getSh(), user.getTh(), user.getNb(), user.getHe(), user.getBW(), user.getNW(), user.getAusland());

                    for (Post post : postRepo.findByAuthor(userId)) {
                        PostGeo postGeo = postGeoRepo.findByPostIdAndUniStatId(post.getId(), uniId);
                        if (postGeo != null) {
                            updateRegionals(json, postGeo.getHh(), postGeo.getHb(), postGeo.getBe(), postGeo.getMv(), postGeo.getBb(), postGeo.getSn(), postGeo.getSt(), postGeo.getBye(), postGeo.getSl(), postGeo.getRp(), postGeo.getSh(), postGeo.getTh(), postGeo.getNb(), postGeo.getHe(), postGeo.getBW(), postGeo.getNW(), postGeo.getAusland());
                        }
                    }
                }
            }


        }
        return json.toString();
    }

    /**
     * Fetches Geolocation-Data for a user in a specific timespan.
     * @param userId the id of the user to fetch for.
     * @param start the inclusive start of the timespan. In Format yyyy-MM-dd.
     * @param end the inclusive end of the timespan. In Format yyyy-MM-dd.
     * @return a JSON-String containing ISO-Codes of german Region Codes as labels and clicks as values.
     * @throws JSONException .
     */
    @GetMapping("/getUserGeoWithPostsByDays")
    public String getUserGeoTotalByDays(int userId, String start, String end) throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);
        if (dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }

        int total = 0;

        json.put("HH", 0);
        json.put("HB", 0);
        json.put("BE", 0);
        json.put("MV", 0);
        json.put("BB", 0);
        json.put("SN", 0);
        json.put("ST", 0);
        json.put("BY", 0);
        json.put("SL", 0);
        json.put("RP", 0);
        json.put("SH", 0);
        json.put("TH", 0);
        json.put("NI", 0);
        json.put("HE", 0);
        json.put("BW", 0);
        json.put("NW", 0);
        json.put("Ausland", 0);

        //Iterate over all days in the interval.
        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            //If we do have stats, put stats for the day into the json.

            if (uniId != 0) {
                UserGeo user = userGeoRepo.findByUserIdAndUniStatId(userId, uniId);
                if (user != null) {
                    updateRegionals(json, user.getHh(), user.getHb(), user.getBe(), user.getMv(), user.getBb(), user.getSn(), user.getSt(), user.getBye(), user.getSl(), user.getRp(), user.getSh(), user.getTh(), user.getNb(), user.getHe(), user.getBW(), user.getNW(), user.getAusland());

                    for (Post post : postRepo.findByAuthor(userId)) {
                        PostGeo postGeo = postGeoRepo.findByPostIdAndUniStatId(post.getId(), uniId);
                        if (postGeo != null) {
                            updateRegionals(json, postGeo.getHh(), postGeo.getHb(), postGeo.getBe(), postGeo.getMv(), postGeo.getBb(), postGeo.getSn(), postGeo.getSt(), postGeo.getBye(), postGeo.getSl(), postGeo.getRp(), postGeo.getSh(), postGeo.getTh(), postGeo.getNb(), postGeo.getHe(), postGeo.getBW(), postGeo.getNW(), postGeo.getAusland());
                        }
                    }
                }
            }
        }
        return json.toString();
    }

    /**
     * Increases all regional data in a JSON File by other values.
     * @param json the current json to add values onto.
     * @param hh the value to add to the key hh.
     * @param hb the value to add to the key hb.
     * @param be the value to add to the key be.
     * @param mv the value to add to the key mw.
     * @param bb the value to add to the key bb.
     * @param sn the value to add to the key sn.
     * @param st the value to add to the key st.
     * @param bye the value to add to the key bye.
     * @param sl the value to add to the key sl.
     * @param rp the value to add to the key rp.
     * @param sh the value to add to the key sh.
     * @param th the value to add to the key th.
     * @param nb the value to add to the key nb.
     * @param he the value to add to the key he.
     * @param bw the value to add to the key bw.
     * @param nw the value to add to the key nw.
     * @param ausland the value to add to the key ausland.
     * @throws JSONException .
     */
    private void updateRegionals(JSONObject json, int hh, int hb, int be, int mv, int bb, int sn, int st, int bye, int sl, int rp, int sh, int th, int nb, int he, int bw, int nw, int ausland) throws JSONException {
        json.put("HH", hh + json.getInt("HH"));
        json.put("HB", hb + json.getInt("HB"));
        json.put("BE", be + json.getInt("BE"));
        json.put("MV", mv + json.getInt("MV"));
        json.put("BB", bb + json.getInt("BB"));
        json.put("SN", sn + json.getInt("SN"));
        json.put("ST", st + json.getInt("ST"));
        json.put("BY", bye + json.getInt("BY"));
        json.put("SL", sl + json.getInt("SL"));
        json.put("RP", rp + json.getInt("RP"));
        json.put("SH", sh + json.getInt("SH"));
        json.put("TH", th + json.getInt("TH"));
        json.put("NI", nb + json.getInt("NI"));
        json.put("HE", he + json.getInt("HE"));
        json.put("BW", bw + json.getInt("BW"));
        json.put("NW", nw + json.getInt("NW"));
        json.put("Ausland", ausland + json.getInt("Ausland"));
    }
}

