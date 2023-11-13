package com.analysetool.api;

import com.analysetool.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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

}
