package com.analysetool.api;

import com.analysetool.modells.AnbieterSearch;
import com.analysetool.modells.FinalSearchStat;
import com.analysetool.modells.SearchStats;
import com.analysetool.repositories.AnbieterSearchRepository;
import com.analysetool.repositories.SearchStatsRepository;
import com.analysetool.services.FinalSearchStatService;
import com.analysetool.util.MathHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.analysetool.modells.EventSearch;
import com.analysetool.repositories.EventSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
            obj.put("search_succes", alleStats.get(i).getSearchSuccessFlag());
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
     * @return Ein JSON-String, der schlechte Ausreißer repräsentiert(nur wenige oder keine Anbieter).
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */
    @GetMapping("/getBadOutlierAllProviderSearches")
    public String getBadOutlierAllProviderSearches() throws JSONException {
        JSONArray Ergebnis = new JSONArray();
        List<AnbieterSearch> anbieterSearches= anbieterSearchRepo.findAll();
        List<Integer> counts=new ArrayList<>();

        for(AnbieterSearch a:anbieterSearches){
            counts.add(a.getCount_found());}
        double mittelwert = MathHelper.getMeanInt(counts);
        //alle Ausreißer
        List<Integer> Outlier = MathHelper.getOutliersInt(counts);

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
     * @return Ein JSON-String, der schlechte Ausreißer repräsentiert(nur wenige oder keine Anbieter).
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
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
            System.out.println("leS "+latestEventSearches.toString());
            List<Integer> resultCounts = latestEventSearches.stream()
                    .map(EventSearch::getResultCount)
                    .collect(Collectors.toList());
            System.out.println("count "+resultCounts.toString());
            List<Integer> lowerBoundOutliers = MathHelper.getLowerBoundOutliersInt(resultCounts);
            System.out.println("lbO "+lowerBoundOutliers.toString());
            List<EventSearch> filteredEventSearches = latestEventSearches.stream()
                    .filter(eventSearch -> lowerBoundOutliers.contains(eventSearch.getResultCount()))
                    .collect(Collectors.toList());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(filteredEventSearches);
        } catch (Exception e) {
            return "Fehler beim Verarbeiten der Daten";
        }
    }

    @GetMapping("getZeroCountEventSearches")
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

}

