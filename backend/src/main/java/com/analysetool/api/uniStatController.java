package com.analysetool.api;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/bericht")
public class uniStatController {

    @Autowired
    universalStatsRepository uniRepo;

    @GetMapping("/gestern")
    public String getLetzte() throws JSONException {
        JSONObject obj = new JSONObject();
        universalStats uniStat=uniRepo.findAll().get(uniRepo.findAll().size()-1);
        obj.put("Datum",uniStat.getDatum());
        obj.put("Besucher",uniStat.getBesucherAnzahl());
        obj.put("Angemeldete Profile",uniStat.getAnbieterProfileAnzahl());
        obj.put("veröffentlichte Artikel",uniStat.getAnzahlArtikel());
        obj.put("veröffentlichte Blogs",uniStat.getAnzahlBlog());
        obj.put("veröffentlichte News",uniStat.getAnzahlNews());

        return obj.toString();
    }

}
