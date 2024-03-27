package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.services.FinalSearchStatService;
import com.analysetool.services.PostService;
import com.analysetool.util.MathHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Tuple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/search-stats")
public class SearchStatsController {

    @Autowired
    private SearchStatsRepository searchStatsRepository;
    @Autowired
    AnbieterSearchRepository anbieterSearchRepo;
    @Autowired
    private EventSearchRepository eventSearchRepo;
    @Autowired
    private FinalSearchStatService fSearchStatService;
    @Autowired
    private PostService postService;
    @Autowired
    private universalStatsRepository uniRepo;
    @Autowired
    private FinalSearchStatRepository finalSearchStatRepo;
    @Autowired
    private FinalSearchStatDLCRepository finalDLCRepo;
    @Autowired
    private BlockedSearchesRepository blockedRepo;
    @Autowired
    private GeoNamesPostalRepository geoNamesRepo;
    @Autowired
    private AnbieterFailedSearchBufferRepository anbieterFailRepo;
    @Autowired
    private BlockedSearchesAnbieterRepository baSearchRepo;

    @Autowired
    public SearchStatsController(SearchStatsRepository searchStatsRepository) {
        this.searchStatsRepository = searchStatsRepository;}


    @GetMapping("/getAll")
    public List<SearchStats> getAllSearchStats() {
        return searchStatsRepository.findAll();
    }

    @GetMapping("/getSearchStats")
    public String getSearchStats(@RequestParam int limit) throws JSONException {
        JSONArray response = new JSONArray();
        List<SearchStats> alleStats = searchStatsRepository.findAll();
        for (int i = alleStats.size() - 1; i != alleStats.size() - limit; i--) {

            JSONObject obj = new JSONObject();
            obj.put("search_string", alleStats.get(i).getSearchString());
            obj.put("search_success", alleStats.get(i).getSearchSuccessFlag());
            if (alleStats.get(i).getClickedPost() != null) {

                obj.put("clicked_post", alleStats.get(i).getClickedPost());

            }
            obj.put("location", alleStats.get(i).getLocation());
            response.put(obj);
        }

        return response.toString();
    }

    @GetMapping("/getSearchStatsByPostWithLimit")
    public String getSearchStatsByPostWithLimit(@RequestParam Long PostId,@RequestParam int limit) throws JSONException {
        JSONArray response = new JSONArray();
        List<SearchStats> alleStats = searchStatsRepository.findByClickedPost(PostId.toString());
        for (int i = alleStats.size() - 1; i != alleStats.size() - limit; i--) {

            JSONObject obj = new JSONObject();
            obj.put("search_string", alleStats.get(i).getSearchString());
            obj.put("search_succes", alleStats.get(i).getSearchSuccessFlag());
            if (alleStats.get(i).getClickedPost() != null) {

                obj.put("clicked_post", alleStats.get(i).getClickedPost());

            }
            if (alleStats.get(i).getSearchTime() != null) {

                obj.put("search_time", alleStats.get(i).getSearchTime());

            }
            if (alleStats.get(i).getDwell_time() != null) {

                obj.put("dwell_time", alleStats.get(i).getDwell_time());

            }
            obj.put("location", alleStats.get(i).getLocation());
            obj.put("search_success_time",alleStats.get(i).getSearch_success_time());


            response.put(obj);
        }
        return response.toString();
    }


    /**
     * Endpoint, schlechte Ausreißer basierend auf den gefundenen Anbietern innerhalb eines Radius aller Anbietersuchen zu ermitteln.
     *
     * @return Ein JSON-String, der schlechte Ausreißer repräsentiert (nur wenige oder keine Anbieter).
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */

