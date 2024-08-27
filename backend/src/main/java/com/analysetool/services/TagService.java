package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class TagService {

    @Autowired
    private WPTermRepository termRepository;
    @Autowired
    private WpTermTaxonomyRepository termTaxonomyRepository;
    @Autowired
    private TagStatRepository tagStatRepo;
    @Autowired
    private TagCatStatRepository tagCatRepo;
    @Autowired
    private WpTermRelationshipsRepository termRelRepo;
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private universalStatsRepository uniRepo;

    /**
     * Fetches a term by its id.
     * @param id the id to fetch for.
     * @return a WPTerm Representation.
     */
    public ResponseEntity<WPTerm> getTermById(@PathVariable Long id) {
        Optional<WPTerm> term = termRepository.findById(id);
        if (term.isPresent()) {
            return ResponseEntity.ok(term.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Fetches a list of all terms.
     * @return a list of all terms.
     */
    public List<WPTerm> getAll(){return termRepository.findAll();}

    // weitere REST-Endpunkte, falls benötigt

    /**
     * Fetches all tags that have been attached to posts.
     * @return a list of terms.
     */
    public List<WPTerm>getPostTags(){
        List<Long> li = termTaxonomyRepository.getAllPostTags();
        List<WPTerm> list = new ArrayList<>();
        for (Long l:li){
            Optional <WPTerm> optTerm =termRepository.findById(l);
            if(optTerm.isPresent()){
                list.add(optTerm.get());
            }
        }
        return list;
    }

    /**
     * Fetches all Terms attached to posts with their id and name.
     * @return a JSON-String.
     * @throws JSONException .
     */
    public String getPostTagsIdName() throws JSONException {
        List<Long> li = termTaxonomyRepository.getAllPostTags();
        JSONArray list = new JSONArray();

        for (Long l:li){
            Optional <WPTerm> optTerm =termRepository.findById(l);
            JSONObject jsonObject = new JSONObject();
            if(optTerm.isPresent()){
                jsonObject.put("id",optTerm.get().getId());
                jsonObject.put("name",optTerm.get().getName());
                list.put(jsonObject);

            }
        }
        System.out.println(list);
        return list.toString();
    }

    /**
     * Fetches the number of posts using the specified tag.
     * @param id the term id of the tag to fetch for.
     * @return a String, containing the number of posts.
     */
    public String getPostCount(@RequestParam String id) {
        return Long.toString(termRepository.getPostCount(id));
    }

    /**
     * Fetch a ranked list of all terms, sorted by their post-count.
     * @return a JSON-String of ranked terms.
     * @throws JSONException .
     */
    public String getTermRanking() throws JSONException {
        List<WpTermTaxonomy> list= termTaxonomyRepository.findTop10TermIdsByCount();
        JSONArray Antwort = new JSONArray();
        for (WpTermTaxonomy i:list){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put( "id",i.getTermId());
            //noinspection OptionalGetWithoutIsPresent
            jsonObject.put("name", termRepository.findById(i.getTermId()).get().getName());
            jsonObject.put("count",i.getCount());
            Antwort.put(jsonObject);
        }
        return Antwort.toString();

    }

    /**
     * Fetches stats for the specified tag.
     * @param id the term id to fetch for-
     * @return a JSON-String of the terms stats.
     * @throws JSONException .
     */
    public String getTagStat(@RequestParam Long id)throws JSONException{
        TagStat tagStat = tagStatRepo.getStatById(id.intValue()).get(0);
        JSONObject obj = new JSONObject();
        obj.put("Tag-Id",tagStat.getTagId());
        obj.put("Relevance",tagStatRepo.getRelevance(tagStat.getTagId()));
        obj.put("Search Successes",tagStat.getSearchSuccess());
        obj.put("Views",tagStat.getViews());
        return obj.toString();
    }

    /**
     * Fetch Relevance and Post count for all tags.
     * @return a JSON-String of all terms, with their relevance and post-counts.
     * @throws JSONException .
     */
    public String getTermsRelevanceCount() throws JSONException {
        List<WpTermTaxonomy> termTaxs = termTaxonomyRepository.findAll();
        JSONArray response = new JSONArray();
        for(WpTermTaxonomy tax:termTaxs){
            JSONObject obj = new JSONObject();
            if(tax.getTaxonomy().equals("post_tag")){
                //noinspection OptionalGetWithoutIsPresent
                obj.put("name",termRepository.findById(tax.getTermId()).get().getName());
                obj.put("id", tax.getTermId());
                if (tagStatRepo.existsByTagId(tax.getTermId().intValue())) { obj.put("relevance", tagStatRepo.getRelevance(tax.getTermId().intValue()));}
                else {obj.put("relevance", 0);}
                obj.put("count",tax.getCount());
                response.put(obj);
            }
        }
        return response.toString();

    }

    /**
     * Fetch relevance and performance for all tags.
     * @return a JSON-String of all terms, with their relevance and performance.
     * @throws JSONException .
     */
    public String getTermsRelevanceAndPerformance() throws JSONException {
        List<WpTermTaxonomy> termTaxs = termTaxonomyRepository.findAll();
        JSONArray response = new JSONArray();
        for(WpTermTaxonomy tax : termTaxs){
            JSONObject obj = new JSONObject();
            if(tax.getTaxonomy().equals("post_tag")){
                //noinspection OptionalGetWithoutIsPresent
                obj.put("name",termRepository.findById(tax.getTermId()).get().getName());
                if (tagStatRepo.existsByTagId(tax.getTermId().intValue())) {
                    obj.put("relevance", tagStatRepo.getRelevance(tax.getTermId().intValue()));
                }
                else {
                    obj.put("relevance", 0);
                    obj.put("performance", 0);
                }
                response.put(obj);
            }
        }

        return response.toString();
    }

    /**
     * Fetches all terms with more posts than the given percentage.
     * @param percentage the percentage of all posts that have to be in the term.
     * @return a JSON-String of all terms that fulfill the condition.
     */
    public String getPostCountAbove(int percentage) {
        HashMap<String, Long> map = new HashMap<>();

        for(Long id : termTaxonomyRepository.getCountAbove(percentage)) {
            map.put(termRepository.getNameById(id.intValue()), termTaxonomyRepository.getCountById(id.intValue()));
        }


        JSONObject json = new JSONObject(map);
        return json.toString();
    }


    /**
     * Fetch the top3 of all terms, sorted.
     * @param sorter what to sort by (relevance).
     * @return a JSON-String containing the top3 terms.
     */
    public String getTop3(String sorter) {
        List<Long> top3 = null;
        String errorString = "";
        if(sorter.equalsIgnoreCase("relevance")) {
            top3 = tagStatRepo.getTop3Relevance();
        }

        String jsonString = null;

        if(top3 == null) {
            errorString = "Wrong sorter / table error";
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                jsonString = objectMapper.writeValueAsString(top3);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                errorString = "JSON Mapping Error";
            }
        }
        System.out.println(errorString);
        return jsonString != null? jsonString : errorString;

    }

    /**
     * Fetch all terms with their relevance and posts views.
     * @return a JSON-String containing all terms with their relevance and views.
     * @throws JSONException .
     */
    public String getTermsRelevanceAndViews() throws JSONException {
        List<WpTermTaxonomy> termTaxs = termTaxonomyRepository.findAll();
        JSONArray response = new JSONArray();
        for(WpTermTaxonomy tax : termTaxs){
            JSONObject obj = new JSONObject();
            if(tax.getTaxonomy().equals("post_tag")){
                //noinspection OptionalGetWithoutIsPresent
                obj.put("name",termRepository.findById(tax.getTermId()).get().getName());
                obj.put("id", tax.getTermId());
                obj.put("count", getCount(tax.getTermId().intValue(), new Date()));
                if (tagStatRepo.existsByTagId(tax.getTermId().intValue())) {
                    obj.put("relevance", (tagStatRepo.getRelevance(tax.getTermId().intValue()) / tagStatRepo.getMaxRelevance()) * 100);
                    obj.put("views", tagStatRepo.getStatById(tax.getTermId().intValue()).get(0).getViews());
                }
                else {
                    obj.put("relevance", 0);
                    obj.put("views", 0);
                }
                response.put(obj);
            }
        }
        return response.toString();
    }

    /**
     * Fetch the number of posts released within the term and the date limit.
     * @param tagId the term to fetch for.
     * @param dateLimit the date-limit (earliest valid date).
     * @return the amount of posts.
     */
    public int getCount(int tagId,Date dateLimit){
        Long termTaxId= termTaxonomyRepository.findByTermId(tagId).getTermTaxonomyId();
        List<wp_term_relationships> termRel = termRelRepo.findByTermTaxonomyId(termTaxId);
        ArrayList<Post> posts = new ArrayList<>();
        for(wp_term_relationships t: termRel){
            @SuppressWarnings("OptionalGetWithoutIsPresent") Post post = postRepo.findById(t.getObjectId()).get();
            Date datePost =Date.from( post.getDate().atZone(ZoneId.systemDefault()).toInstant());
            if(datePost.before(dateLimit)){
                posts.add(post);
            }
        }
        return posts.size();
    }

    /**
     * Fetch a specific terms stats, in a specific date range.
     * @param tagId the term to fetch for.
     * @param start the start of the time period to fetch in.
     * @param end the end of the time period to fetch in.
     * @return a JSON-String containing Tag-Stats.
     * @throws JSONException .
     */
    public String getTagStatsSingle(int tagId, String start, String end) throws JSONException {

        java.sql.Date dateStart;
        java.sql.Date dateEnd;

        if(start.isBlank()) {
            dateStart = tagStatRepo.getEarliestTrackingForTag(tagId) >= tagCatRepo.getEarliestTrackingForTag(tagId) ? java.sql.Date.valueOf(uniRepo.getDateByUniId(tagStatRepo.getEarliestTrackingForTag(tagId)).toString()) : java.sql.Date.valueOf(uniRepo.getDateByUniId(tagCatRepo.getEarliestTrackingForTag(tagId)).toString());
        } else {
            dateStart = java.sql.Date.valueOf(start);
        }

        if(end.isBlank()) {
            dateEnd = java.sql.Date.valueOf(LocalDate.now().toString());
        } else {
            dateEnd = java.sql.Date.valueOf(end);
        }


        if (dateStart.after(dateEnd)) {
            java.sql.Date datePuffer = dateEnd;
            dateEnd = dateStart;
            dateStart = datePuffer;
        }

        JSONArray array = new JSONArray();

        for (LocalDate date : dateStart.toLocalDate().datesUntil(dateEnd.toLocalDate().plusDays(1)).toList()) {

            int uniId = 0;

            //Check if we have stats for the day we are checking
            if (uniRepo.findByDatum(java.sql.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniRepo.findByDatum(java.sql.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            JSONObject json = new JSONObject();

            json.put("count", getCount(tagId, Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            json.put("viewsPosts", tagStatRepo.getViewsByTagIdAndUniId(tagId, uniId) == null ? 0 : tagStatRepo.getViewsByTagIdAndUniId(tagId, uniId));
            json.put("viewsCat", tagCatRepo.getViewsByTagIdAndUniId(tagId, uniId) == null ? 0 : tagCatRepo.getViewsByTagIdAndUniId(tagId, uniId));
            json.put("date", date.toString());

            array.put(json);
        }

        return array.toString();
    }

    /**
     * Fetch a specific terms stats, in a specific date range.
     * @param tagId the term to fetch for.
     * @param daysBack how many days back from today to start tracking.
     * @return a JSON-String containing Tag-Stats.
     * @throws JSONException .
     */
    public String getTagStatsSingle(int tagId, int daysBack) throws JSONException {
        LocalDate dateEnd = LocalDate.now();
        return getTagStatsSingle(tagId, dateEnd.minusDays(daysBack).toString(), dateEnd.toString());
    }

    /**
     * Fetch a shortened representation of a terms stats.
     * @param tagId the term to fetch for.
     * @return a JSON-Object containing the tags stats (count, name, viewsPosts, viewsCat, id).
     * @throws JSONException .
     */
    public JSONObject getTagStatsShort(int tagId) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("count", getCount(tagId, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())));
        json.put("name", termRepository.getNameById(tagId));
        json.put("viewsPosts", tagStatRepo.getSumOfViewsForTag(tagId) == null ? 0 : tagStatRepo.getSumOfViewsForTag(tagId));
        json.put("viewsCat", tagCatRepo.getSumOfViewsForTag(tagId) == null ? 0 : tagCatRepo.getSumOfViewsForTag(tagId));
        json.put("id", tagId);

        return json;
    }

    /**
     * Fetch all TagStats, sorted.
     * @param sorter what to sort by.
     * @return a JSON String containing Tag-Stats.
     * @throws JSONException .
     */
    public String getTagStatsAll(String sorter) throws JSONException {
        List<JSONObject> array = new ArrayList<>();

        for(long tagId : termTaxonomyRepository.getTermIdsOfPostTags()) {
            array.add(getTagStatsShort((int) tagId));
        }

        if(sorter == null || sorter.isBlank()) {
            sorter = "viewsTotal";
        }

        switch(sorter) {
            case "viewsPosts" -> {
                array.sort((o1, o2) -> {
                    try {
                        return o2.getInt("viewsPosts") - o1.getInt("viewsPosts");
                    } catch (JSONException ignored) {}
                    return 0;
                });
            }
            case "viewsCat" -> {
                array.sort((o1, o2) -> {
                    try {
                        return o2.getInt("viewsCat") - o1.getInt("viewsCat");
                    } catch (JSONException ignored) {}
                    return 0;
                });
            }
            case "count" -> {
                array.sort((o1, o2) -> {
                    try {
                        return o2.getInt("count") - o1.getInt("count");
                    } catch (JSONException ignored) {}
                    return 0;
                });
            }
            case "viewsTotal" ->  {
                array.sort((o1, o2) -> {
                    try {
                        return (o2.getInt("viewsPosts") + o2.getInt("viewsCat")) - (o1.getInt("viewsPosts") + o1.getInt("viewsCat"));
                    } catch (JSONException ignored) {}
                    return 0;
                });
            }
        }
        JSONArray jsonArray = new JSONArray(array);
        return jsonArray.toString();
    }

    /**
     * Fetch a page of TagStats, sorted.
     * @param page the page to load.
     * @param size the amount of results to load.
     * @param sorter what to sort by.
     * @return a JSON String containing Tag-Stats.
     * @throws JSONException .
     */
    public String getTagStatsPageable(int page, int size, String sorter) throws JSONException {
        JSONArray array = new JSONArray();
        List<Long> tags;

        PageRequest pageable = PageRequest.of(page, size);

        //Currently expecting either "viewsTotal" or "count"
        if(sorter.equals("viewsTotal")) {
            tags = tagStatRepo.getOrderedByTotalViews(pageable);
        } else {
            tags = termTaxonomyRepository.getTagIdsByPostCount(pageable);
        }

        for(Long tagId : tags) {
            JSONObject json = getTagStatsShort(Math.toIntExact(tagId));
            array.put(json);
        }
        return array.toString();
    }

    /**
     * Fetch relevance of a specific term.
     * @param id the term to fetch for.
     * @return the relevance of the term.
     */
    public double getRelevance(int id) {
        return tagStatRepo.getRelevance(id);
    }

    /**
     * Fetch the highest relevance of all terms
     * @return the relevance of the term.
     */
    public double getMaxRelevance() {
        return tagStatRepo.getMaxRelevance();
    }

}
