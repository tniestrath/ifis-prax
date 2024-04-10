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

    /**
     * Fetches a list of all sites registered in the Matomo setup.
     *
     * @return A JSON string containing the details of all sites.
     */
    @GetMapping("/getAllSites")
    public String getAllSites() {
        return matomoService.getMatomoData("SitesManager.getAllSites");
    }


    /**
     * Fetches a summary of visits without specific filters.
     *
     * @return A JSON string containing a summary of visits.
     */
    @GetMapping("/getVisitsSummary")
    public String getVisitsSummary() {
        return matomoService.getMatomoData( "VisitsSummary.get");
    }

    /**
     * Fetches a custom summary of visits based on provided parameters.
     *
     * @param siteId  The ID of the site for which to fetch the visit summary.
     * @param period  The period for which to fetch the visits summary (e.g., day, week, month, year, or range).
     * @param date    The specific date or date range (for the 'range' period) for which to fetch the visit summary.
     * @return A JSON string containing a custom summary of visits.
     */
    @GetMapping("/getCustomVisitsSummary")
    public String getVisitsSummary(@RequestParam String siteId, @RequestParam String period, @RequestParam String date) {
        String method = "VisitsSummary.get&idSite=" + siteId + "&period=" + period + "&date=" + date;
        return matomoService.getMatomoData(method);
    }

    /**
     * Fetches live details about the recent visitors of a specific site.
     *
     * @param siteId  The ID of the site for which to fetch live visitor details.
     * @return A JSON string containing live details of recent visitors.
     */
    @GetMapping("/getLiveVisitors")
    public String getLiveVisitors(@RequestParam String siteId) {
        String method = "Live.getLastVisitsDetails&idSite=" + siteId;
        return matomoService.getMatomoData(method);
    }

}
