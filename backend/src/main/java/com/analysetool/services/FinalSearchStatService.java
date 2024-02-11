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


    public List<Map.Entry<String, Integer>> getRankingTopNSearchQueriesInMap(Map<FinalSearchStat, List<FinalSearchStatDLC>> searchStatsMap, int topN) {
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

    public Map<Integer, Long> getPopularSearchHours(List<FinalSearchStat> searchStats) {
        return searchStats.stream()
                .collect(Collectors.groupingBy(FinalSearchStat::getHour, Collectors.counting()));
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
