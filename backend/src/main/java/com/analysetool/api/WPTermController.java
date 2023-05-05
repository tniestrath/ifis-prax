package com.analysetool.api;

import com.analysetool.modells.WPTerm;
import com.analysetool.modells.WpTermTaxonomy;
import com.analysetool.repositories.WPTermRepository;
import com.mysql.cj.xdevapi.JsonArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.analysetool.repositories.WpTermTaxonomyRepository;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@CrossOrigin
@RestController
//@RequestMapping("/terms")
public class WPTermController {

    @Autowired
    private WPTermRepository termRepository;
    @Autowired
    private WpTermTaxonomyRepository termTaxonomyRepository;

    @GetMapping("terms/{id}")
    public ResponseEntity<WPTerm> getTermById(@PathVariable Long id) {
        Optional<WPTerm> term = termRepository.findById(id);
        if (term.isPresent()) {
            return ResponseEntity.ok(term.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("terms/getall")
    List<WPTerm> getall(){return termRepository.findAll();}
    // weitere REST-Endpunkte, falls benötigt
    @GetMapping("terms/getPostTags")
    List<WPTerm>getPostTags(){
        List<Long> li = termTaxonomyRepository.getAllPostTags();
        List<WPTerm> list = new ArrayList<WPTerm>();
        for (Long l:li){
            Optional <WPTerm> optTerm =termRepository.findById(l);
             if(optTerm.isPresent()){
                 list.add(optTerm.get());
             }
        }
        return list;
    }
/*
    @GetMapping(value="terms/getPostTagsIdName", produces = MediaType.APPLICATION_JSON_VALUE)
    JSONArray getPostTagsIdName() throws JSONException {
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
        return list;
    }
    */
    @GetMapping("terms/getPostTagsIdName")
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

    @GetMapping("terms/getPostcount")
    String getPostCount(@RequestParam String id) {
        return Long.toString(termRepository.getPostCount(id));
    }

    @GetMapping("terms/getTermRanking")
    String getTermRanking() throws JSONException {
       List<WpTermTaxonomy> list= termTaxonomyRepository.findTop10TermIdsByCount();
        JSONArray Antwort = new JSONArray();
       for (WpTermTaxonomy i:list){
           JSONObject jsonObject=new JSONObject();
          jsonObject.put( "tagId",i.getTermId());
          jsonObject.put("tag", termRepository.findById(i.getTermId()).get().getName());
          jsonObject.put("count",i.getCount());
          Antwort.put(jsonObject);
       }
       return Antwort.toString();

    }
}


