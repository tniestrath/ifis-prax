package com.analysetool.api;

import com.analysetool.util.Problem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/self")
public class SelfDiagnosisController {

    //ToDo : Add Logic to partial checkups.

    /**
     * An aggregate methods to find Problems in all parts of the database.
     * @return an ordered JSONArray-String, containing information about all Problems that have been found.
     * @throws JSONException .
     */
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

        largeList.sort((o1, o2) -> o2.getSeverity() - o1.getSeverity());
        return largeList;
    }

    private List<Problem> findUniStatProblems() {
        List<Problem> list = new ArrayList<>();
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



}
