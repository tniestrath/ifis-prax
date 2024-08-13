package com.analysetool.api;

import com.analysetool.services.DiagnosisService;
import com.analysetool.services.LogService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/diagnosis", "/0wB4P2mly-xaRmeeDOj0_g/diagnosis"})
public class DiagnosisController {

    @Autowired
    DiagnosisService diagnosisService;
    @Autowired
    LogService logService;

    /**
     * An aggregate methods to find Problems in all parts of the database.
     * @return an ordered JSONArray-String, containing information about all Problems that have been found. (ordered by descending severity)
     * @throws JSONException .
     */
    @GetMapping("/doCheckUp")
    public String doCheckUp() throws JSONException {return diagnosisService.doCheckUp();}

    /**
     * An aggregate methods to find Problems in all parts of the database.
     * @return an ordered JSONArray-String, containing information about all Problems that have been found. (ordered by descending severity)
     * @throws JSONException .
     */
    @GetMapping("/doCheckUpSite")
    public String doCheckUpHTML() throws JSONException {return diagnosisService.doCheckUpHTML();}


    @GetMapping("/getBotProblem")
    public String findPotentialBotsTest(int repeatedClicksLimit) {return diagnosisService.findPotentialBotsTest(repeatedClicksLimit);}

    /**
     * Fetches all Data for blocked Bots by the Black Hole Plugin.
     * @return a JSON-Array of JSON-Objects representing their data as a String.
     * @throws JSONException .
     */
    @GetMapping("/getBlackHoleData")
    public String getAllBlockedBotsBlackHole() throws JSONException {return diagnosisService.getAllBlockedBotsBlackHole();}

    /**
     * Fetches external-services from table.
     * @param page page number.
     * @param size page size.
     * @return a JSON-String containing sets of external services.
     * @throws JSONException .
     */
    @GetMapping("/getServices")
    public String getServices(int page, int size) throws JSONException {return diagnosisService.getServices(page, size);}

    /**
     * Add an external service to database.
     * @param name the name of the service.
     * @param link the link to the service (iframe source).
     * @return a boolean whether the addition worked correctly.
     */
    @PostMapping("/addService")
    public boolean addService(String name, String link) {return diagnosisService.addService(name, link);}

    /**
     * Removes an external service to database.
     * @param name the name of the service.
     * @param link the link to the service (iframe source).
     * @return a boolean whether the addition worked correctly.
     */
    @PostMapping("/removeService")
    public boolean removeService(String name, String link) {return diagnosisService.removeService(name, link);}

    /**
     * Manually runs the stat-updating procedure.
     * @return boolean whether the procedure ended correctly, a Time-Out does NOT mean it did not work.
     */
    @GetMapping("/run")
    public void updateStats() {
        try {
            logService.runScheduled();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/updateServiceBuffer")
    public boolean updateServiceBuffer(String name, String link, int buffer) {return diagnosisService.updateServiceBuffer(name, link, buffer);}

    @GetMapping("/getRunningStatus")
    public boolean isRunning() {
        return logService.isRunning();
    }

}
