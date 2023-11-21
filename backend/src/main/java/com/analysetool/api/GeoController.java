package com.analysetool.api;

import com.analysetool.modells.ClicksByBundesland;
import com.analysetool.modells.ClicksByBundeslandCitiesDLC;
import com.analysetool.modells.PostGeo;
import com.analysetool.modells.UserGeo;
import com.analysetool.repositories.*;
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
import java.time.ZoneOffset;
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

    /**
     *
     * @param date ein Datum im Format yyyy-MM-dd.
     * @param bundesland der ISO-Code eines Bundeslands.
     * @return Anzahl der Clicks aus diesem Bundesland am angegebenen Tag.
     * @throws ParseException Falls Datum falsches Format. Schande.
     */
    @GetMapping("/getClicksByDayAndBundesland")
    public int getClicksByDayAndBundesland(String date, String bundesland) throws ParseException {
        int id = uniStatRepo.findByDatum(new SimpleDateFormat("yyyy-MM-dd").parse(date)).get().getId();
        return clicksByBundeslandRepo.getByUniIDAndBundesland(id, bundesland).getClicks();
    }

    /**
     *
     * @param date ein Datum im Format yyyy-MM-dd.
     * @param country der ISO-Code eines Bundeslands.
     * @return Anzahl der Clicks aus diesem Bundesland am angegebenen Tag.
     * @throws ParseException Falls Datum falsches Format. Schande.
     */
    @GetMapping("/getClicksByDayAndCountry")
    public int getClicksByDayAndCountry(String date, String country) throws ParseException {
        int id = uniStatRepo.findByDatum(new SimpleDateFormat("yyyy-MM-dd").parse(date)).get().getId();
        return clicksByCountryRepo.getByUniIDAndCountry(id, country).getClicks();
    }

    @GetMapping("/getPostGeoByIDAndDay")
    public List<Integer> getPostGeoByIDAndDay(long id, String start, String end){
        List<Integer> liste = new ArrayList<>();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);

        PostGeo geo = postGeoRepo.findByPostIdAndUniStatId(id, uniStatRepo.findByDatum(dateStart).get().getId());
        if(geo != null) {
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
        for(LocalDate date : dateStart.toLocalDate().plusDays(1).datesUntil(dateEnd.toLocalDate()).toList()) {
            boolean isGeo = false;
            if(uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                geo = postGeoRepo.findByPostIdAndUniStatId(id, uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId());
                isGeo = true;
            }
            if(geo != null && isGeo) {
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


    @GetMapping("/getUserGeoByIDAndDay")
    public List<Integer> getUserGeoByIDAndDay(long id, String start, String end){
        List<Integer> liste = new ArrayList<>();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);

        UserGeo geo = userGeoRepo.findByUserIdAndUniStatId(id, uniStatRepo.findByDatum(dateStart).get().getId());
        if(geo != null) {
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
        for(LocalDate date : dateStart.toLocalDate().plusDays(1).datesUntil(dateEnd.toLocalDate()).toList()) {
            boolean isGeo = false;
            if(uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                geo = userGeoRepo.findByUserIdAndUniStatId(id, uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId());
                isGeo = true;
            }
            if(geo != null && isGeo) {
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
     *
     * @param start a String representing the start date of calculation format: YYYY-MM-DD
     * @param end a String representing the end date of calculation format: YYYY-MM-DD
     * @return a json-string containing the clicks of each bundesland and adjacent country of interest, labeled by their ISO-Code for bundesland and english name for countries.
     * @throws JSONException if something unexpected happened.
     */
    @GetMapping("/getTotalGermanGeoByDay")
    public String getTotalGermanGeoByDay(String start, String end) throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);

        if(dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }

        int total = 0;

        //Iterate over all days in the interval.
        for(LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if(uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            //If we do have stats, put stats for the day into the json.
            if(uniId != 0) {
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
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium") != null) {
                    try {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks() + Integer.parseInt(json.get("BG").toString()));
                    } catch (JSONException e) {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks();
                }
                //..For the Netherlands
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands") != null) {
                    try {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks() + Integer.parseInt(json.get("NL").toString()));
                    } catch (JSONException e) {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks();
                }
                //..For Austria
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria") != null) {
                    try {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks() + Integer.parseInt(json.get("AT").toString()));
                    } catch (JSONException e) {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks();
                }
                //..For Luxembourg
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg") != null) {
                    try {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks() + Integer.parseInt(json.get("LU").toString()));
                    } catch (JSONException e) {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks();
                }
                //..and for Switzerland.
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland") != null) {
                    try {
                        json.put("SW", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks() + Integer.parseInt(json.get("SW").toString()));
                    } catch (JSONException e) {
                        json.put("SW", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks();
                }

                json.put("total", total);

            }

        }
        return json.toString();
    }

    @GetMapping("/getTotalGermanGeoAllTime")
    public String getTotalGermanGeoAllTime() throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = new Date(uniStatRepo.getEarliestUniStat().getDatum().getTime());
        Date dateEnd = new Date(uniStatRepo.getLatestUniStat().getDatum().getTime());
        if(dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }
        int total = 0;

        //Iterate over all days in the interval.
        for(LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if(uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            //If we do have stats, put stats for the day into the json.
            if(uniId != 0) {
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
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium") != null) {
                    try {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks() + Integer.parseInt(json.get("BG").toString()));
                    } catch (JSONException e) {
                        json.put("BG", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Belgium").getClicks();
                }
                //..For the Netherlands
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands") != null) {
                    try {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks() + Integer.parseInt(json.get("NL").toString()));
                    } catch (JSONException e) {
                        json.put("NL", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Netherlands").getClicks();
                }
                //..For Austria
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria") != null) {
                    try {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks() + Integer.parseInt(json.get("AT").toString()));
                    } catch (JSONException e) {
                        json.put("AT", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Austria").getClicks();
                }
                //..For Luxembourg
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg") != null) {
                    try {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks() + Integer.parseInt(json.get("LU").toString()));
                    } catch (JSONException e) {
                        json.put("LU", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Luxembourg").getClicks();
                }
                //..and for Switzerland.
                if(clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland") != null) {
                    try {
                        json.put("SW", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks() + Integer.parseInt(json.get("SW").toString()));
                    } catch (JSONException e) {
                        json.put("SW", clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks());
                    }
                    total += clicksByCountryRepo.getByUniIDAndCountry(uniId, "Switzerland").getClicks();
                }

                json.put("total", total);

            }

        }
        return json.toString();
    }

    @GetMapping("/getRegionGermanGeoAllTime")
    public String getRegionGermanGeoAllTime(String region) throws JSONException {
        JSONObject json = new JSONObject();
        for(ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByBundesland(region)) {
            try {
                json.put(cityClicks.getCity(), cityClicks.getClicks() + Integer.parseInt(json.get(cityClicks.getCity()).toString()));
            } catch (JSONException e) {
                json.put(cityClicks.getCity(), cityClicks.getClicks());
            }
        }
        return json.toString();
    }

    @GetMapping("/getRegionGermanGeoAllTimeAverages")
    public String getRegionGermanGeoAllTimeAverage(String region) throws JSONException {
        JSONObject json = new JSONObject();
        int countDays = clicksByBundeslandCitiesDLCRepo.getCountDays();
        for(ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByBundesland(region)) {

            try {
                json.put(cityClicks.getCity(), (cityClicks.getClicks() / countDays) + Integer.parseInt(json.get(cityClicks.getCity()).toString()));
            } catch (JSONException e) {
                json.put(cityClicks.getCity(), cityClicks.getClicks() / countDays);
            }
        }
        return json.toString();
    }

    @GetMapping("/getRegionGermanGeoByDaysAverages")
    public String getRegionGermanGeoByDaysAverages(String region, String start, String end) throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);
        if(dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }

        int countDays = (int) dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).count();

        //Iterate over all days in the interval.
        for(LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }
            if(uniId != 0) {
                for(ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByUniIDAndBundesland(uniId, region)) {
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

    @GetMapping("/getRegionGermanGeoByDays")
    public String getRegionGermanGeoByDays(String region, String start, String end) throws JSONException {
        JSONObject json = new JSONObject();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);
        if(dateStart.after(dateEnd)) {
            Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }


        //Iterate over all days in the interval.
        for(LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }
            if(uniId != 0) {
                for(ClicksByBundeslandCitiesDLC cityClicks : clicksByBundeslandCitiesDLCRepo.getByUniIDAndBundesland(uniId, region)) {
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


    @GetMapping("/geoRange")
    public String[] getDateRange() {
        String[] string;
        string = new String[]{uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getLastEntry()).get().getDatum().toInstant().atOffset(ZoneOffset.ofHours(0)).toString(), uniStatRepo.findById(clicksByBundeslandCitiesDLCRepo.getFirstEntry()).get().getDatum().toInstant().atOffset(ZoneOffset.ofHours(0)).toString()};
        return string;
    }
}
