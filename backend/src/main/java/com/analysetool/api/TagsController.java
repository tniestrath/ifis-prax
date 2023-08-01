package com.analysetool.api;

import com.analysetool.modells.TagStat;
import com.analysetool.modells.WPTerm;
import com.analysetool.modells.WpTermTaxonomy;
import com.analysetool.repositories.TagStatRepository;
import com.analysetool.repositories.WPTermRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.analysetool.repositories.WpTermTaxonomyRepository;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.analysetool.api.PostController.top3Helper;

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

    @GetMapping("/{id}")
    public ResponseEntity<WPTerm> getTermById(@PathVariable Long id) {
        Optional<WPTerm> term = termRepository.findById(id);
        return term.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getAll")
    List<WPTerm> getAll(){return termRepository.findAll();}
    // weitere REST-Endpunkte, falls benötigt
    @GetMapping("/getPostTags")
    List<WPTerm>getPostTags(){
        List<Long> li = termTaxonomyRepository.getAllPostTags();
        List<WPTerm> list = new ArrayList<>();
        for (Long l:li){
            Optional <WPTerm> optTerm =termRepository.findById(l);
            optTerm.ifPresent(list::add);
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

    @GetMapping("/getPostCount")
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
          jsonObject.put("name", termRepository.findById(i.getTermId()).get().getName());
          jsonObject.put("count",i.getCount());
          Antwort.put(jsonObject);
       }
       return Antwort.toString();

    }

    @GetMapping("/getTagStat")
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
        List<WpTermTaxonomy> termTaxs = termTaxonomyRepository.findAll();
        JSONArray response = new JSONArray();
        for(WpTermTaxonomy tax:termTaxs){
            JSONObject obj = new JSONObject();
            if(tax.getTaxonomy().equals("post_tag")){
                obj.put("name",termRepository.findById(tax.getTermId()).get().getName());
                obj.put("id", tax.getTermId());
                if (tagStatRepo.existsByTagId(tax.getTermId().intValue())) { obj.put("relevance", tagStatRepo.getStatById(tax.getTermId().intValue()).getRelevance());}
                else {obj.put("relevance", 0);}
                obj.put("count",tax.getCount());
                response.put(obj);
            }
        }
        return response.toString();

    }


    @GetMapping("/getTop3")
    public String getTop3(String sorter) {
        return top3Helper(sorter, tagStatRepo.getTop3Relevance(), tagStatRepo.getTop3Performance());

    }
}


