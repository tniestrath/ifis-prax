package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
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
@RequestMapping("/tags")
public class TagsController {

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

    @GetMapping("/{id}")
    public ResponseEntity<WPTerm> getTermById(@PathVariable Long id) {
        Optional<WPTerm> term = termRepository.findById(id);
        if (term.isPresent()) {
            return ResponseEntity.ok(term.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getall")
    List<WPTerm> getall(){return termRepository.findAll();}

    // weitere REST-Endpunkte, falls benötigt

    @GetMapping("/getPostTags")
    List<WPTerm>getPostTags(){
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

    @GetMapping("/getPostTagsIdName")
    String getPostTagsIdName() throws JSONException {
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

    @GetMapping("/getPostcount")
    String getPostCount(@RequestParam String id) {
        return Long.toString(termRepository.getPostCount(id));
    }

    @GetMapping("/getTermRanking")
    String getTermRanking() throws JSONException {
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

    @GetMapping("/getTagStat")
    public String getTagStat(@RequestParam Long id)throws JSONException{
        TagStat tagStat = tagStatRepo.getStatById(id.intValue()).get(0);
        JSONObject obj = new JSONObject();
        obj.put("Tag-Id",tagStat.getTagId());
        obj.put("Relevance",tagStatRepo.getRelevance(tagStat.getTagId()));
        obj.put("Search Successes",tagStat.getSearchSuccess());
        obj.put("Views",tagStat.getViews());
        return obj.toString();
    }

    @GetMapping("/allTermsRelevanceAndCount")
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


    @GetMapping("/allTermsRelevanceAndPerformance")
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

    @GetMapping("/getPostCountAbove")
    public String getPostCountAbove(int percentage) {
        HashMap<String, Long> map = new HashMap<>();

        for(Long id : termTaxonomyRepository.getCountAbove(percentage)) {
            map.put(termRepository.getNameById(id.intValue()), termTaxonomyRepository.getCountById(id.intValue()));
        }


        JSONObject json = new JSONObject(map);
        return json.toString();
    }


    @GetMapping("/getTop3")
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

    @GetMapping("/allTermsRelevanceAndViews")
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

    public static float getRelevance2(HashMap<String, Long> viewsLastYear, String currentDateString, int time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd.MM");

        // Add the current year to the date string
        String year = String.valueOf(LocalDate.now().getYear());
        LocalDate currentDate = LocalDate.parse(year + "-" + currentDateString, formatter);

        long views = 0;

        for (int i = 0; i < time; i++) {
            String dateKey = currentDate.minusDays(i).format(DateTimeFormatter.ofPattern("dd.MM"));
            views += viewsLastYear.getOrDefault(dateKey, 0L);
        }

        return (float) views / time;
    }



    public float getRelevance(HashMap<String,Long>viewsLastYear,int currentDayOfYear,int time){
        int counter =currentDayOfYear-time;
        long views=0;
        while(counter<=currentDayOfYear){
            if (viewsLastYear.containsKey(Integer.toString(counter))){
                views=views+(viewsLastYear.get(Integer.toString(counter)));
            }
            counter++;
        }
        return (float)views/time;
    }

    public static Date getDate(int zuruek) throws ParseException {
        String dateString = LocalDate.now(ZoneId.systemDefault()).minusDays(zuruek).format(DateTimeFormatter.ISO_DATE);
        Date vortag = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        return vortag;
    }
    public static int getDayOfYear(){
        LocalDate today = LocalDate.now();
        int dayOfYear = today.getDayOfYear();
        return dayOfYear;
    }
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


    @GetMapping("/getTagStatsSingle")
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

    @GetMapping("/getTagStatsDaysBack")
    public String getTagStatsSingle(int tagId, int daysBack) throws JSONException {
        LocalDate dateEnd = LocalDate.now();
        return getTagStatsSingle(tagId, dateEnd.minusDays(daysBack).toString(), dateEnd.toString());
    }


    public JSONObject getTagStatsShort(int tagId) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("count", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        json.put("name", termRepository.getNameById(tagId));
        json.put("viewsPosts", tagStatRepo.getSumOfViewsForTag(tagId) == null ? 0 : tagStatRepo.getSumOfViewsForTag(tagId));
        json.put("viewsCat", tagCatRepo.getSumOfViewsForTag(tagId) == null ? 0 : tagCatRepo.getSumOfViewsForTag(tagId));

        return json;
    }

    @GetMapping("/getTagStatsAll")
    public String getTagStatsAll(String sorter) throws JSONException {
        List<JSONObject> array = new ArrayList<>();

        for(long tagId : termTaxonomyRepository.getTermIdsOfPostTags()) {
            array.add(getTagStatsShort((int) tagId));
        }

        if(sorter.isBlank()) {
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

    @GetMapping("/getRelevance")
    public double getRelevance(int id) {
        return tagStatRepo.getRelevance(id);
    }

    @GetMapping("/getMaxRelevance")
    public double getMaxRelevance() {
        return tagStatRepo.getMaxRelevance();
    }
}


