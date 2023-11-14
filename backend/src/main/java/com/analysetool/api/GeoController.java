package com.analysetool.api;

import com.analysetool.modells.PostGeo;
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
        LocalDate dateStart = Date.valueOf(start).toLocalDate();
        LocalDate dateEnd = Date.valueOf(end).toLocalDate();

        PostGeo geo = postGeoRepo.findByPostIdAndUniStatId(id, uniStatRepo.getByDatum(dateStart).getId());
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
            liste.add(geo.getHb());
            liste.add(geo.getHe());
            liste.add(geo.getBW());
            liste.add(geo.getNW());
            liste.add(geo.getAusland());
        }
        for(LocalDate date : dateStart.datesUntil(dateEnd).toList()) {
            geo = postGeoRepo.findByPostIdAndUniStatId(id, uniStatRepo.getByDatum(date).getId());
            if(geo != null) {
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
                liste.set(13, geo.getHb() + liste.get(13));
                liste.set(14, geo.getHe() + liste.get(14));
                liste.set(15, geo.getBW() + liste.get(15));
                liste.set(16, geo.getNW() + liste.get(16));
                liste.set(17, geo.getAusland() + liste.get(17));
            }

        }
        return liste;
    }
}
