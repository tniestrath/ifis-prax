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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/bericht")
public class uniStatController {

    @Autowired
    universalStatsRepository uniRepo;

    @GetMapping(value = "/gestern", produces = MediaType.TEXT_HTML_VALUE)
    public String getLetzte() throws JSONException {
        JSONObject obj = new JSONObject();
        universalStats uniStat=uniRepo.findAll().get(uniRepo.findAll().size()-1);
        obj.put("Datum",uniStat.getDatum());
        obj.put("Besucher",uniStat.getBesucherAnzahl());

        obj.put("Angemeldete Profile",uniStat.getAnbieterProfileAnzahl());
        obj.put("Angemeldete Basic Profile",uniStat.getAnbieterBasicAnzahl());
        obj.put("Angemeldete Basic-Plus Profile",uniStat.getAnbieterBasicPlusAnzahl());
        obj.put("Angemeldete Plus Profile",uniStat.getAnbieterPlusAnzahl());
        obj.put("Angemeldete Premium Profile",uniStat.getAnbieterPremiumAnzahl());
        obj.put("Angemeldete Premium Sponsoren Profile",uniStat.getAnbieterPremiumSponsorenAnzahl());

        obj.put("veröffentlichte Artikel",uniStat.getAnzahlArtikel());
        obj.put("veröffentlichte Blogs",uniStat.getAnzahlBlog());
        obj.put("veröffentlichte News",uniStat.getAnzahlNews());

        obj.put("aktueller jährlicher Umsatz",uniStat.getUmsatz());

        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Bericht - "+ new SimpleDateFormat("dd.MM.yyyy").format(uniStat.getDatum()) + "</title>\n" +
                "    <style>" +
                "       table{\n" +
                "           border: 1px solid black;\n" +
                "           width: 100%;\n"+
                "           text-align: center;\n"+
                "       }\n" +
                "       tr{\n" +
                "           border-bottom: 2px solid black;\n" +
                "           height: 20px;\n"+
                "       }" +
                "    </style>"+
                "</head>\n" +
                "<body>\n" +
                "    <h1>Datum: "+ new SimpleDateFormat("dd.MM.yyyy").format(uniStat.getDatum()) + "</h1>\n" +
                "    <table>\n" +
                "        <tr>\n"+
                "           <th>Besucher</th>\n" +
                "           <th>Angemeldete Benutzer</th>\n" +
                "           <th>Artikel</th>\n" +
                "           <th>Blogs</th>\n" +
                "           <th>News</th>\n" +
                "        </tr>\n"+
                "        <tr>\n" +
                "            <td>"+ obj.get("Besucher") +"</td>\n" +
                "            <td>"+ obj.get("Angemeldete Profile") +"</td>\n" +
                "            <td>"+ obj.get("veröffentlichte Artikel") +"</td>\n" +
                "            <td>"+ obj.get("veröffentlichte Blogs") +"</td>\n" +
                "            <td>"+ obj.get("veröffentlichte News") +"</td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";

        return html;
    }



}
