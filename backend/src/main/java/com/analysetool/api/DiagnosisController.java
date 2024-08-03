package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.DiagnosisService;
import com.analysetool.services.LogService;
import com.analysetool.services.PostService;
import com.analysetool.services.UniqueUserService;
import com.analysetool.util.Problem;
import com.mysql.cj.log.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    @GetMapping("/getServices")
    public String getServices(int page, int size) throws JSONException {return diagnosisService.getServices(page, size);}

    @PostMapping("/addService")
    public boolean addService(String name, String link) {return diagnosisService.addService(name, link);}

    @PostMapping("/removeService")
    public boolean removeService(String name, String link) {return diagnosisService.removeService(name, link);}

    @GetMapping("/run")
    public boolean updateStats() {
        try {
            logService.runScheduled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