    @GetMapping("/getBadOutlierAllProviderSearches")
    public String getBadOutlierAllProviderSearches() throws JSONException {
        JSONArray Ergebnis = new JSONArray();

        CopyOnWriteArrayList<AnbieterSearch> anbieterSearches = new CopyOnWriteArrayList<>(anbieterSearchRepo.findAll());

        CopyOnWriteArrayList<Integer> counts=new CopyOnWriteArrayList<>();

        for(AnbieterSearch a:anbieterSearches){
            counts.add(a.getCount_found());}
        double mittelwert = MathHelper.getMeanInt(counts);
        //alle Ausreißer
        List<Integer> Outlier =  MathHelper.getOutliersInt(counts);

        for(Integer i:Outlier) {

            //schlechte Ausreißer ermitteln
            if (i < mittelwert) {
                JSONObject obj = new JSONObject();
                for(AnbieterSearch a:anbieterSearches) {
                    if (a.getCount_found() == i) {
                        obj.put("Ort", a.getCity_name());
                        obj.put("Umkreis", a.getUmkreis());
                        obj.put("Count",a.getCount_found());
                    }
                    anbieterSearches.remove(a);
                }
                Ergebnis.put(obj);
            }
        }
        return Ergebnis.toString();
    }

    /**
     * Endpoint, schlechte Ausreißer basierend auf den gefundenen Anbietern innerhalb eines Radius einer gewissen Anzahl an Anbietersuchen zu ermitteln.
     * @param limit = 0 benutzt alle Suchen, sonst normales limit
     * @return Ein JSON-String, der schlechte Ausreißer repräsentiert (nur wenige oder keine Anbieter).
     */
    @GetMapping("/getBadOutlierForXProviderSearches")
    public String getBadOutlierForXProviderSearches(@RequestParam int limit) {
        try {
            List<AnbieterSearch> anbieterSearches = new ArrayList<>();
            if (limit > 0) {
                Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
                anbieterSearches = anbieterSearchRepo.findAllByOrderByIdDesc(pageable).getContent();
            } else if (limit == 0) {
                anbieterSearches = anbieterSearchRepo.findAll();
            }

            List<Integer> counts = anbieterSearches.stream()
                    .map(AnbieterSearch::getCount_found)
                    .collect(Collectors.toList());

            List<Integer> lowerBoundOutliers = MathHelper.getLowerBoundOutliersInt(counts);

            List<AnbieterSearch> filteredAnbieterSearches = anbieterSearches.stream()
                    .filter(anbieterSearch -> lowerBoundOutliers.contains(anbieterSearch.getCount_found()))
                    .collect(Collectors.toList());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(filteredAnbieterSearches);
        } catch (Exception e) {
            return "Fehler beim Verarbeiten der Daten: " + e.getMessage();
        }
    }


    /**
     * Findet und liefert eine Liste von EventSearch-Objekten als JSON-String,
     * die als schlechte Ausreißer aufgrund ihres resultCount-Wertes identifiziert wurden,
     * begrenzt auf eine bestimmte Anzahl der zuletzt hinzugefügten Eventsearches.
     * Diese Methode holt die letzten 'limit' EventSearch-Objekte, sortiert nach ihrer ID in absteigender Reihenfolge,
     * berechnet die Ausreißer für ihre resultCount-Werte und filtert die entsprechenden Events heraus.
     *
     * @param limit Die maximale Anzahl von EventSearch-Objekten, die zurückgegeben werden sollen.0=alle
     * @return Ein String, der ein JSON-Array von EventSearch-Objekten repräsentiert.
     *         Jedes Objekt im Array ist ein schlechter Ausreißer basierend auf dem resultCount-Wert.
     *         Bei einem Fehler in der Verarbeitung wird eine Fehlermeldung zurückgegeben.
     */
    @GetMapping("/badOutliersEventSearch")
    public String findBadOutliersEventSearch(@RequestParam int limit) {
        try {
            List<EventSearch> latestEventSearches = new ArrayList<>();
            if (limit > 0) {
                Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
                latestEventSearches = eventSearchRepo.findAllByOrderByIdDesc(pageable).getContent();
            } else if (limit == 0) {
                latestEventSearches = eventSearchRepo.findAll();
            }
            List<Integer> resultCounts = latestEventSearches.stream()
                    .map(EventSearch::getResultCount)
                    .collect(Collectors.toList());
            List<Integer> lowerBoundOutliers = MathHelper.getLowerBoundOutliersInt(resultCounts);
            List<EventSearch> filteredEventSearches = latestEventSearches.stream()
                    .filter(eventSearch -> lowerBoundOutliers.contains(eventSearch.getResultCount()))
                    .collect(Collectors.toList());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(filteredEventSearches);
        } catch (Exception e) {
            return "Fehler beim Verarbeiten der Daten";
        }
    }

