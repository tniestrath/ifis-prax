package com.analysetool.api;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
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

    @Autowired
    private WPUserMetaRepository wpUserMetaRepository;

    @Autowired
    private WPTermRepository termRepo;
    @Autowired
    private PostStatsRepository postStatsRepo;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private WpTermRelationshipsRepository termRelRepo;
    @Autowired
    private WpTermTaxonomyRepository termTaxRepo;


    public int getAdminCount() {
        int adminCount = 0;
        for(String cap : wpUserMetaRepository.getWpCapabilities()) {
            if(cap.contains("administrator")){ adminCount++;}
        }
        return adminCount;
    }

    @GetMapping(value = "/callups")
    public String getCallupsByTime(@RequestParam() int days) throws JSONException, ParseException {
        JSONArray response = new JSONArray();

        String dateString = LocalDate.now(ZoneId.systemDefault()).minusDays(days).format(DateTimeFormatter.ISO_DATE);

        List<UniversalStats> universalStatsList = uniRepo.getAllByDatumAfter(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
        for (UniversalStats uniStat : universalStatsList) {
            JSONObject callup = new JSONObject();
            callup.put("date", new SimpleDateFormat("yyyy-MM-dd").format(uniStat.getDatum()));
            callup.put("clicks", uniStat.getTotalClicks());
            callup.put("visitors", uniStat.getBesucherAnzahl());

            response.put(callup);
        }
        return response.toString();
    }

    @GetMapping(value = "/gestern", produces = MediaType.TEXT_HTML_VALUE)
    public String getLetzte() throws JSONException {
        JSONObject obj = new JSONObject();
        UniversalStats uniStat=uniRepo.findAll().get(uniRepo.findAll().size()-1);
        obj.put("Datum",uniStat.getDatum());
        obj.put("Besucher",uniStat.getBesucherAnzahl());

        obj.put("Angemeldete Profile",uniStat.getAnbieterProfileAnzahl() - getAdminCount());
        obj.put("Angemeldete Nutzer ohne Abo", uniStat.getAnbieter_abolos_anzahl());
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
    @GetMapping(value = "/letzte7Tage", produces = MediaType.TEXT_HTML_VALUE)
    public String getLast7Days() throws JSONException {
        List<UniversalStats> last7DaysStats = uniRepo.findTop7ByOrderByDatumDesc();
        Collections.reverse(last7DaysStats);

        StringBuilder tableRows = new StringBuilder();

        // Header für die Tabelle
        tableRows.append("<tr>\n");
        tableRows.append("<th>Datum</th>\n");
        tableRows.append("<th>Besucher</th>\n");
        tableRows.append("<th>Angemeldete Profile</th>\n");
        tableRows.append("<th>User ohne Abo</th>\n");
        tableRows.append("<th>Basic Profile</th>\n");
        tableRows.append("<th>Basic-Plus Profile</th>\n");
        tableRows.append("<th>Plus Profile</th>\n");
        tableRows.append("<th>Premium Profile</th>\n");
        tableRows.append("<th>Premium Sponsoren Profile</th>\n");
        tableRows.append("<th>Artikel</th>\n");
        tableRows.append("<th>Blogs</th>\n");
        tableRows.append("<th>News</th>\n");
        //tableRows.append("<th>Umsatz</th>\n");
        tableRows.append("</tr>\n");

        for (UniversalStats uniStat : last7DaysStats) {
            JSONObject obj = new JSONObject();
            obj.put("Datum", new SimpleDateFormat("dd.MM.yyyy").format(uniStat.getDatum()));
            obj.put("Besucher", uniStat.getBesucherAnzahl());
            obj.put("Angemeldete Profile", uniStat.getAnbieterProfileAnzahl() - getAdminCount());
            obj.put("Angemeldete Nutzer ohne Abo", uniStat.getAnbieter_abolos_anzahl());
            obj.put("Angemeldete Basic Profile", uniStat.getAnbieterBasicAnzahl());
            obj.put("Angemeldete Basic-Plus Profile", uniStat.getAnbieterBasicPlusAnzahl());
            obj.put("Angemeldete Plus Profile", uniStat.getAnbieterPlusAnzahl());
            obj.put("Angemeldete Premium Profile", uniStat.getAnbieterPremiumAnzahl());
            obj.put("Angemeldete Premium Sponsoren Profile", uniStat.getAnbieterPremiumSponsorenAnzahl());
            obj.put("veröffentlichte Artikel", uniStat.getAnzahlArtikel());
            obj.put("veröffentlichte Blogs", uniStat.getAnzahlBlog());
            obj.put("veröffentlichte News", uniStat.getAnzahlNews());
            //obj.put("aktueller jährlicher Umsatz", uniStat.getUmsatz());

            tableRows.append("<tr>\n");
            tableRows.append("<td>").append(obj.get("Datum")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Besucher")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Angemeldete Profile")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Angemeldete Nutzer ohne Abo")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Angemeldete Basic Profile")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Angemeldete Basic-Plus Profile")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Angemeldete Plus Profile")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Angemeldete Premium Profile")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("Angemeldete Premium Sponsoren Profile")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("veröffentlichte Artikel")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("veröffentlichte Blogs")).append("</td>\n");
            tableRows.append("<td>").append(obj.get("veröffentlichte News")).append("</td>\n");
           // tableRows.append("<td>").append(obj.get("aktueller jährlicher Umsatz")).append("</td>\n");
            tableRows.append("</tr>\n");
        }


        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Bericht für die letzten 7 Tage</title>\n" +
                "    <style>\n" +
                "       table {\n" +
                "           border: 1px solid black;\n" +
                "           width: 100%;\n" +
                "           text-align: center;\n" +
                "       }\n" +
                "       tr {\n" +
                "           border-bottom: 2px solid black;\n" +
                "           height: 20px;\n" +
                "       }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Statistik für die letzten 7 Tage</h1>\n" +
                "    <table>\n" +
                tableRows.toString() +
                "    </table>\n" +
                "</body>\n" +
                "</html>";

        return html;
    }

    @GetMapping("/getAccountTypeAllYesterday")
    public String getAccTypes() {
        HashMap<String, Long> map = new HashMap<>();
        UniversalStats uni = uniRepo.findTop1ByOrderByDatumDesc();

        map.put("Anbieter", uni.getAnbieter_abolos_anzahl());
        map.put("Basic", uni.getAnbieterBasicAnzahl());
        map.put("Basic-Plus", uni.getAnbieterBasicPlusAnzahl());
        map.put("Plus", uni.getAnbieterPlusAnzahl());
        map.put("Premium", uni.getAnbieterPremiumAnzahl());
        map.put("Sponsor", uni.getAnbieterPremiumSponsorenAnzahl());


        return new JSONObject(map).toString();

    }
    public JSONObject getClickOfDayAsJson(long id,int daysBack) throws JSONException {

        JSONObject obj = new JSONObject();
        PostStats stats =postStatsRepo.getStatByArtID(id);
        long clicks = 0;
        HashMap<String,Long> allClicks = (HashMap<String, Long>) stats.getViewsLastYear();
        LocalDate heute = LocalDate.now();
        // Datum, das n Tage zurückliegt
        LocalDate vergangenesDatum = heute.minusDays(daysBack);
        // Datum in dd.MM-Format umwandeln
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        String formatiertesDatum = vergangenesDatum.format(formatter);

        clicks=allClicks.get(formatiertesDatum);

        obj.put("iD",id);
        obj.put("name",postRepository.findById(id).get().getTitle());
        obj.put("clicks of day",clicks);
        return obj;
    }

    @GetMapping("/getTop5ByClicksAndDaysBackAndType")
    public String getTop5ByClicks(@RequestParam String type, @RequestParam int daysBack) throws JSONException {
        JSONArray ergebnis = new JSONArray();

        List<Post> posts = postRepository.findAllUserPosts();

        int tagIdBlog = termRepo.findBySlug("blog").getId().intValue();
        int tagIdArtikel = termRepo.findBySlug("artikel").getId().intValue();
        int tagIdPresse = termRepo.findBySlug("news").getId().intValue();

        for (Post post : posts) {


                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {

                    for (WpTermTaxonomy termTax : termTaxRepo.findByTermTaxonomyId(l)) {

                        if (termTax.getTermId() == tagIdBlog && type.equals("blog")) {
                            JSONObject obj = new JSONObject();
                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                        if (termTax.getTermId() == tagIdArtikel && type.equals("artikel") ) {
                            JSONObject obj = new JSONObject();
                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                        if (termTax.getTermId() == tagIdPresse && type.equals("news")) {
                            JSONObject obj = new JSONObject();
                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                    }
                }

        }
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        for (int i = 0; i < ergebnis.length(); i++) {
            jsonObjects.add(ergebnis.getJSONObject(i));
        }


        Collections.sort(jsonObjects, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    long clicks1 = o1.getLong("clicks of day");
                    long clicks2 = o2.getLong("clicks of day");
                    return Long.compare(clicks2, clicks1);  // für absteigende Sortierung
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // neues JSONArray, das nur die Top-5-Elemente enthält
        JSONArray top5JsonArray = new JSONArray();
        for (int i = 0; i < Math.min(5, jsonObjects.size()); i++) {
            top5JsonArray.put(jsonObjects.get(i));
        }

        return top5JsonArray.toString();
    }




}
