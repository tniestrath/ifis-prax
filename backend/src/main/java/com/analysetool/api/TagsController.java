package com.analysetool.api;

import com.analysetool.modells.WPTerm;
import com.analysetool.services.TagService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping(value = {"/tags", "/0wB4P2mly-xaRmeeDOj0_g/tags"}, method = RequestMethod.GET, produces = "application/json")
public class TagsController {

    @Autowired
    private TagService tagService;

    /**
     * Fetches a term by its id.
     * @param id the id to fetch for.
     * @return a WPTerm Representation.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WPTerm> getTermById(@PathVariable Long id) {return tagService.getTermById(id);}

    /**
     * Fetches a list of all terms.
     * @return a list of all terms.
     */
    @GetMapping("/getall")
    List<WPTerm> getAll(){return tagService.getAll();}

    // weitere REST-Endpunkte, falls benötigt

    /**
     * Fetches all tags that have been attached to posts.
     * @return a list of terms.
     */
    @GetMapping("/getPostTags")
    List<WPTerm>getPostTags(){return tagService.getPostTags();}

    /**
     * Fetches all Terms attached to posts with their id and name.
     * @return a JSON-String.
     * @throws JSONException .
     */
    @GetMapping("/getPostTagsIdName")
    String getPostTagsIdName() throws JSONException {return tagService.getPostTagsIdName();}

    /**
     * Fetches the number of posts using the specified tag.
     * @param id the term id of the tag to fetch for.
     * @return a String, containing the number of posts.
     */
    @GetMapping("/getPostcount")
    String getPostCount(@RequestParam String id) {return tagService.getPostCount(id);}

    /**
     * Fetch a ranked list of all terms, sorted by their posts count.
     * @return a JSON-String of ranked terms.
     * @throws JSONException .
     */
    @GetMapping("/getTermRanking")
    String getTermRanking() throws JSONException {return tagService.getTermRanking();}

    /**
     * Fetches stats for the specified tag.
     * @param id the term id to fetch for-
     * @return a JSON-String of the terms stats.
     * @throws JSONException .
     */
    @GetMapping("/getTagStat")
    public String getTagStat(@RequestParam Long id)throws JSONException{return tagService.getTagStat(id);}

    /**
     * Fetch Relevance and Post count for all tags.
     * @return a JSON-String of all terms, with their relevance and post-counts.
     * @throws JSONException .
     */
    @GetMapping("/allTermsRelevanceAndCount")
    public String getTermsRelevanceCount() throws JSONException {return tagService.getTermsRelevanceCount();}

    /**
     * Fetch relevance and performance for all tags.
     * @return a JSON-String of all terms, with their relevance and performance.
     * @throws JSONException .
     */
    @GetMapping("/allTermsRelevanceAndPerformance")
    public String getTermsRelevanceAndPerformance() throws JSONException {return tagService.getTermsRelevanceAndPerformance();}

    /**
     * Fetches all terms with more posts than the given percentage.
     * @param percentage the percentage of all posts that have to be in the term.
     * @return a JSON-String of all terms that fulfill the condition.
     */
    @GetMapping("/getPostCountAbove")
    public String getPostCountAbove(int percentage) {return tagService.getPostCountAbove(percentage);}

    /**
     * Fetch the top3 of all terms, sorted.
     * @param sorter what to sort by (relevance).
     * @return a JSON-String containing the top3 terms.
     */
    @GetMapping("/getTop3")
    public String getTop3(String sorter) {return tagService.getTop3(sorter);}

    /**
     * Fetch all terms with their relevance and posts views.
     * @return a JSON-String containing all terms with their relevance and views.
     * @throws JSONException .
     */
    @GetMapping("/allTermsRelevanceAndViews")
    public String getTermsRelevanceAndViews() throws JSONException {return tagService.getTermsRelevanceAndViews();}

    /**
     * Fetch a specific terms stats, in a specific date range.
     * @param tagId the term to fetch for.
     * @param start the start of the time period to fetch in.
     * @param end the end of the time period to fetch in.
     * @return a JSON-String containing Tag-Stats.
     * @throws JSONException .
     */
    @GetMapping("/getTagStatsSingle")
    public String getTagStatsSingle(int tagId, String start, String end) throws JSONException {return tagService.getTagStatsSingle(tagId, start, end);}

    /**
     * Fetch a specific terms stats, in a specific date range.
     * @param tagId the term to fetch for.
     * @param daysBack how many days back from today to start tracking.
     * @return a JSON-String containing Tag-Stats.
     * @throws JSONException .
     */
    @GetMapping("/getTagStatsDaysBack")
    public String getTagStatsSingle(int tagId, int daysBack) throws JSONException {return tagService.getTagStatsSingle(tagId, daysBack);}

    /**
     * Fetch all TagStats, sorted.
     * @param sorter what to sort by.
     * @return a JSON String containing Tag-Stats.
     * @throws JSONException .
     */
    @GetMapping("/getTagStatsAll")
    public String getTagStatsAll(String sorter) throws JSONException {return tagService.getTagStatsAll(sorter);}

    @GetMapping("/getTagStatsPageable")
    public String getTagStatsPageable(int page, int size, String sorter, String search) throws JSONException {return tagService.getTagStatsPageable(page, size, sorter, search);}

    /**
     * Fetch relevance of a specific term.
     * @param id the term to fetch for.
     * @return the relevance of the term.
     */
    @GetMapping("/getRelevance")
    public double getRelevance(int id) {return tagService.getRelevance(id);}

    /**
     * Fetch the highest relevance of all terms
     * @return the relevance of the term.
     */
    @GetMapping("/getMaxRelevance")
    public double getMaxRelevance() {return tagService.getMaxRelevance();}
}


