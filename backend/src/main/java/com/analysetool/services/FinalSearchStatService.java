package com.analysetool.services;

import com.analysetool.modells.FinalSearchStat;
import com.analysetool.modells.FinalSearchStatDLC;
import com.analysetool.repositories.FinalSearchStatDLCRepository;
import com.analysetool.repositories.FinalSearchStatRepository;
import com.analysetool.repositories.universalStatsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FinalSearchStatService {

    @Autowired
    private FinalSearchStatRepository repository;
    @Autowired
    private FinalSearchStatDLCRepository DLCRepo;
    @Autowired
    private universalStatsRepository uniRepo;
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
        Boolean savedAll = false;
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
        Boolean savedAll = false;
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
        List<FinalSearchStat> allSearchesOfLocation = new ArrayList<>();

        switch(locationType){
            case "city":
                allSearchesOfLocation = repository.findAllByCity(location);
                break;
            case "state":
                allSearchesOfLocation = repository.findAllByState(location);
                break;
            case "country":
                allSearchesOfLocation = repository.findAllByCountry(location);
                break;
        }

        List<FinalSearchStatDLC> allSearchSuccessesOfSearch = new ArrayList<>();

        for(FinalSearchStat stat:allSearchesOfLocation){
            allSearchSuccessesOfSearch = DLCRepo.findAllByFinalSearchId(stat.getId());
            response.put(stat,allSearchSuccessesOfSearch);
        }

        return response;
    }

    public Map<FinalSearchStat,List<FinalSearchStatDLC>> getAllSearchStats(){
        Map<FinalSearchStat,List<FinalSearchStatDLC>> response = new HashMap<>();
        List<FinalSearchStat> allSearches = repository.findAll();



        for(FinalSearchStat stat:allSearches){
            List<FinalSearchStatDLC> allSearchSuccessesOfSearch = DLCRepo.findAllByFinalSearchId(stat.getId());
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
     * Finds frequent searches with few search successes based on the given thresholds.
     *
     * @param searchStatsMap         A map containing search statistics mapped to their respective search DLCs.
     * @param searchThreshold        The threshold for the number of occurrences of a search query.
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

/*
    // Optimierung für Sucherfolge
    public Map<String, Integer> findFrequentSearchesWithFewSearchSuccessesOptimized(
            Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap,
            int searchThreshold,
            int searchSuccessThreshold) {

        // Nutzung der analyzeSearchQueries Methode mit einer angepassten resultCounter Funktion
        return analyzeSearchQueries(searchStatsMap, searchThreshold, searchSuccessThreshold,
                stat -> stat.getList().size()); // Hier muss die Logik angepasst werden, um die Anzahl der Sucherfolge korrekt zu zählen
    }

    // Optimierung für Resultat-Zählungen
    public Map<String, Integer> findFrequentSearchesWithFewResultsOptimized(
            Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap,
            int searchThreshold,
            int resultThreshold) {

        return analyzeSearchQueries(searchStatsMap, searchThreshold, resultThreshold, this::getTotalResultCount);
    }
*/


    public Map<Integer, Long> getPopularSearchHours(List<FinalSearchStat> searchStats) {
        return searchStats.stream()
                .collect(Collectors.groupingBy(FinalSearchStat::getHour, Collectors.counting()));
    }

    public Map<String, Map<String, Long>> getPopularSearchQueriesByLocation(List<FinalSearchStat> searchStats, String locationType) {
        Function<FinalSearchStat, String> locationFunction = "city".equals(locationType) ? FinalSearchStat::getCity : "country".equals(locationType)? FinalSearchStat::getCountry : FinalSearchStat::getState  ;

        return searchStats.stream()
                .collect(Collectors.groupingBy(locationFunction,
                        Collectors.groupingBy(FinalSearchStat::getSearchQuery, Collectors.counting())));
    }



    public Map<String, Double> getSearchSuccessRate(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap) {
        // Berechnen der Gesamtanzahl von Suchanfragen pro Suchbegriff
        Map<String, Long> totalSearchesPerQuery = searchStatsMap.keySet().stream()
                .collect(Collectors.groupingBy(FinalSearchStat::getSearchQuery, Collectors.counting()));

        // Berechnen der Gesamtanzahl von Klicks pro Suchbegriff
        Map<String, Long> totalClicksPerQuery = searchStatsMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(dlc -> new AbstractMap.SimpleEntry<>(entry.getKey().getSearchQuery(), dlc)))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.counting()));

        // Berechnen der Erfolgsrate pro Suchbegriff
        return totalSearchesPerQuery.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            Long totalClicks = totalClicksPerQuery.getOrDefault(entry.getKey(), 0L);
                            Long totalSearches = entry.getValue();
                            // Verhindern der Division durch Null
                            if (totalSearches > 0) {
                                return (totalClicks.doubleValue() / totalSearches) * 100; // Multipliziert mit 100, um Prozentwerte zu erhalten
                            } else {
                                return 0.0; // Keine Suchanfragen entspricht einer Erfolgsrate von 0%
                            }
                        }));
    }

    public double getAverageResultsPerSearch(List<FinalSearchStat> searchStats) {
        if (searchStats.isEmpty()) {
            return 0.0;
        }

        double totalResults = searchStats.stream()
                .mapToInt(stat -> stat.getFoundArtikelCount() + stat.getFoundBlogCount() + stat.getFoundNewsCount() +
                        stat.getFoundWhitepaperCount() + stat.getFoundRatgeberCount() + stat.getFoundPodcastCount() +
                        stat.getFoundAnbieterCount() + stat.getFoundEventsCount())
                .sum();

        return totalResults / searchStats.size();
    }

    public double getAverageSearchSuccess(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap) {
        if (searchStatsMap.isEmpty()) {
            return 0.0; // Verhindert Division durch Null, falls keine Daten vorhanden sind.
        }

        // Berechnen der Gesamtanzahl von Klicks über alle Suchanfragen.
        long totalClicks = searchStatsMap.values().stream()
                .mapToLong(List::size) // Größe jeder Liste gibt die Anzahl der Klicks pro Suchanfrage an.
                .sum();

        // Berechnen der Gesamtanzahl der Suchanfragen.
        long totalSearches = searchStatsMap.size();

        // Berechnen der durchschnittlichen Erfolgsrate als Verhältnis von totalClicks zu totalSearches.
        return (double) totalClicks / totalSearches;
    }

    public Map<String, Integer> getContentTypesDistribution(List<FinalSearchStat> searchStats) {
        Map<String, Integer> contentDistribution = new HashMap<>();

        searchStats.forEach(stat -> {
            contentDistribution.merge("Artikel", stat.getFoundArtikelCount(), Integer::sum);
            contentDistribution.merge("Blogs", stat.getFoundBlogCount(), Integer::sum);
            contentDistribution.merge("News", stat.getFoundNewsCount(), Integer::sum);
            contentDistribution.merge("Whitepapers", stat.getFoundWhitepaperCount(), Integer::sum);
            contentDistribution.merge("Ratgeber", stat.getFoundRatgeberCount(), Integer::sum);
            contentDistribution.merge("Podcasts", stat.getFoundPodcastCount(), Integer::sum);
            contentDistribution.merge("Anbieter", stat.getFoundAnbieterCount(), Integer::sum);
            contentDistribution.merge("Events", stat.getFoundEventsCount(), Integer::sum);
        });

        return contentDistribution;
    }

    public Map<String, Long> getSearchQueryDistributionByCountry(List<FinalSearchStat> searchStats) {
        return searchStats.stream()
                .collect(Collectors.groupingBy(FinalSearchStat::getCountry, Collectors.counting()));
    }

    public Map<String, Long> getSearchQueryDistributionByCity(List<FinalSearchStat> searchStats) {
        return searchStats.stream()
                .collect(Collectors.groupingBy(FinalSearchStat::getCity, Collectors.counting()));
    }

    public Map<String, Long> getSearchQueryDistributionByState(List<FinalSearchStat> searchStats) {
        return searchStats.stream()
                .collect(Collectors.groupingBy(FinalSearchStat::getState, Collectors.counting()));
    }

    public Map<Long, Long> getMostFoundPosts(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap) {
        return searchStatsMap.values().stream()
                .flatMap(List::stream) // Erstellen einer Stream aus allen DLC-Objekten
                .map(FinalSearchStatDLC::getPostId) // Extrahieren der postId
                .filter(Objects::nonNull) // Sicherstellen, dass die postId nicht null ist
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) // Zählen der Vorkommen jeder postId
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed()) // Sortieren der Einträge nach ihrer Häufigkeit, absteigend
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new)); // Sammeln der Ergebnisse in einer Map, die die Reihenfolge beibehält
    }

    public Map<Long, Long> getMostFoundUsers(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap) {
        return searchStatsMap.values().stream()
                .flatMap(List::stream) // Erstellen einer Stream aus allen DLC-Objekten
                .map(FinalSearchStatDLC::getUserId) // Extrahieren der userId
                .filter(Objects::nonNull) // Sicherstellen, dass die userId nicht null ist
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) // Zählen der Vorkommen jeder userId
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed()) // Sortieren der Einträge nach ihrer Häufigkeit, absteigend
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new)); // Sammeln der Ergebnisse in einer Map, die die Reihenfolge beibehält
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
        String response = "";

        for(FinalSearchStat stat:stats){
            response=response+toString(stat);
        }

        return response;
    }

    public String toString(FinalSearchStat stat){
        int uniId = stat.getUniId();
        int newestUniId = uniRepo.getLatestUniStat().getId();
        int daysBack= newestUniId-uniId;
        LocalDate date = LocalDate.now().minusDays(daysBack);
        return stat.toStringAlt(date);
    }

}
