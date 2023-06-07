package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/stats")
public class statsController {
    @Autowired
    private statsRepository statRepository;
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private UserStatsRepository userStatsRepo;
    @Autowired
    private TagStatRepository tagStatRepo;
    @Autowired
    private WPTermRepository termRepo;
    @Autowired
    private WpTermRelationshipsRepository termRelRepo;
    @Autowired
    private WpTermTaxonomyRepository taxTermRepo;

/*
    @PostMapping
    public stats createStat(@RequestBody stats stat) {
        return statRepository.save(stat);
    }
*/
    @GetMapping("/{id}")
    public Optional<stats> getStat(@PathVariable Long id) {
        return statRepository.findById(id);
    }

    @GetMapping
    public List<stats> getAllStats() {
        return statRepository.findAll();
    }

    @GetMapping("/maxPerformance")
    public float getMaxPerformance(){
        return statRepository.getMaxPerformance();
    }

    @GetMapping("/maxRelevance")
    public float getMaxRelevance(){
        return statRepository.getMaxRelevance();
    }
    @GetMapping("/getPerformanceByArtId")
    public float getPerformanceByArtId(@RequestParam int id){
        return statRepository.getPerformanceByArtID(id);
    }

    @GetMapping("/getViewsBrokenDown")
    public String getViewsBrokenDown(@RequestParam Long id) throws JSONException {
        long viewsBlog = 0;
        long viewsArtikel = 0;
        long viewsProfile = userStatsRepo.findByUserId(id).getProfileView();
        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();

        int tagIdPresse = termRepo.findBySlug("pressemitteilung").getId().intValue();
        long viewsPresse = 0;
        List<Post> posts = postRepo.findByAuthor(id.intValue());

        List<Long> postTags = new ArrayList<>();
        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                stats Stat = statRepository.getStatByArtID(post.getId());
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : taxTermRepo.findByTermTaxonomyId(l)) {
                        if (termTax.getTermId() == tagIdBlog) {
                            viewsBlog = viewsBlog + Stat.getClicks();
                        }
                        if (termTax.getTermId() == tagIdArtikel) {
                            viewsArtikel = viewsArtikel + Stat.getClicks();
                        }
                        if (termTax.getTermId() == tagIdPresse) {
                            viewsPresse = viewsPresse + Stat.getClicks();
                        }}


                    }
                }
            }
            JSONObject obj = new JSONObject();
            obj.put("viewsBlog", viewsBlog);
            obj.put("viewsArtikel", viewsArtikel);
            obj.put("viewsPresse", viewsPresse);
            obj.put("viewsProfile", viewsProfile);
            return obj.toString();

        }

 /*
    @PutMapping("/{id}")
    public stats updateStat(@PathVariable Long id, @RequestBody stats stat) {
        Optional<stats> existingStat = statRepository.findById(id);
        if (existingStat.isPresent()) {
            stat.setId(existingStat.get().getId());
            return statRepository.save(stat);
        } else {
            throw new ResourceNotFoundException("Stat not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteStat(@PathVariable Long id) {
        statRepository.deleteById(id);
    }*/
    @GetMapping("/bestPost")
    public String getBestPost(@RequestParam Long id, @RequestParam String type) throws JSONException {
       List<Post> Posts = postRepo.findByAuthor(id.intValue());
       stats Stats = null;
       float max = 0;
       long PostId=0;
       for(Post post:Posts){
           if(statRepository.existsByArtId(post.getId())){
               Stats = statRepository.getStatByArtID(post.getId());
               if(type.equals("relevance")){
                   if(Stats.getRelevance()>max){max = Stats.getRelevance();PostId= Stats.getArtId();}
               }
               if(type.equals("performance")){
                   if(Stats.getPerformance()>max){max = Stats.getPerformance();PostId= Stats.getArtId();}
               }
           }
       }
       JSONObject obj = new JSONObject();
       obj.put("ID",PostId);
       obj.put(type,max);
       obj.put("title",postRepo.findById(PostId).get().getTitle());
       return obj.toString();
    }

    @GetMapping("/getPostStat")
    public String getStat2(@RequestParam Long id) throws JSONException {
        stats Stat = statRepository.getStatByArtID(id);
        JSONObject obj = new JSONObject();
        obj.put("Post-Id",Stat.getArtId());
        obj.put("Relevanz",Stat.getRelevance());
        obj.put("Performance",Stat.getPerformance());
        obj.put("Views",Stat.getClicks());
        obj.put("Refferings",Stat.getReferrings());
        obj.put("Article Reffering Rate",Stat.getArticleReferringRate());
        obj.put("Search Successes",Stat.getSearchSucces());
        obj.put("Search Success Rate",Stat.getSearchSuccessRate());

        return obj.toString();
    }
    @GetMapping("getTagStat")
    public String getTagStat(@RequestParam Long id)throws JSONException{
        TagStat tagStat = tagStatRepo.getStatById(id.intValue());
        JSONObject obj = new JSONObject();
        obj.put("Tag-Id",tagStat.getTagId());
        obj.put("Relevance",tagStat.getRelevance());
        obj.put("Search Successes",tagStat.getSearchSuccess());
        obj.put("Views",tagStat.getViews());
        return obj.toString();
    }

    @GetMapping("/allTermsRelevanceAndCount")
    public String getTermsRelevanceCount() throws JSONException {
    List<WpTermTaxonomy> termTaxs = taxTermRepo.findAll();
    JSONArray response = new JSONArray();
    for(WpTermTaxonomy tax:termTaxs){
        JSONObject obj = new JSONObject();
        if(tax.getTaxonomy().equals("post_tag")){
            obj.put("name",termRepo.findById(tax.getTermId()).get().getName());
            obj.put("id", tax.getTermId());
            if (tagStatRepo.existsByTagId(tax.getTermId().intValue())) { obj.put("relevance", tagStatRepo.getStatById(tax.getTermId().intValue()).getRelevance());}
            else {obj.put("relevance", 0);}
            obj.put("count",tax.getCount());
            response.put(obj);
        }
    }
    return response.toString();
    }

    @GetMapping("/getNewestStatsByAuthor")
    public String getNewestStatsByAuthor(@RequestParam Long id) throws JSONException{
        List<Post> posts =postRepo.findByAuthor(id.intValue()) ;
        long newestId = 0 ;
        LocalDateTime newestTime = null ;
        for(Post post : posts){
            if(newestTime == null || newestTime.isBefore(post.getDate())){
                newestTime = post.getDate();
                newestId = post.getId();
            }
        }
        long views = 0;
        long searchSuccesses = 0;
        float SearchSuccessRate = 0 ;
        long refferings = 0;
        float refrate = 0;
        float relevanz = 0;
        float performance = 0 ;

        if(statRepository.existsByArtId(newestId)){
            stats Stats = statRepository.getStatByArtID(newestId);
            views = Stats.getClicks();
            searchSuccesses = Stats.getSearchSuccess();
            SearchSuccessRate = Stats.getSearchSuccessRate();
            refferings = Stats.getRefferings();
            refrate = Stats.getArticleReferringRate();
            relevanz = Stats.getRelevance();
            performance = Stats.getPerformance();
        }
        JSONObject obj = new JSONObject();
        obj.put("ID",newestId);
        obj.put("views",views);
        obj.put("Search Successes",searchSuccesses);
        obj.put("Search Success Rate",SearchSuccessRate);
        obj.put("refferings",refferings);
        obj.put("article reffering rate",refrate);
        obj.put("relevanz",relevanz);
        obj.put("performance",performance);


        return obj.toString();

    }


}
