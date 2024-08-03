package com.analysetool.api;

import com.analysetool.services.SystemLoadService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = {"/systemLoad", "/0wB4P2mly-xaRmeeDOj0_g/systemLoad"}, method = RequestMethod.GET, produces = "application/json")
public class SystemLoadController {

    private final SystemLoadService systemLoadService;

    @Autowired
    public SystemLoadController(SystemLoadService systemLoadService) {
        this.systemLoadService = systemLoadService;
    }

    @GetMapping("/getHour")
    public int getHour() {
        return systemLoadService.getHour();
    }

    @GetMapping("/average")
    public String getAverageLoad() {return systemLoadService.getAverageLoad();}

    @GetMapping("/current")
    public String getCurrentLoad() throws JSONException {return systemLoadService.getCurrentLoad();}

    @GetMapping("/peak")
    public String getPeakLoad() {return systemLoadService.getPeakLoad();}

    @GetMapping("/systemLive")
    public String getComplete() throws JSONException {return systemLoadService.getComplete();}

    /**
     * Berechnet die Festplattennutzung in Prozent.
     * Diese Methode summiert den Gesamtspeicherplatz und den verfügbaren Speicherplatz
     * aller Laufwerke auf dem System und berechnet den Prozentsatz des genutzten
     * Speicherplatzes. Dies kann nützlich sein, um einen Überblick über die
     * Speicherauslastung des Systems zu erhalten.
     *
     * @return Die aktuelle Festplattennutzung in Prozent.
     */
    @GetMapping("/getDiscUsageInPercentage")
    public String getDiscUsageInPercentage(){return systemLoadService.getDiscUsageInPercentage();}

    /**
     * Diese Methode liefert Informationen über den Speicherplatz auf der Festplatte, einschließlich
     * des gesamten Speicherplatzes, des genutzten Speicherplatzes, des freien Speicherplatzes und der
     * prozentualen Nutzung der Festplatte. Die Daten werden im JSON-Format zurückgegeben.
     *
     * @return Eine JSON-String-Repräsentation der Festplattendaten oder "Error" bei Auftreten von Fehlern.
     */
    @GetMapping("/getDiscData")
    public String getDiscData(){return systemLoadService.getDiscData();}
}

