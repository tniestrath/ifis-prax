package com.analysetool.api;

import com.analysetool.modells.Post;
import com.analysetool.modells.WPTerm;
import com.analysetool.modells.WpTermTaxonomy;
import com.analysetool.modells.stats;
import com.analysetool.repositories.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        long viewsArtikel=0;
        long viewsProfile=userStatsRepo.findByUserId(id).getProfileView();
        int tagIdBlog= termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel= termRepo.findBySlug("artikel").getId().intValue();
       // int tagIdPresse= termRepo.findBySlug("blog");
        //long viewsPresse=0;
        List <Post>posts= postRepo.findByAuthor(id.intValue());

        List<Long> postTags = new ArrayList<>();
        for(Post post : posts) {
            if(statRepository.existsByArtId(post.getId())) {
                stats Stat = statRepository.getStatByArtID(post.getId());
                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {
                    for (WpTermTaxonomy termTax : taxTermRepo.findByTermTaxonomyId(l)) {
                        if (termTax.getTermId() == tagIdBlog) {
                        viewsBlog=viewsBlog+Stat.getClicks();
                        }
                        if (termTax.getTermId() == tagIdArtikel) {
                            viewsArtikel=viewsArtikel+Stat.getClicks();
                        }
                    }


                }
            }
        }
        JSONObject obj= new JSONObject();
        obj.put("viewsBlog",viewsBlog);
        obj.put("viewsArtikel",0);
        obj.put("viewsProfile",viewsProfile);
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
       obj.put("titel",postRepo.findById(PostId).get().getTitle());
       return obj.toString();
    }


}