    @GetMapping("/getZeroCountEventSearches")
    public String getZeroCountEventSearches(){
        return eventSearchRepo.getEventSearchesWithCountZero().toString();
    }

    @GetMapping("/getSearchStatsByPostId")
    public String getSearchStatsByPostId(@RequestParam Long postId){
       return fSearchStatService.getSearchStatsByPostId(postId).toString();
    }

    @GetMapping("/getSearchStatsByUserId")
    public String getSearchStatsByUserId(@RequestParam Long userId){
        return fSearchStatService.getSearchStatsByUserId(userId).toString();
    }

    /**
     * Retrieves and returns search statistics for posts similar to a given post, based on tag similarity.
     * Similarity is determined by a specified minimum similarity percentage. The result includes each similar
     * post's search statistics, similarity score, and post ID.
     *
     * @param postId The ID of the reference post for which similar posts are sought.
     * @param similarityPercentage The minimum threshold of tag similarity (in percentage)
     *        to consider a post similar to the given post. !!60% = 60 ; 0,6% = 0,6 ...!!<--------------------------
     * @return A JSON string representing an array of objects. Each object contains the post ID,
     *         its similarity score to the given post, and its search statistics.
     * @throws JSONException If an issue occurs during JSON processing.
     */
    @GetMapping("/getSearchStatsForSimilarPostsByTags")
    public String getSearchStatsForSimilarPostsByTags(@RequestParam Long postId,@RequestParam float similarityPercentage) throws JSONException {
        JSONArray ergebnis = new JSONArray();
        Map<Long,Float> similarityMap = postService.getSimilarPosts(postId,similarityPercentage);
        List< FinalSearchStat> searchStats;

        for(Long postIds : similarityMap.keySet()){
            JSONObject obj = new JSONObject();
           searchStats = fSearchStatService.getSearchStatsByPostId(postIds);
           if((!(searchStats==null))&& (!searchStats.isEmpty())){

               obj.put("searchStats",fSearchStatService.toStringList(searchStats));
               obj.put("similarity",similarityMap.get(postIds));
               obj.put("postId",postIds);

               ergebnis.put(obj);
           }
        }
        return ergebnis.toString();
    }

    /**
     * Retrieves demand data based on location and analysis type.
     *
     * @param location         The location to analyze.
     * @param locationType     The type of location (e.g., city, country, state).
     * @param searchThreshold  The threshold for the number of occurrences for a search query.
     * @param resultThreshold  The threshold for the result count.
     * @param analysisType     The type of analysis to perform ("searchSuccess" or "resultCount").
     * @return A map containing frequent searches with few results based on the given thresholds.
     */
    @GetMapping("/getDemandByLocation")
    public String getDemandByLocation(@RequestParam String location, @RequestParam String locationType, @RequestParam int searchThreshold, @RequestParam int resultThreshold, @RequestParam String analysisType) {
        Map<FinalSearchStat, List<FinalSearchStatDLC>> dataPool = fSearchStatService.getSearchStatsByLocation(location, locationType);
        Map<String, Integer> responseMap = switch (analysisType) {
            case "searchSuccess" ->
                    fSearchStatService.findFrequentSearchesWithFewSearchSuccesses(dataPool, searchThreshold, resultThreshold);
            case "resultCount" ->
                    fSearchStatService.findFrequentSearchesWithFewResults(dataPool, searchThreshold, resultThreshold);
            default -> new HashMap<>();
        };

        try {
            // Konvertieren der Map in eine Liste von Objekten für eine einfachere JSON-Struktur
            List<Map<String, Object>> responseList = responseMap.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("searchQuery", entry.getKey());
                        item.put("count", entry.getValue());
                        return item;
                    })
                    .collect(Collectors.toList());

