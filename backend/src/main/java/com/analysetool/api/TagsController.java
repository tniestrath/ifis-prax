package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.TagService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping(value = {"/tags", "/0wB4P2mly-xaRmeeDOj0_g/tags"}, method = RequestMethod.GET, produces = "application/json")
public class TagsController {

    @Autowired
    private TagService tagService;

    @GetMapping("/{id}")
    public ResponseEntity<WPTerm> getTermById(@PathVariable Long id) {return tagService.getTermById(id);}

    @GetMapping("/getall")
    List<WPTerm> getAll(){return tagService.getAll();}

    // weitere REST-Endpunkte, falls benötigt

    @GetMapping("/getPostTags")
    List<WPTerm>getPostTags(){return tagService.getPostTags();}

    @GetMapping("/getPostTagsIdName")
    String getPostTagsIdName() throws JSONException {return tagService.getPostTagsIdName();}

    @GetMapping("/getPostcount")
    String getPostCount(@RequestParam String id) {return tagService.getPostCount(id);}

    @GetMapping("/getTermRanking")
    String getTermRanking() throws JSONException {return tagService.getTermRanking();}

    @GetMapping("/getTagStat")
    public String getTagStat(@RequestParam Long id)throws JSONException{return tagService.getTagStat(id);}

    @GetMapping("/allTermsRelevanceAndCount")
    public String getTermsRelevanceCount() throws JSONException {return tagService.getTermsRelevanceCount();}


    @GetMapping("/allTermsRelevanceAndPerformance")
    public String getTermsRelevanceAndPerformance() throws JSONException {return tagService.getTermsRelevanceAndPerformance();}

    @GetMapping("/getPostCountAbove")
    public String getPostCountAbove(int percentage) {return tagService.getPostCountAbove(percentage);}


    @GetMapping("/getTop3")
    public String getTop3(String sorter) {return tagService.getTop3(sorter);}

    @GetMapping("/allTermsRelevanceAndViews")
    public String getTermsRelevanceAndViews() throws JSONException {return tagService.getTermsRelevanceAndViews();}

    @GetMapping("/getTagStatsSingle")
    public String getTagStatsSingle(int tagId, String start, String end) throws JSONException {return tagService.getTagStatsSingle(tagId, start, end);}

    @GetMapping("/getTagStatsDaysBack")
    public String getTagStatsSingle(int tagId, int daysBack) throws JSONException {return tagService.getTagStatsSingle(tagId, daysBack);}


    public JSONObject getTagStatsShort(int tagId) throws JSONException {return tagService.getTagStatsShort(tagId);}

    @GetMapping("/getTagStatsAll")
    public String getTagStatsAll(String sorter) throws JSONException {return tagService.getTagStatsAll(sorter);}

    @GetMapping("/getRelevance")
    public double getRelevance(int id) {return tagService.getRelevance(id);}

    @GetMapping("/getMaxRelevance")
    public double getMaxRelevance() {return tagService.getMaxRelevance();}
}


