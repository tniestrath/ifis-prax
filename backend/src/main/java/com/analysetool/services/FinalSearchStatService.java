package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.MathHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
@Service
public class FinalSearchStatService {

    @Autowired
    private FinalSearchStatRepository repository;
    @Autowired
    private FinalSearchStatDLCRepository DLCRepo;
    @Autowired
    private universalStatsRepository uniRepo;
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
    public FinalSearchStatService(SearchStatsRepository searchStatsRepository) {
        this.searchStatsRepository = searchStatsRepository;
    }



    public List<SearchStats> getAllSearchStats() {
        return searchStatsRepository.findAll();
    }

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


    public String getZeroCountEventSearches(){
        return eventSearchRepo.getEventSearchesWithCountZero().toString();
    }

    public void saveAll(List<FinalSearchStat> stats) {
        repository.saveAll(stats);
    }

    @Transactional
    public Boolean saveAllBoolean(List<FinalSearchStat> stats) {
        try{
            repository.saveAll(stats);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Transactional
    public Boolean saveAllBooleanNotBatch(List<FinalSearchStat> stats) {
        boolean savedAll = false;
        for(FinalSearchStat stat:stats){
          try{
              repository.save(stat);
              savedAll = true;
          }catch(Exception e){
              savedAll=false;
          }
      }
        return savedAll;
    }

    @Transactional
    public Boolean saveAllDLCBooleanNotBatch(List<FinalSearchStatDLC> stats) {
        boolean savedAll = false;
        for(FinalSearchStatDLC stat:stats){
            try{
                DLCRepo.save(stat);
                savedAll = true;
            }catch(Exception e){
                savedAll=false;
            }
        }
        return savedAll;
    }

    @Transactional
    public Boolean saveAllDLCBoolean(List<FinalSearchStatDLC> stats) {
        try{
            DLCRepo.saveAll(stats);
            return true;
        }catch (Exception e){
            return false;
        }
    }



    @Transactional
    public Boolean saveAllDLCBooleanFromMap(Map<String, List<FinalSearchStatDLC>> statsMap) {
        try {

            List<FinalSearchStatDLC> statsList = new ArrayList<>();

            for (List<FinalSearchStatDLC> dlcList : statsMap.values()) {
                statsList.addAll(dlcList);
            }


            DLCRepo.saveAll(statsList);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<FinalSearchStat> getSearchStatsByPostId(Long postId){
        List<Long> FinalSearchStatIds=DLCRepo.getFinalSearchStatIdsByPostId(postId);
        List<FinalSearchStat> stats = repository.findAllById(FinalSearchStatIds);

        return stats;
    }

    public List<FinalSearchStat> getSearchStatsByUserId(Long userId){
        List<Long> FinalSearchStatIds=DLCRepo.getFinalSearchStatIdsByUserId(userId);
        List<FinalSearchStat> stats = repository.findAllById(FinalSearchStatIds);

        return stats;
    }




    public Map<FinalSearchStat,List<FinalSearchStatDLC>> getSearchStatsByLocation(String location, String locationType){
        Map<FinalSearchStat,List<FinalSearchStatDLC>> response = new HashMap<>();
        List<FinalSearchStat> allSearchesOfLocation = switch (locationType) {
            case "city" -> repository.findAllByCity(location);
            case "state" -> repository.findAllByState(location);
            case "country" -> repository.findAllByCountry(location);
            default -> new ArrayList<>();
        };

        List<FinalSearchStatDLC> allSearchSuccessesOfSearch;

        for(FinalSearchStat stat:allSearchesOfLocation){
            allSearchSuccessesOfSearch = DLCRepo.findAllByFinalSearchId(stat.getId());
            response.put(stat,allSearchSuccessesOfSearch);
        }

        return response;
    }

    public List<Map.Entry<String, Integer>> getRankingTopNSearchQueriesInMapBySS(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap, int topN) {
        Map<String, Integer> searchQueryClicks = new HashMap<>();

        // Zusammenführen der Klicks aller Suchanfragen
        searchStatsMap.forEach((searchStat, dlcs) -> {
            String searchQuery = searchStat.getSearchQuery();
            searchQueryClicks.merge(searchQuery, dlcs.size(), Integer::sum);
        });

        // Sortierung und Ermittlung der Top-N-Suchanfragen
        List<Map.Entry<String, Integer>> topSearchQueries = searchQueryClicks.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());

        return topSearchQueries;
    }

    public List<Map.Entry<String, Long>> getRankingTopNSearchedQueries(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap, int topN) {
        // Erstellen einer Map zur Speicherung der Häufigkeit jeder Suchanfrage
        Map<String, Long> searchQueryFrequencies = new HashMap<>();

        // Zählen, wie oft jede Suchanfrage vorkommt
        searchStatsMap.keySet().forEach(searchStat -> {
            String searchQuery = searchStat.getSearchQuery();
            // Erhöhen der Anzahl für jede Suchanfrage
            searchQueryFrequencies.merge(searchQuery, 1L, Long::sum);
        });

        // Sortierung der Map nach Häufigkeit der Suchanfragen und Ermittlung der Top-N
        List<Map.Entry<String, Long>> topSearchedQueries = searchQueryFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());

        return topSearchedQueries;
    }


    /**
     * Ermittelt Suchanfragen, die häufig durchgeführt werden, aber nur wenige oder keine Ergebnisse liefern.
     *
     * @param searchStatsMap Map von FinalSearchStat zu List<FinalSearchStatDLC>, die Suchstatistiken enthält.
     * @param searchThreshold (Optional) Mindestanzahl von Suchvorgängen, um als "häufig gesucht" zu gelten.
     * @param resultThreshold (Optional) Höchstanzahl von Ergebnissen (Klicks), um als "wenig gefunden" zu gelten.
     * @return Eine Map von Suchanfragen zu ihrer jeweiligen Anzahl von Ergebnissen, die die Kriterien erfüllen.
     */
    public Map<String, Integer> findFrequentSearchesWithFewResults(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap, int searchThreshold, int resultThreshold) {
        Map<String, Integer> frequentSearches = new HashMap<>();

        Map<String, Integer> searchQueryCounts = new HashMap<>();

        // Schleife zur Ermittlung der Vorkommen jeder searchQuery und der kumulierten resultCounts
        for (Map.Entry<FinalSearchStat, List<FinalSearchStatDLC>> entry : searchStatsMap.entrySet()) {
            FinalSearchStat searchStat = entry.getKey();
            String searchQuery = searchStat.getSearchQuery();
            int resultCount = searchStat.getFoundArtikelCount() +
                    searchStat.getFoundBlogCount() +
                    searchStat.getFoundNewsCount() +
                    searchStat.getFoundWhitepaperCount() +
                    searchStat.getFoundRatgeberCount() +
                    searchStat.getFoundPodcastCount() +
                    searchStat.getFoundAnbieterCount() +
                    searchStat.getFoundEventsCount();

            // Wenn searchQuery bereits in searchQueryCounts enthalten ist, überprüfen Sie, ob der neue Count größer ist
            if (searchQueryCounts.containsKey(searchQuery)) {
                int currentResultCount = frequentSearches.get(searchQuery);
                if (resultCount > currentResultCount) {
                    searchQueryCounts.put(searchQuery, resultCount);
                }
            } else {
                searchQueryCounts.put(searchQuery, resultCount);
            }
        }

        // Filtern basierend auf searchThreshold und resultThreshold
        for (Map.Entry<String, Integer> entry : searchQueryCounts.entrySet()) {
            String searchQuery = entry.getKey();
            int resultCount = entry.getValue();
            long occurrences = searchStatsMap.keySet().stream()
                    .filter(stat -> stat.getSearchQuery().equals(searchQuery))
                    .count();

            if (occurrences > searchThreshold && resultCount <= resultThreshold) {
                frequentSearches.put(searchQuery, resultCount);
            }
        }

        return frequentSearches;
    }

    /**
     * Retrieves and returns search statistics for posts similar to a given post, based on tag similarity.
     * Similarity is determined by a specified minimum similarity percentage. The result includes each similar
     * post's search statistics, similarity score, and post ID.
     *
     * @param postId The ID of the reference post for which similar posts are sought.
     * @param similarityPercentage The minimum threshold of tag similarity (in percentage)
     *        to consider a post similar to the given post. !!60% = 60 ; 0,6% = 0,6 ...!!<--------------------------
     * @return A JSON string representing an array of objects. Each object contains the post's ID,
     *         its similarity score to the given post, and its search statistics.
     * @throws JSONException If an issue occurs during JSON processing.
     */
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
     * Retrieves demand data based on a location and analysis type.
     *
     * @param location         The location to analyze.
     * @param locationType     The type of location (e.g., city, country, state).
     * @param searchThreshold  The threshold for the number of occurrences for a search query.
     * @param resultThreshold  The threshold for the result count.
     * @param analysisType     The type of analysis to perform ("searchSuccess" or "resultCount").
     * @return A map containing frequent searches with few results based on the given thresholds.
     */
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
     * Finds frequent searches with few search successes based on the given thresholds.
     *
     * @param searchStatsMap         A map containing search statistics mapped to their respective search DLCs.
     * @param searchThreshold        The threshold for the number of occurrences for a search query.
     * @param searchSuccessThreshold The threshold for the search success count.
     * @return A map containing frequent searches with few search successes based on the given thresholds.
     */
    public Map<String, Integer> findFrequentSearchesWithFewSearchSuccesses(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap, int searchThreshold, int searchSuccessThreshold) {
        Map<String, Integer> frequentSearches = new HashMap<>();

        // Map zur Speicherung der Anzahl der Vorkommen jeder searchQuery
        Map<String, Integer> searchQueryOccurrences = new HashMap<>();
        for (FinalSearchStat searchStat : searchStatsMap.keySet()) {
            String searchQuery = searchStat.getSearchQuery();
            searchQueryOccurrences.put(searchQuery, searchQueryOccurrences.getOrDefault(searchQuery, 0) + 1);
        }

        // Map zur Speicherung der Anzahl der Sucherfolge für jede searchQuery
        Map<String, Integer> searchQuerySuccessCounts = new HashMap<>();
        for (Map.Entry<FinalSearchStat, List<FinalSearchStatDLC>> entry : searchStatsMap.entrySet()) {
            FinalSearchStat searchStat = entry.getKey();
            String searchQuery = searchStat.getSearchQuery();
            int successCount = entry.getValue().size(); // Anzahl der Sucherfolge für diese searchQuery
            searchQuerySuccessCounts.put(searchQuery, searchQuerySuccessCounts.getOrDefault(searchQuery, 0) + successCount);
        }

        // Überprüfen und Ausgeben von searchQueries unter dem Threshold
        for (Map.Entry<String, Integer> entry : searchQuerySuccessCounts.entrySet()) {
            String searchQuery = entry.getKey();
            int successCount = entry.getValue();
            int occurrences = searchQueryOccurrences.get(searchQuery);

            // Überprüfen, ob die Anzahl der Vorkommen größer oder gleich dem Suchschwellenwert ist
            if (occurrences >= searchThreshold && successCount <= searchSuccessThreshold) {
                frequentSearches.put(searchQuery, successCount);
            }
        }

        return frequentSearches;
    }

    // Generische Methode zur Analyse von Suchanfragen
    public Map<String, Integer> analyzeSearchQueries(
            Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap,
            int searchThreshold,
            int resultThreshold,
            Function<FinalSearchStat, Integer> resultCounter) {

        // Erzeugen einer Map von searchQuery zu deren DLCs
        Map<String, Integer> queryToResultsCount = searchStatsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getSearchQuery(),
                        e -> resultCounter.apply(e.getKey()) // Anwendung der resultCounter Funktion auf jeden FinalSearchStat
                ));

        // Filtern der Ergebnisse basierend auf den Schwellenwerten
        return queryToResultsCount.entrySet().stream()
                .filter(e -> e.getValue() >= searchThreshold && e.getValue() <= resultThreshold)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Methode zur Berechnung der Gesamtanzahl der Ergebnisse für einen FinalSearchStat
    private int getTotalResultCount(FinalSearchStat stat) {
        return stat.getFoundArtikelCount() +
                stat.getFoundBlogCount() +
                stat.getFoundNewsCount() +
                stat.getFoundWhitepaperCount() +
                stat.getFoundRatgeberCount() +
                stat.getFoundPodcastCount() +
                stat.getFoundAnbieterCount() +
                stat.getFoundEventsCount();
    }

    public Map<Integer,Long> getSearchCountDistributedByUniId(){
        Map<Integer,Long> response = new HashMap<>();
        List<Object[]> obj = repository.findUniIdCounts();
        for(Object[] object:obj){
            Integer uniId = ((Number) object[0]).intValue();
            Long count = ((Number) object[1]).longValue();
            System.out.println("uniId : "+uniId+" count: "+count);
            response.put(uniId,count);
        }
        return response;
    }
    public String toStringList(List<FinalSearchStat>stats){
        StringBuilder response = new StringBuilder();

        for(FinalSearchStat stat:stats){
            response.append(toString(stat));
        }

        return response.toString();
    }

    public String toString(FinalSearchStat stat){
        int uniId = stat.getUniId();
        int newestUniId = uniRepo.getLatestUniStat().getId();
        int daysBack= newestUniId-uniId;
        LocalDate date = LocalDate.now().minusDays(daysBack);
        return stat.toStringAlt(date);
    }

    /**
     *
     * @param page the page of the given size to load. (Page 2 of size 5 loads 5-10).
     * @param size the number of results to load.
     * @param sorter the entry you want to sort by (count | found | ss)
     * @param dir the direction you want to sort. (ASC for Ascending, anything else for DESCENDING)
     * @return a collection of Search-Stats-Data-Entries.
     */
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
    public String getAllUnfixedZeroCountSearches(int page, int size) throws JSONException {
        JSONArray array = new JSONArray();

        for(Tuple pair : finalSearchStatRepo.getAllUnfixedSearchesWithZeroFound("%&%;%", PageRequest.of(page, size))) {
            JSONObject json = new JSONObject();
            //noinspection RedundantCast
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

    public boolean unblockSearch(long search) {
        boolean unblocked = false;
        //noinspection OptionalGetWithoutIsPresent
        if(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).isPresent()) {
            //noinspection OptionalGetWithoutIsPresent
            blockedRepo.delete(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).get());
            unblocked = true;
        }

        return unblocked;
    }

    public boolean blockSearch(long search) {
        boolean deleted = false;
        //noinspection OptionalGetWithoutIsPresent
        if(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).isEmpty()) {
            BlockedSearches b = new BlockedSearches();
            //noinspection OptionalGetWithoutIsPresent
            b.setSearch(finalSearchStatRepo.findById(search).get().getSearchQuery());
            blockedRepo.save(b);
            deleted = true;
        }

        return deleted;
    }

