package com.analysetool.api;

import com.analysetool.services.MatomoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatomoController {

    private final MatomoService matomoService;

    @Autowired
    public MatomoController(MatomoService matomoService) {
        this.matomoService = matomoService;
    }

    @GetMapping("/getAllSites")
    public String getAllSites(@RequestParam String authToken) {
        return matomoService.getMatomoData(authToken, "SitesManager.getAllSites");
    }

    @GetMapping("/getVisitsSummary")
    public String getVisitsSummary(@RequestParam String authToken) {
        return matomoService.getMatomoData(authToken, "VisitsSummary.get");
    }


}
