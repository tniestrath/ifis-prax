package com.analysetool.api;

import com.analysetool.modells.ClicksByBundesland;
import com.analysetool.modells.PostGeo;
import com.analysetool.modells.UserGeo;
import com.analysetool.repositories.*;
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

    @GetMapping("/getTotalGermanGeoByDay")
    public List<Integer> getTotalGermanGeoByDay(String start, String end) {
        List<Integer> liste = new ArrayList<>();
        Date dateStart = Date.valueOf(start);
        Date dateEnd = Date.valueOf(end);

        for(int i = 0; i < 16; i++) {
            liste.add(0);
        }

        for(LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate()).toList()) {
            int uniId = 0;
            if(uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniStatRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }
            if(uniId != 0) {
                for (ClicksByBundesland clicksByB : clicksByBundeslandRepo.getByUniID(uniId)) {
                    switch (clicksByB.getBundesland()) {
                        case "HH" -> liste.set(0, liste.get(0) + clicksByB.getClicks());
                        case "HB" -> liste.set(1, liste.get(1) + clicksByB.getClicks());
                        case "BE" -> liste.set(2, liste.get(2) + clicksByB.getClicks());
                        case "BB" -> liste.set(3, liste.get(3) + clicksByB.getClicks());
                        case "SN" -> liste.set(4, liste.get(4) + clicksByB.getClicks());
                        case "ST" -> liste.set(5, liste.get(5) + clicksByB.getClicks());
                        case "BY" -> liste.set(6, liste.get(6) + clicksByB.getClicks());
                        case "SL" -> liste.set(7, liste.get(7) + clicksByB.getClicks());
                        case "RP" -> liste.set(8, liste.get(8) + clicksByB.getClicks());
                        case "SH" -> liste.set(9, liste.get(9) + clicksByB.getClicks());
                        case "TH" -> liste.set(10, liste.get(10) + clicksByB.getClicks());
                        case "NI" -> liste.set(11, liste.get(11) + clicksByB.getClicks());
                        case "HE" -> liste.set(13, liste.get(13) + clicksByB.getClicks());
                        case "BW" -> liste.set(14, liste.get(14) + clicksByB.getClicks());
                        case "NW" -> liste.set(15, liste.get(15) + clicksByB.getClicks());
                    }
                }
            }
        }
        return liste;
    }
}