    public String flipSearch(long search) {
        //noinspection OptionalGetWithoutIsPresent
        if(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).isPresent()) {
            //noinspection OptionalGetWithoutIsPresent
            blockedRepo.delete(blockedRepo.getBySearch(finalSearchStatRepo.findById(search).get().getSearchQuery()).get());
            //noinspection OptionalGetWithoutIsPresent
            return finalSearchStatRepo.findById(search).get().getSearchQuery();
        } else {
            BlockedSearches bs = new BlockedSearches();
            //noinspection OptionalGetWithoutIsPresent
            bs.setSearch(finalSearchStatRepo.findById(search).get().getSearchQuery());
            blockedRepo.save(bs);
            return "DELETED";
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
                //noinspection RedundantCast
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
                //noinspection RedundantCast
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
                //noinspection RedundantCast
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


    public String flipAnbieterSearch(long search) throws JSONException {
        @SuppressWarnings("OptionalGetWithoutIsPresent") AnbieterFailedSearchBuffer afb = anbieterFailRepo.findById(search).get();
        JSONObject json = new JSONObject();
        if(baSearchRepo.getBySearchAndPlace(afb.getSearch(), afb.getCity()).isPresent()) {
            baSearchRepo.delete(baSearchRepo.getBySearchAndPlace(afb.getSearch(), afb.getCity()).get());
            json.put("city", afb.getCity());
            json.put("query", afb.getSearch());
        } else {
            BlockedSearchesAnbieter bs = new BlockedSearchesAnbieter();
            bs.setSearch(afb.getSearch());
            bs.setPlace(afb.getCity());
            baSearchRepo.save(bs);
            json.put("query", "DELETED");
        }
        return json.toString();
    }


    public String flipAnbieterSearch(String search, String place) throws JSONException {
        @SuppressWarnings("OptionalGetWithoutIsPresent") AnbieterFailedSearchBuffer afb = anbieterFailRepo.findByCityAndSearch(place, search).get();
        JSONObject json = new JSONObject();
        if(baSearchRepo.getBySearchAndPlace(afb.getSearch(), afb.getCity()).isPresent()) {
            baSearchRepo.delete(baSearchRepo.getBySearchAndPlace(afb.getSearch(), afb.getCity()).get());
            json.put("city", afb.getCity());
            json.put("query", afb.getSearch());
        } else {
            BlockedSearchesAnbieter bs = new BlockedSearchesAnbieter();
            bs.setSearch(search);
            bs.setPlace(afb.getCity());
            baSearchRepo.save(bs);
            json.put("query", "DELETED");
        }
        return json.toString();
    }


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


    public String getAllAnbieterBlocked() throws JSONException {
        JSONArray array = new JSONArray();
        for(BlockedSearchesAnbieter blocked : baSearchRepo.findAll()) {
            JSONObject json = new JSONObject();
            json.put("search", blocked.getSearch());
            json.put("place", blocked.getPlace());
            array.put(json);
        }

        return array.toString();
    }


    public void deleteDLCById(long id) {
        if(finalDLCRepo.existsById(id)) {
            finalDLCRepo.deleteById(id);
        }
    }



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
