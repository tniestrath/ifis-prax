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
    public String getAllSites() {
        return matomoService.getMatomoData("SitesManager.getAllSites");
    }

    @GetMapping("/getVisitsSummary")
    public String getVisitsSummary() {
        return matomoService.getMatomoData( "VisitsSummary.get");
    }

    @GetMapping("/getCustomVisitsSummary")
    public String getVisitsSummary(@RequestParam String siteId, @RequestParam String period, @RequestParam String date) {
        String method = "VisitsSummary.get&idSite=" + siteId + "&period=" + period + "&date=" + date;
        return matomoService.getMatomoData(method);
    }

    @GetMapping("/getLiveVisitors")
    public String getLiveVisitors(@RequestParam String siteId) {
        String method = "Live.getLastVisitsDetails&idSite=" + siteId;
        return matomoService.getMatomoData(method);
    }

}
