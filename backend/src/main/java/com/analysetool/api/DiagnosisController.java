package com.analysetool.api;

import com.analysetool.modells.UniversalStats;
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

    private List<Problem> findTypeProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }


    private List<Problem> findWebsiteProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }


}