            // Konvertieren der Liste in einen JSON-String
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(responseList);
        } catch (Exception e) {
            // Rückgabe eines Fehler-JSON-Strings im Fehlerfall
            return "{\"error\":\"Error processing request\"}";
        }
    }

    /**
     *
     * @param page the page of the given size to load. (Page 2 of size 5 loads 5-10).
     * @param size the number of results to load.
     * @param sorter the entry you want to sort by (count | found | ss)
     * @param dir the direction you want to sort. (ASC for Ascending, anything else for DESCENDING)
     * @return a collection of Search-Stats-Data-Entries.
     */
    @GetMapping("/getCoolSearchList")
    public String getCoolSearchList(int page, int size, String sorter, String dir) {
        switch (sorter) {
            case "count" -> {
                return getTopSearchQueriesBySearchedCount(page, size, dir);
            }
            case "found" -> {
                return getSearchQueriesByFoundCount(page, size, dir);
            }
            case "ss" -> {
                return getTopSearchQueriesBySS(page, size, dir);
            }
            default -> {
                return "Du banause musst n sorter angeben sonst setzt es was";
            }
        }
    }

    private String getSearchQueriesByFoundCount(int page, int size, String dir) {
        JSONArray response = new JSONArray();
        List<Tuple> pairs;
        if(dir != null && dir.equals("ASC")) {
            pairs = finalSearchStatRepo.getQueryAndFoundCountAverageASC(PageRequest.of(page, size));
        } else {
            pairs = finalSearchStatRepo.getQueryAndFoundCountAverageDESC(PageRequest.of(page, size));
        }

        for(Tuple pair : pairs) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("query", (String) pair.get(0));
                obj.put("id", finalSearchStatRepo.getIdsBySearch((String) pair.get(0)).get(0));
                obj.put("searchedCount", finalSearchStatRepo.getCountSearchedByQuery((String) pair.get(0)));
                obj.put("sSCount", finalSearchStatRepo.getCountSearchSuccessForQuery((String) pair.get(0)));
                obj.put("foundCount", (int) pair.get(1));
                response.put(obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return response.toString();
    }


    private String getTopSearchQueriesBySS(int page, int size, String dir){
        JSONArray response = new JSONArray();
        List<Tuple> pairs;
        if(dir != null && dir.equals("ASC")) {
            pairs = finalSearchStatRepo.getQueriesAndCountsSSASC(PageRequest.of(page, size));
        } else {
            pairs = finalSearchStatRepo.getQueriesAndCountsSSDESC(PageRequest.of(page, size));
        }

        for(Tuple pair :  pairs) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("query", (String) pair.get(0));
                obj.put("id", finalSearchStatRepo.getIdsBySearch((String) pair.get(0)).get(0));
                obj.put("sSCount", (long) pair.get(1));
                obj.put("searchedCount", finalSearchStatRepo.getCountSearchedByQuery((String) pair.get(0)));
                obj.put("foundCount", finalSearchStatRepo.getSumFoundLastSearchOfQuery((String) pair.get(0)));
                response.put(obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return response.toString();
    }

    private String getTopSearchQueriesBySearchedCount(int page, int size, String dir){
        JSONArray response = new JSONArray();
        List<Tuple> pairs;
        if(dir != null && dir.equals("ASC")) {
            pairs = finalSearchStatRepo.getQueriesAndCountsASC(PageRequest.of(page, size));
        } else {
            pairs = finalSearchStatRepo.getQueriesAndCountsDESC(PageRequest.of(page, size));
        }

            for(Tuple pair : pairs) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("query", (String) pair.get(0));
                    obj.put("searchedCount", (long) pair.get(1));
                    obj.put("id", finalSearchStatRepo.getIdsBySearch((String) pair.get(0)).get(0));
                    obj.put("sSCount", finalSearchStatRepo.getCountSearchSuccessForQuery((String) pair.get(0)));
                    obj.put("foundCount", finalSearchStatRepo.getSumFoundLastSearchOfQuery((String) pair.get(0)));
                    response.put(obj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return response.toString();
    }

    /**
     * Gibt die Verteilung der Suchanfragen über einen bestimmten Zeitraum zurück.
     * Diese Methode berechnet die Anzahl der Suchanfragen pro Tag für die letzte Woche, den letzten Monat oder das letzte Jahr,
     * abhängig vom übergebenen {@code distributionType}. Die Ergebnisse sind für die Darstellung in einem Liniendiagramm
     * aufbereitet, wobei jedes Datenobjekt ein Datum und die entsprechende Anzahl der Suchanfragen für dieses Datum enthält.
     *
     * @param distributionType Der Zeitraum der Verteilung: "week" für die letzte Woche, "month" für den letzten Monat, "year" für das letzte Jahr.
     * @return Ein JSON-String, der eine Array von Objekten enthält, wobei jedes Objekt ein Datum (im Format DD-MM-YYYY) und die Anzahl der Suchanfragen an diesem Tag repräsentiert.
     * @throws JSONException Falls beim Erstellen der JSON-Objekte ein Fehler auftritt.
     */
    @GetMapping("/getSearchCountDistributedByTime")
    public String getSearchCountDistributedByTime(@RequestParam String distributionType) throws JSONException {
        int latestUniId = uniRepo.getLatestUniStat().getId();
        int lowerBoundUniId=0;
        Map<Integer,Long> allSearchCountsByUniId= fSearchStatService.getSearchCountDistributedByUniId();
        Calendar cal = Calendar.getInstance();

        JSONArray response = new JSONArray();

        switch (distributionType) {
            case "week" -> lowerBoundUniId = latestUniId - 7;
            case "month" -> lowerBoundUniId = latestUniId - 30;
            case "year" -> lowerBoundUniId = latestUniId - 365;
        }

        cal.add(Calendar.DAY_OF_YEAR, -(latestUniId-lowerBoundUniId ));

        for(int lowerBound = lowerBoundUniId; lowerBound<=latestUniId; lowerBound++){
            JSONObject obj = new JSONObject();

            Long count = allSearchCountsByUniId.getOrDefault(lowerBound,0L);
            String date = String.format("%1$td-%1$tm-%1$tY", cal.getTime());

            obj.put("date",date);
            obj.put("count",count);
            response.put(obj);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return response.toString();
    }

    /**
     * Finds all Search-Queries and the number of times they have been searched.
     * Only 'Unfixed', so only searches that have never found anything.
     * Nonsense or non-legit (potential attacks) are NOT listed.
     * @return a JSONArray-String, containing JSON-Objects with 'search' and 'count'
     */
    @GetMapping("/getAllUnfixedSearches")
    public String getAllUnfixedZeroCountSearches(int page, int size) throws JSONException {
        JSONArray array = new JSONArray();

        for(Tuple pair : finalSearchStatRepo.getAllUnfixedSearchesWithZeroFound("%&%;%", PageRequest.of(page, size))) {
            JSONObject json = new JSONObject();
            json.put("search", (String) pair.get(0));
            json.put("id", finalSearchStatRepo.getIdsBySearch((String) pair.get(0)).get(0));
            json.put("count", (long) pair.get(1));
            array.put(json);
        }

        return array.toString();
    }

    /**
     * Gets all potentially threatening queries.
     * @return a JSONArray-String, containing JSON-Objects with 'search' and 'count'
     */
    @GetMapping("/getAllThreats")
    public String getAllThreats() throws JSONException {
        Map<String, Integer> searchesAndCounts = new HashMap<>();
        JSONArray array = new JSONArray();
        for(FinalSearchStat f : finalSearchStatRepo.getAllSearchesOrderedByFoundAscending()) {
            if(isHack(f.getSearchQuery()) && blockedRepo.getBySearch(f.getSearchQuery()).isEmpty()) {
                searchesAndCounts.merge(f.getSearchQuery(), 1, Integer::sum);
            }
        }

        for(String key : searchesAndCounts.keySet()) {
            JSONObject json = new JSONObject();
            json.put("search", key);
            json.put("count", searchesAndCounts.get(key));
            array.put(json);
        }

        return array.toString();
    }

    @PostMapping("/unblockSearch")
    @Modifying
    public boolean unblockSearch(long search) {
        boolean unblocked = false;
        if(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).isPresent()) {
            blockedRepo.delete(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).get());
            unblocked = true;
        }

        return unblocked;
    }

    @PostMapping("/blockSearch")
    @Modifying
    public boolean blockSearch(long search) {
        boolean deleted = false;
        if(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).isEmpty()) {
            BlockedSearches b = new BlockedSearches();
            b.setSearch(finalSearchStatRepo.findById(search).get().getSearchQuery());
            blockedRepo.save(b);
            deleted = true;
        }

        return deleted;
    }

    @GetMapping("/flipSearch")
    @Modifying
    public String flipSearch(long search) {
        if(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).isPresent()) {
            blockedRepo.delete(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).get());
            return finalSearchStatRepo.findById(search).get().getSearchQuery();
        } else {
            BlockedSearches bs = new BlockedSearches();
            bs.setSearch(finalSearchStatRepo.findById(search).get().getSearchQuery());
            blockedRepo.save(bs);
            return "DELETED";
        }
    }

    @GetMapping("/flipAnbieterSearch")
    @Modifying
    public String flipAnbieterSearch(long search) {
        if(baSearchRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).isPresent()) {
            baSearchRepo.delete(baSearchRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).get());
            return finalSearchStatRepo.findById(search).get().getSearchQuery();
        } else {
            BlockedSearchesAnbieter bs = new BlockedSearchesAnbieter();
            bs.setSearch(finalSearchStatRepo.findById(search).get().getSearchQuery());
            baSearchRepo.save(bs);
            return "DELETED";
        }
    }

    @GetMapping("/getAllBlocked")
    public String getAllBlocked() throws JSONException {
        JSONArray array = new JSONArray();
        for(BlockedSearches blocked : blockedRepo.findAll()) {
            JSONObject json = new JSONObject();
            json.put("search", blocked.getSearch());
            json.put("id", finalSearchStatRepo.getIdsBySearch(blocked.getSearch()).get(0));
            array.put(json);
        }

        return array.toString();
    }

    @PostMapping("/deleteDLCById")
    @Modifying
    public void deleteDLCById(long id) {
        if(finalDLCRepo.existsById(id)) {
            finalDLCRepo.deleteById(id);
        }
    }


    @GetMapping("/getAnbieterNoneFound")
    public String getAnbieterNoneFound(int page, int size) throws JSONException {
        JSONArray array = new JSONArray();

        for(AnbieterFailedSearchBuffer a : anbieterFailRepo.getPageable(PageRequest.of(page, size))) {
            JSONObject json = new JSONObject();
            json.put("search", a.getSearch());
            json.put("count", a.getCount());
            json.put("city", a.getCity().equals("") ? "none" : a.getCity());
            json.put("id", a.getId());
            array.put(json);
        }

        return array.toString();
    }

    boolean isHack(String text) {
        return text.contains("&") && text.contains(";");
    }

}

