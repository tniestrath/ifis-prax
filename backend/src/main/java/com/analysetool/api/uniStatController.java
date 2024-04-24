package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.UniqueUserService;
import com.analysetool.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/bericht")
public class uniStatController {

    @Autowired
    UniversalCategoriesDLCRepository universalCategoriesDLCRepo;
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
    @Autowired
    private UniversalStatsHourlyRepository universalStatsHourlyRepo;
    @Autowired
    TrackingBlacklistRepository trackBlackRepo;
    @Autowired
    UniqueUserService uniqueService;

    /**
     *
     * @return gibt aus, wie viele Admins es aktuell gibt.
     */
    public int getAdminCount() {
        int adminCount = 0;
        for(String cap : wpUserMetaRepository.getWpCapabilities()) {
            if(cap.contains("administrator")){ adminCount++;}
        }
        return adminCount;
    }

    /**
     *
     * @param days  if 1, get hourly stats (for the last 24 hours) - if higher get Daily.
     * @return a JSON-String containing a lot of universal-stat data.
     * @throws JSONException .
     * @throws ParseException .
     */
    @GetMapping(value = "/callups")
    public String getCallupsByTime(@RequestParam() int days) throws JSONException, ParseException {
        JSONArray response = new JSONArray();

        String dateString = LocalDate.now(ZoneId.systemDefault()).minusDays(days).format(DateTimeFormatter.ISO_DATE);

        if(days > 1) {
            List<UniversalStats> universalStatsList = uniRepo.getAllByDatumAfter(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
            for (UniversalStats uniStat : universalStatsList) {
                JSONObject callup = new JSONObject();
                callup.put("date", new SimpleDateFormat("yyyy-MM-dd").format(uniStat.getDatum()));
                callup.put("sensibleClicks", uniStat.getSensibleClicks());
                callup.put("clicks", uniStat.getTotalClicks());
                callup.put("visitors", uniStat.getBesucherAnzahl());

                response.put(callup);
            }
        } else {
            List<UniversalStatsHourly> universalStatsList = universalStatsHourlyRepo.getLast24();
            for (UniversalStatsHourly uniStat : universalStatsList) {
                JSONObject callup = new JSONObject();
                callup.put("date", uniStat.getStunde());
                callup.put("sensibleClicks", uniStat.getSensibleClicks());
                callup.put("clicks", uniStat.getTotalClicks());
                callup.put("visitors", uniStat.getBesucherAnzahl());

                response.put(callup);
            }
        }

        return response.toString();
    }

    /**
     *
     * @return ein HTML Code, der eine Tabelle mit Statistiken enthält.
     * @throws JSONException .
     */
    @GetMapping(value = "/today", produces = MediaType.TEXT_HTML_VALUE)
    public String getLetzte() throws JSONException {
        JSONObject obj = new JSONObject();
        UniversalStats uniStat=uniRepo.findAll().get(uniRepo.findAll().size()-1);

        //Datum des Berichts
        obj.put("Datum",uniStat.getDatum());

        //Anzahl der Besucher Allgemein
        obj.put("Besucher",uniStat.getBesucherAnzahl());

        //Anzahl der Profile pro Profiltyp
        obj.put("Angemeldete Profile",uniStat.getAnbieterProfileAnzahl() - getAdminCount());
        obj.put("Angemeldete Nutzer ohne Abo", uniStat.getAnbieter_abolos_anzahl());
        obj.put("Angemeldete Basic Profile",uniStat.getAnbieterBasicAnzahl());
        obj.put("Angemeldete Basic-Plus Profile",uniStat.getAnbieterBasicPlusAnzahl());
        obj.put("Angemeldete Plus Profile",uniStat.getAnbieterPlusAnzahl());
        obj.put("Angemeldete Premium Profile",uniStat.getAnbieterPremiumAnzahl());
        obj.put("Angemeldete Premium Sponsoren Profile",uniStat.getAnbieterPremiumSponsorenAnzahl());

        //Anzahl der veröffentlichten Posts nach Kategorie
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


    /**
     *
     * @param date : das Datum, für das die UniStatsNach Kategorie ausgegeben werden sollen.
     * @param hour : die Stunde, für die ausgegeben werden sollen.
     * @return eine Liste der Anzahl von Clicks und Besucher nach Category des letzten abgeschlossenen Tages.
     *
     */
    @GetMapping("getCallupByCategoryDateAndHour")
    public String getCallupByCategoryHourly(String date, int hour) throws ParseException, JSONException {

        List<String> labelsForCategory = new ArrayList<>();
        labelsForCategory.add("Main");
        labelsForCategory.add("Article");
        labelsForCategory.add("News");
        labelsForCategory.add("Blog");
        labelsForCategory.add("Podcast");
        labelsForCategory.add("Whitepaper");
        labelsForCategory.add("Ratgeber");
        labelsForCategory.add("Ratgeber-Post");
        labelsForCategory.add("Ratgeber-Buch");
        labelsForCategory.add("Ratgeber-Glossar");
        labelsForCategory.add("Ratgeber-Selbstlernangebot");
        labelsForCategory.add("Ueber");
        labelsForCategory.add("Impressum");
        labelsForCategory.add("Preisliste");
        labelsForCategory.add("Partner");
        labelsForCategory.add("Datenschutz");
        labelsForCategory.add("Newsletter");
        labelsForCategory.add("Image");
        labelsForCategory.add("AGBs");

        List<Integer> clicksByCategory = new ArrayList<>();

        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = uniRepo.findByDatum(new SimpleDateFormat("yyyy-MM-dd").parse(date)).get().getId();

        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsMain());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsArticle());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsNews());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsBlog());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsPodcast());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsWhitepaper());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsRatgeber());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsRatgeberPost());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsRatgeberBuch());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsRatgeberGlossar());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsRatgeberSelf());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsUeber());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsImpressum());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsPreisliste());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsPartner());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsDatenschutz());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsNewsletter());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsImage());
        clicksByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getViewsAGBS());

        List<Integer> besucherByCategory = new ArrayList<>();

        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherMain());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherArticle());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherNews());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherBlog());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherPodcast());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherWhitepaper());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherRatgeber());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherRatgeberPost());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherRatgeberBuch());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherRatgeberGlossar());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherRatgeberSelf());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherUeber());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherImpressum());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherPreisliste());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherPartner());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherDatenschutz());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherNewsletter());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherImage());
        besucherByCategory.add(universalCategoriesDLCRepo.getByUniStatIdAndStunde(id, hour).getBesucherAGBS());

        JSONObject obj = new JSONObject().put("labels", new JSONArray(labelsForCategory));
        obj.put("besucher", new JSONArray(besucherByCategory));
        obj.put("clicks", new JSONArray(clicksByCategory));
        return obj.toString();
    }

    /**
     *
     * @param date : Das Datum, für die zusammengefasste Stats ausgegeben werden sollen.
     * @return eine Liste der Anzahl von Clicks und Besucher nach Category des letzten abgeschlossenen Tages.
     *
     */
    @GetMapping("getCallupByCategoryDate")
    public String getCallupByCategoryDaily(String date) throws ParseException, JSONException {
        List<String> labelsForCategory = new ArrayList<>();

        labelsForCategory.add("Main");
        labelsForCategory.add("Anbieterverzeichnis");
        labelsForCategory.add("Artikel");
        labelsForCategory.add("News");
        labelsForCategory.add("Blog");
        labelsForCategory.add("Podcast");
        labelsForCategory.add("Videos");
        labelsForCategory.add("Whitepaper");
        labelsForCategory.add("Events");
        labelsForCategory.add("Ratgeber");
        labelsForCategory.add("Ratgeber-Post");
        labelsForCategory.add("Ratgeber-Buch");
        labelsForCategory.add("Ratgeber-Glossar");
        labelsForCategory.add("Ratgeber-Self");
        labelsForCategory.add("Ueber");
        labelsForCategory.add("Impressum");
        labelsForCategory.add("Preisliste");
        labelsForCategory.add("Partner");
        labelsForCategory.add("Datenschutz");
        labelsForCategory.add("Newsletter");
        labelsForCategory.add("Image");
        labelsForCategory.add("AGBs");


        List<Integer> clicksByCategory = new ArrayList<>();

        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = uniRepo.findByDatum(new SimpleDateFormat("yyyy-MM-dd").parse(date)).get().getId();
        //Main Page
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsMainByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsAnbieterByUniStatId(id));
        //Posts
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsArticleByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsNewsByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsBlogByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsPodcastByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsVideosByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsWhitepaperByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsEventsByUniStatId(id));
        //Ratgeber
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberPostByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberBuchByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberGlossarByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberSelfByUniStatId(id));
        //Footer
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsUeberByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsImpressumByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsPreislisteByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsPartnerByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsDatenschutzByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsNewsletterByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsImageByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsAGBSByUniStatId(id));

        List<Integer> besucherByCategory = new ArrayList<>();

        //Main Page
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserMainByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserAnbieterByUniStatId(id));
        //Posts
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserArticleByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserNewsByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserBlogByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserPodcastByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserVideosByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserWhitepaperByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserEventsByUniStatId(id));
        //Ratgeber
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberPostByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberBuchByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberGlossarByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberSelfByUniStatId(id));
        //Footer
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserUeberByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserImpressumByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserPreislisteByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserPartnerByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserDatenschutzByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserNewsletterByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserImageByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserAGBSByUniStatId(id));


        JSONObject obj = new JSONObject().put("labels", new JSONArray(labelsForCategory));
        obj.put("besucher", new JSONArray(besucherByCategory));
        obj.put("clicks", new JSONArray(clicksByCategory));
        return obj.toString();
    }


    @GetMapping("getCallupByCategoryAllTime")
    public String getCallupByCategoryAllTime() throws JSONException {
        List<String> labelsForCategory = new ArrayList<>();

        labelsForCategory.add("Main");
        labelsForCategory.add("Anbieterverzeichnis");
        labelsForCategory.add("Artikel");
        labelsForCategory.add("News");
        labelsForCategory.add("Blog");
        labelsForCategory.add("Podcast");
        labelsForCategory.add("Videos");
        labelsForCategory.add("Whitepaper");
        labelsForCategory.add("Events");
        labelsForCategory.add("Ratgeber");
        labelsForCategory.add("Ratgeber-Post");
        labelsForCategory.add("Ratgeber-Buch");
        labelsForCategory.add("Ratgeber-Glossar");
        labelsForCategory.add("Ratgeber-Self");
        labelsForCategory.add("Ueber");
        labelsForCategory.add("Impressum");
        labelsForCategory.add("Preisliste");
        labelsForCategory.add("Partner");
        labelsForCategory.add("Datenschutz");
        labelsForCategory.add("Newsletter");
        labelsForCategory.add("Image");
        labelsForCategory.add("AGBs");


        List<Integer> clicksByCategory = new ArrayList<>();

        //Main Page
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsMainAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsAnbieterAllTime());
        //Posts
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsArticleAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsNewsAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsBlogAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsPodcastAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsVideosAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsWhitepaperAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsEventsAllTime());
        //Ratgeber
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberPostAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberBuchAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberGlossarAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberSelfAllTime());
        //Footer
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsUeberAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsImpressumAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsPreislisteAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsPartnerAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsDatenschutzAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsNewsletterAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsImageAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsAGBSAllTime());

        List<Integer> besucherByCategory = new ArrayList<>();

        //Main Page
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserMainAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserAnbieterAllTime());
        //Posts
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserArticleAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserNewsAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserBlogAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserPodcastAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserVideosAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserWhitepaperAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserEventsAllTime());
        //Ratgeber
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberPostAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberBuchAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberGlossarAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberSelfAllTime());
        //Footer
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserUeberAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserImpressumAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserPreislisteAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserPartnerAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserDatenschutzAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserNewsletterAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserImageAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserAGBSAllTime());


        JSONObject obj = new JSONObject().put("labels", new JSONArray(labelsForCategory));
        obj.put("besucher", new JSONArray(besucherByCategory));
        obj.put("clicks", new JSONArray(clicksByCategory));
        return obj.toString();
    }



    @GetMapping("/getRatgeberDetailedDaily")
    public String getRatgeberDetailedByDate(String date) throws ParseException, JSONException {
        List<String> labelsForCategory = new ArrayList<>();

        labelsForCategory.add("posts");
        labelsForCategory.add("glossar");
        labelsForCategory.add("buch");

        List<Integer> clicksByCategory = new ArrayList<>();
        List<Integer> besucherByCategory = new ArrayList<>();

        @SuppressWarnings("OptionalGetWithoutIsPresent") int id = uniRepo.findByDatum(new SimpleDateFormat("yyyy-MM-dd").parse(date)).get().getId();

        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberPostByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberGlossarByUniStatId(id));
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberBuchByUniStatId(id));

        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberPostByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberGlossarByUniStatId(id));
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberBuchByUniStatId(id));

        JSONObject obj = new JSONObject().put("labels", new JSONArray(labelsForCategory));
        obj.put("besucher", new JSONArray(besucherByCategory));
        obj.put("clicks", new JSONArray(clicksByCategory));
        return obj.toString();
    }


    @GetMapping("/getRatgeberDetailedAllTime")
    public String getRatgeberDetailedAllTime() throws JSONException {
        List<String> labelsForCategory = new ArrayList<>();

        labelsForCategory.add("posts");
        labelsForCategory.add("glossar");
        labelsForCategory.add("buch");

        List<Integer> clicksByCategory = new ArrayList<>();
        List<Integer> besucherByCategory = new ArrayList<>();

        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberPostAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberGlossarAllTime());
        clicksByCategory.add(universalCategoriesDLCRepo.getSumViewsRatgeberBuchAllTime());

        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberPostAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberGlossarAllTime());
        besucherByCategory.add(universalCategoriesDLCRepo.getSumUserRatgeberBuchAllTime());

        JSONObject obj = new JSONObject().put("labels", new JSONArray(labelsForCategory));
        obj.put("besucher", new JSONArray(besucherByCategory));
        obj.put("clicks", new JSONArray(clicksByCategory));
        return obj.toString();
    }

    /**
     *
     * @return eine HTML-Seite, die den Bericht wie in der Methode getLetzte, nur für die letzten 7 Tage erstellt.
     * @throws JSONException .
     */
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
            //Erstelle pro Durchlauf einen Bericht
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

    /**
     *
     * @param id  die ID des Posts, für den die Clicks ermittelt werden sollen.
     * @param daysBack wie viele Tage zurückgeschaut werden soll.
     * @return  JSONObject, dass die ID des Posts, den Namen und die Clicks des gewünschten Tages enthält.
     * @throws JSONException .
     */
    public JSONObject getClickOfDayAsJson(long id,int daysBack) throws JSONException {

        JSONObject obj = new JSONObject();
        PostStats stats =postStatsRepo.getStatByArtID(id);
        long clicks;
        HashMap<String,Long> allClicks = (HashMap<String, Long>) stats.getViewsLastYear();
        LocalDate heute = LocalDate.now();
        // Datum, das n Tage zurückliegt
        LocalDate vergangenesDatum = heute.minusDays(daysBack);
        // Datum in dd.MM-Format umwandeln
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        String formatiertesDatum = vergangenesDatum.format(formatter);

        clicks=allClicks.get(formatiertesDatum);

        obj.put("iD",id);
        //noinspection OptionalGetWithoutIsPresent
        obj.put("name",postRepository.findById(id).get().getTitle());
        obj.put("clicks of day",clicks);
        return obj;
    }

    /**
     *
     * @param type der Typ Post, für den eine Top5 erstellt werden soll ("blog" | "artikel" | "news" | "whitepaper" | "podcast" | "videos")
     * @param daysBack how many days to look back.
     * @return a JSON String.
     * @throws JSONException .
     */
    @GetMapping("/getTop5ByClicksAndDaysBackAndType")
    public String getTop5ByClicks(@RequestParam String type, @RequestParam int daysBack) throws JSONException {
        JSONArray ergebnis = new JSONArray();

        List<Post> posts = postRepository.findAllUserPosts();

        int tagIdBlog = termRepo.findBySlug(Constants.getInstance().getBlogSlug()).getId().intValue();
        int tagIdArtikel = termRepo.findBySlug(Constants.getInstance().getArtikelSlug()).getId().intValue();
        int tagIdNews = termRepo.findBySlug(Constants.getInstance().getNewsSlug()).getId().intValue();
        int tagIdWhitepaper = termRepo.findBySlug(Constants.getInstance().getWhitepaperSlug()).getId().intValue();
        int tagIdPodcast = termRepo.findBySlug(Constants.getInstance().getPodastSlug()).getId().intValue();
        int tagIdVideos = termRepo.findBySlug(Constants.getInstance().getVideoSlug()).getId().intValue();

        for (Post post : posts) {


                for (Long l : termRelRepo.getTaxIdByObject(post.getId())) {

                    for (WpTermTaxonomy termTax : termTaxRepo.findByTermTaxonomyId(l)) {

                        if (termTax.getTermId() == tagIdBlog && type.equals("blog")) {
                            JSONObject obj;

                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                        if (termTax.getTermId() == tagIdArtikel && type.equals("artikel") ) {
                            JSONObject obj;
                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                        if (termTax.getTermId() == tagIdNews && type.equals("news")) {
                            JSONObject obj;
                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                        if (termTax.getTermId() == tagIdWhitepaper && type.equals("whitepaper")) {
                            JSONObject obj;
                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                        if (termTax.getTermId() == tagIdPodcast && type.equals("podcast")) {
                            JSONObject obj;
                            obj = getClickOfDayAsJson(post.getId(),daysBack);
                            ergebnis.put(obj);
                        }

                        if (termTax.getTermId() == tagIdVideos && type.equals("videos")) {
                            JSONObject obj;
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


        jsonObjects.sort((o1, o2) -> {
            try {
                long clicks1 = o1.getLong("clicks of day");
                long clicks2 = o2.getLong("clicks of day");
                return Long.compare(clicks2, clicks1);  // für absteigende Sortierung
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // neues JSONArray, das nur die Top-5-Elemente enthält
        JSONArray top5JsonArray = new JSONArray();
        for (int i = 0; i < Math.min(5, jsonObjects.size()); i++) {
            top5JsonArray.put(jsonObjects.get(i));
        }

        return top5JsonArray.toString();
    }

    @GetMapping("/getPostsByType")
    public String getPostsByType() {
        HashMap<String, Integer> map = new HashMap<>();
        UniversalStats uniStat = uniRepo.getLatestUniStat();
        return makeTypeMap(map, uniStat);
    }

    @GetMapping("/getPostsByTypeYesterday")
    public String getPostsByTypeYesterday(){
        HashMap<String, Integer> map = new HashMap<>();
        UniversalStats uniStat = uniRepo.getSecondLastUniStats().get(1);
        return makeTypeMap(map, uniStat);
    }

    private String makeTypeMap(HashMap<String, Integer> map, UniversalStats uniStat) {
        map.put("Blogs", Math.toIntExact(uniStat.getAnzahlBlog()));
        map.put("News", Math.toIntExact(uniStat.getAnzahlNews()));
        map.put("Artikel", Math.toIntExact(uniStat.getAnzahlArtikel()));
        map.put("Whitepaper", Math.toIntExact(uniStat.getAnzahlWhitepaper()));
        map.put("Podcasts", Math.toIntExact(uniStat.getAnzahlPodcast()));

        return new JSONObject(map).toString();
    }

/**
 * Provides conversion rate calculation APIs for different subscription types without including blocked IPs in the count.
 * Each method calculates the conversion rate based on the difference in subscription counts (of various types) between today and yesterday, divided by the count of unique IPs accessed today after excluding blocked IPs.
 */

    /**
     * Calculates and returns the conversion rate for non-subscribers.
     *
     * @return the conversion rate for non-subscribers as a double. It's calculated by subtracting the number of non-subscribers yesterday from today, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRateNoSub")
    public double getConversionRateNoSub(){
        List<UniversalStats> lastTwoDays = uniRepo.getSecondLastUniStats();
        long noSubDiffTodayYesterday = lastTwoDays.get(0).getAnbieter_abolos_anzahl() - lastTwoDays.get(1).getAnbieter_abolos_anzahl();

        List<String> uniqueIps= uniqueService.getIpsToday();
        List<String> blockedIps= trackBlackRepo.getAllIps();
        uniqueIps.removeAll(blockedIps);

        return (double)noSubDiffTodayYesterday/uniqueIps.size();
    }

    /**
     * Calculates and returns the conversion rate for basic subscribers.
     *
     * @return the conversion rate for basic subscribers as a double. It's determined by the difference in basic subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRateBasicSub")
    public double getConversionRateBasicSub(){
        List<UniversalStats> lastTwoDays = uniRepo.getSecondLastUniStats();
        long noSubDiffTodayYesterday = lastTwoDays.get(0).getAnbieterBasicAnzahl() - lastTwoDays.get(1).getAnbieterBasicAnzahl();

        List<String> uniqueIps= uniqueService.getIpsToday();
        List<String> blockedIps= trackBlackRepo.getAllIps();
        uniqueIps.removeAll(blockedIps);

        return (double)noSubDiffTodayYesterday/uniqueIps.size();
    }

    /**
     * Calculates and returns the conversion rate for basic plus subscribers.
     *
     * @return the conversion rate for basic plus subscribers as a double. It's computed by the difference in basic plus subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRateBasicPlusSub")
    public double getConversionRateBasicPlusSub(){
        List<UniversalStats> lastTwoDays = uniRepo.getSecondLastUniStats();
        long noSubDiffTodayYesterday = lastTwoDays.get(0).getAnbieterBasicPlusAnzahl() - lastTwoDays.get(1).getAnbieterBasicPlusAnzahl();

        List<String> uniqueIps= uniqueService.getIpsToday();
        List<String> blockedIps= trackBlackRepo.getAllIps();
        uniqueIps.removeAll(blockedIps);

        return (double)noSubDiffTodayYesterday/uniqueIps.size();
    }


    /**
     * Calculates and returns the conversion rate for plus subscribers.
     *
     * @return the conversion rate for plus subscribers as a double. This rate is calculated by subtracting yesterday's plus subscriber count from today's, divided by the count of today's unique, non-blocked IPs.
     */
    @GetMapping("/getConversionRatePlusSub")
    public double getConversionRatePlusSub(){
        List<UniversalStats> lastTwoDays = uniRepo.getSecondLastUniStats();
        long noSubDiffTodayYesterday = lastTwoDays.get(0).getAnbieterPlusAnzahl() - lastTwoDays.get(1).getAnbieterPlusAnzahl();

        List<String> uniqueIps= uniqueService.getIpsToday();
        List<String> blockedIps= trackBlackRepo.getAllIps();
        uniqueIps.removeAll(blockedIps);

        return (double)noSubDiffTodayYesterday/uniqueIps.size();
    }

    /**
     * Calculates and returns the conversion rate for premium subscribers.
     *
     * @return the conversion rate for premium subscribers as a double. It's the difference in premium subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRatePremiumSub")
    public double getConversionRatePremiumSub(){
        List<UniversalStats> lastTwoDays = uniRepo.getSecondLastUniStats();
        long noSubDiffTodayYesterday = lastTwoDays.get(0).getAnbieterPremiumAnzahl() - lastTwoDays.get(1).getAnbieterPremiumAnzahl();

        List<String> uniqueIps= uniqueService.getIpsToday();
        List<String> blockedIps= trackBlackRepo.getAllIps();
        uniqueIps.removeAll(blockedIps);

        return (double)noSubDiffTodayYesterday/uniqueIps.size();
    }

    /**
     * Calculates and returns the conversion rate for premium sponsor subscribers.
     *
     * @return the conversion rate for premium sponsor subscribers as a double. This is based on the difference in premium sponsor subscriber counts between today and yesterday, divided by today's unique, non-blocked IP count.
     */
    @GetMapping("/getConversionRatePremiumSponsorSub")
    public double getConversionRatePremiumSponsorSub(){
        List<UniversalStats> lastTwoDays = uniRepo.getSecondLastUniStats();
        long noSubDiffTodayYesterday = lastTwoDays.get(0).getAnbieterPremiumSponsorenAnzahl() - lastTwoDays.get(1).getAnbieterPremiumSponsorenAnzahl();

        List<String> uniqueIps= uniqueService.getIpsToday();
        List<String> blockedIps= trackBlackRepo.getAllIps();
        uniqueIps.removeAll(blockedIps);

        return (double)noSubDiffTodayYesterday/uniqueIps.size();
    }

    /**
     * Calculates and returns the overall conversion rate across all subscription types.
     *
     * @return the overall conversion rate as a double.
     */
    @GetMapping("/getTotalConversionRateMembership")
    public double getTotalConversionRateMembership() {
        return (getConversionRateNoSub() + getConversionRateBasicSub() + getConversionRateBasicPlusSub() +
                getConversionRatePlusSub() + getConversionRatePremiumSub() + getConversionRatePremiumSponsorSub()) / 6;
    }
    /**
     * Returns a JSON string representing the ranking of subscription types by their conversion rates.
     * Each subscription type is represented as a JSON object with its name, rate, and rank.
     * These objects are added to a JSON array, which is then converted to a string.
     *
     * @return A JSON string that represents the conversion rate ranking of subscription types.
     */
    @GetMapping("/getSubscriptionRateRanking")
    public String getSubscriptionRateRanking() throws JSONException {
        Map<String, Double> rates = new LinkedHashMap<>();
        rates.put("Non-Subscriber", getConversionRateNoSub());
        rates.put("Basic Subscriber", getConversionRateBasicSub());
        rates.put("Basic Plus Subscriber", getConversionRateBasicPlusSub());
        rates.put("Plus Subscriber", getConversionRatePlusSub());
        rates.put("Premium Subscriber", getConversionRatePremiumSub());
        rates.put("Premium Sponsor Subscriber", getConversionRatePremiumSponsorSub());

        // Sortieren der Map nach Konversionsraten
        Map<String, Double> sortedRates = rates.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new));

        // Erstellung des JSON-Arrays
        JSONArray jsonArray = new JSONArray();
        int rank = 1;
        for (Map.Entry<String, Double> entry : sortedRates.entrySet()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("Membership", entry.getKey());
            jsonObj.put("Rate", entry.getValue());
            jsonObj.put("Rank", rank++);
            jsonArray.put(jsonObj);
        }

        return jsonArray.toString();
    }
    ///////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //////////////////////////////AB HIER UNIVERSAL STATS HOURLY \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\




}
