package com.analysetool.api;

import com.analysetool.modells.FinalSearchStatDLC;
import com.analysetool.modells.UniversalStats;
import com.analysetool.repositories.FinalSearchStatDLCRepository;
import com.analysetool.repositories.PostTypeRepository;
import com.analysetool.repositories.universalStatsRepository;
import com.analysetool.util.Problem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/diagnosis")
public class DiagnosisController {


    @Autowired
    universalStatsRepository uniRepo;
    @Autowired
    FinalSearchStatDLCRepository finalSearchStatDLCRepo;
    @Autowired
    PostTypeRepository postTypeRepo;
    //ToDo : Add Logic to partial checkups.

    /**
     * An aggregate methods to find Problems in all parts of the database.
     * @return an ordered JSONArray-String, containing information about all Problems that have been found. (ordered by descending severity)
     * @throws JSONException .
     */
    @GetMapping("/doCheckUp")
    public String doCheckUp() throws JSONException {
        JSONArray problems = new JSONArray();



        for(Problem p : allCheckups()) {
            JSONObject json = new JSONObject();
            json.put("severity", p.getSeverity());
            json.put("description", p.getDescription());
            json.put("area", p.getAffectedArea());
            json.put("solutions", p.getSuggestedSolutions());
            problems.put(json);
        }

        return problems.toString();
    }

    private List<Problem> allCheckups() {
        List<Problem> largeList  = new ArrayList<>();

        //Add new lines for new checkups.
        largeList.addAll(findUniStatProblems());
        largeList.addAll(findUniDLCProblems());
        largeList.addAll(findGeoProblems());
        largeList.addAll(findSearchStatProblems());
        largeList.addAll(findTypeProblems());
        largeList.addAll(findWebsiteProblems());

        largeList.sort((o1, o2) -> o2.getSeverity() - o1.getSeverity());
        return largeList;
    }

    private List<Problem> findUniStatProblems() {
        List<Problem> list = new ArrayList<>();
        list.addAll(uniDateConsistencyCheckup());
        list.addAll(uniValuesCheckup());
        return list;
    }

    private List<Problem> uniValuesCheckup() {
        List<Problem> list  = new ArrayList<>();
        int severityNegative = 5;
        String descriptionNegative = "A negative Value has been found, where it shouldn't be possible: ";
        String area = "uni-stat-values";

        for(UniversalStats uni : uniRepo.findAllOrderById()) {
            if(uni.getTotalClicks() < 0) {
                list.add(new Problem(severityNegative, descriptionNegative + "totalClicks " + uni.getId(), area));
            } else if(uni.getAnbieter_abolos_anzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterAbolos at " + uni.getId(), area));
            } else if(uni.getAnbieterBasicAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterBasic at " + uni.getId(), area));
            } else if(uni.getAnbieterBasicPlusAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterBasicPlus at " + uni.getId(), area));
            } else if(uni.getAnbieterPlusAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterPlus at " + uni.getId(), area));
            } else if(uni.getAnbieterPremiumAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterPremium at " + uni.getId(), area));
            }
        }
        return list;
    }

    private List<Problem> uniDateConsistencyCheckup() {
        List<Problem> list  = new ArrayList<>();

        String area = "unistat-date";
        int severityMissing = 0;
        int severityDuplicate = 5;
        String descriptionMissing = "A missing Date has been found between: ";
        String descriptionDuplicate = "A duplicate Date has been found: ";


        java.sql.Date lastDate = null;
        for (UniversalStats uni : uniRepo.findAllOrderById()) {
            if(lastDate == null) {
                lastDate = new java.sql.Date(uni.getDatum().getTime());
            } else {
                //If two Dates are the same, add a duplicate problem
                if(lastDate.equals(new java.sql.Date(uni.getDatum().getTime()))) {
                    list.add(new Problem(severityDuplicate, descriptionDuplicate + lastDate, area));
                } else {
                    //Check whether the distance between the two dates is greater than a day, if so, add a missing Problem.
                    java.sql.Date sqlDate2 = new java.sql.Date(uni.getDatum().getTime());
                    // Calculate difference in milliseconds
                    long differenceInMilliseconds = Math.abs(sqlDate2.getTime() - lastDate.getTime());
                    // Convert milliseconds to days
                    long differenceInDays = differenceInMilliseconds / (1000 * 60 * 60 * 24);
                    if(differenceInDays > 1) {
                        list.add(new Problem(severityMissing, descriptionMissing + lastDate + " and " + sqlDate2, area));
                    }
                    lastDate = sqlDate2;
                }
            }
        }
        return list;

    }

    private List<Problem> findUniDLCProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }

    private List<Problem> findGeoProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }

    private List<Problem> findSearchStatProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }

    /**
     * Checks in FinalSearchStatsDLC, whether there are rows that report a SearchSuccess, without anything being clicked
     * @return a List of Problems.
     */
    private List<Problem> successErrorCheck() {
        List<Problem> list  = new ArrayList<>();

        String area = "SearchSuccess";
        int severityError= 2;
        String descriptionInvalid = "A row with neither a post or user clicked has been found for: ";
        String descriptionNoFinal = "A row with no FinalSearchStatsId has been found for: ";

        for(FinalSearchStatDLC f : finalSearchStatDLCRepo.findAll()) {
            if(f.getPostId() == null && f.getUserId() == null) {
                list.add(new Problem(severityError, descriptionInvalid + "SS DLC ID: " + f.getId() + "Final ID: " +f.getFinalSearchId(), area));
            }
            if(f.getFinalSearchId() == null) {
                list.add(new Problem(severityError, descriptionNoFinal + f.getId() + "Final ID: " +f.getFinalSearchId(), area));
            }
        }
        return list;
    }

    private List<Problem> findTypeProblems() {
        List<Problem> list = new ArrayList<>();
        list.addAll(newTypeFoundCheck());
        return list;
    }

    private List<Problem> newTypeFoundCheck() {
        List<Problem> list  = new ArrayList<>();

        String area = "PostTypes";
        int severityError= 3;
        String descriptionNewType = "A new Type has been found: ";
        String solutions = "Add the Type to newTypeFoundCheck or change generating algorithm";

        for(String type : postTypeRepo.getDistinctTypes()) {
            if(!type.equalsIgnoreCase("cyber-risk-check")
                    && !type.equalsIgnoreCase("artikel")
                    && !type.equalsIgnoreCase("news")
                    && !type.equalsIgnoreCase("Event: Sonstige")
                    && !type.equalsIgnoreCase("blog")
                    && !type.equalsIgnoreCase("whitepaper")
                    && !type.equalsIgnoreCase("Event: Kongress")
                    && !type.equalsIgnoreCase("Event: Schulung/Seminar")
                    && !type.equalsIgnoreCase("podcast_first_series")
                    && !type.equalsIgnoreCase("Event: Workshop")
                    && !type.equalsIgnoreCase("Event: Messe")
                    && !type.equalsIgnoreCase("videos")) {
                    list.add(new Problem(severityError, descriptionNewType + type, area, solutions));
            }
        }

        return list;
    }

    private List<Problem> findWebsiteProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }


}
