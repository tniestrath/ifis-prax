package com.analysetool.api;

import com.analysetool.modells.*;
import com.analysetool.services.FinalSearchStatService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/search-stats", "/0wB4P2mly-xaRmeeDOj0_g/search-stats"}, method = RequestMethod.GET, produces = "application/json")
public class SearchStatsController {

    @Autowired
    private FinalSearchStatService fSearchStatService;

    @GetMapping("/getAll")
    public List<SearchStats> getAllSearchStats() {
        return fSearchStatService.getAllSearchStats();
    }

    @GetMapping("/getSearchStats")
    public String getSearchStats(@RequestParam int limit) throws JSONException {return fSearchStatService.getSearchStats(limit);}

    @GetMapping("/getSearchStatsByPostWithLimit")
    public String getSearchStatsByPostWithLimit(@RequestParam Long PostId,@RequestParam int limit) throws JSONException {return fSearchStatService.getSearchStatsByPostWithLimit(PostId, limit);}


    /**
     * Endpoint, schlechte Ausreißer basierend auf den gefundenen Anbietern innerhalb eines Radius aller Anbietersuchen zu ermitteln.
     *
     * @return Ein JSON-String, der schlechte Ausreißer repräsentiert (nur wenige oder keine Anbieter).
     * @throws JSONException Falls ein Problem mit der JSON-Verarbeitung auftritt.
     */
    @GetMapping("/getBadOutlierAllProviderSearches")
    public String getBadOutlierAllProviderSearches() throws JSONException {return fSearchStatService.getBadOutlierAllProviderSearches();}

    /**
     * Endpoint, schlechte Ausreißer basierend auf den gefundenen Anbietern innerhalb eines Radius einer gewissen Anzahl an Anbietersuchen zu ermitteln.
     * @param limit = 0 benutzt alle Suchen, sonst normales limit
     * @return Ein JSON-String, der schlechte Ausreißer repräsentiert (nur wenige oder keine Anbieter).
     */
    @GetMapping("/getBadOutlierForXProviderSearches")
    public String getBadOutlierForXProviderSearches(@RequestParam int limit) {return fSearchStatService.getBadOutlierForXProviderSearches(limit);}


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
    public String findBadOutliersEventSearch(@RequestParam int limit) {return fSearchStatService.findBadOutliersEventSearch(limit);}

    @GetMapping("/getZeroCountEventSearches")
    public String getZeroCountEventSearches(){return fSearchStatService.getZeroCountEventSearches();}

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
     * @return A JSON string representing an array of objects. Each object contains the post's ID,
     *         its similarity score to the given post, and its search statistics.
     * @throws JSONException If an issue occurs during JSON processing.
     */
    @GetMapping("/getSearchStatsForSimilarPostsByTags")
    public String getSearchStatsForSimilarPostsByTags(@RequestParam Long postId,@RequestParam float similarityPercentage) throws JSONException {return fSearchStatService.getSearchStatsForSimilarPostsByTags(postId, similarityPercentage);}

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
    @GetMapping("/getDemandByLocation")
    public String getDemandByLocation(@RequestParam String location, @RequestParam String locationType, @RequestParam int searchThreshold, @RequestParam int resultThreshold, @RequestParam String analysisType) {return fSearchStatService.getDemandByLocation(location, locationType, searchThreshold, resultThreshold, analysisType);}

    /**
     *
     * @param page the page of the given size to load. (Page 2 of size 5 loads 5-10).
     * @param size the number of results to load.
     * @param sorter the entry you want to sort by (count | found | ss)
     * @param dir the direction you want to sort. (ASC for Ascending, anything else for DESCENDING)
     * @return a collection of Search-Stats-Data-Entries.
     */
    @GetMapping("/getCoolSearchList")
    public String getCoolSearchList(int page, int size, String sorter, String dir) {return fSearchStatService.getCoolSearchList(page, size, sorter, dir);}



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
    public String getSearchCountDistributedByTime(@RequestParam String distributionType) throws JSONException {return fSearchStatService.getSearchCountDistributedByTime(distributionType);}

    /**
     * Finds all Search-Queries and the number of times they have been searched.
     * Only 'Unfixed', so only searches that have never found anything.
     * Nonsense or non-legit (potential attacks) are NOT listed.
     * @return a JSONArray-String, containing JSON-Objects with 'search' and 'count'
     */
    @GetMapping("/getAllUnfixedSearches")
    public String getAllUnfixedZeroCountSearches(int page, int size) throws JSONException {return fSearchStatService.getAllUnfixedZeroCountSearches(page, size);}

    /**
     * Gets all potentially threatening queries.
     * @return a JSONArray-String, containing JSON-Objects with 'search' and 'count'
     */
    @GetMapping("/getAllThreats")
    public String getAllThreats() throws JSONException {return fSearchStatService.getAllThreats();}

    @PostMapping("/unblockSearch")
    @Modifying
    public boolean unblockSearch(long search) {return fSearchStatService.unblockSearch(search);}

    @PostMapping("/blockSearch")
    @Modifying
    public boolean blockSearch(long search) {return fSearchStatService.blockSearch(search);}

    @GetMapping("/flipSearch")
    @Modifying
    public String flipSearch(long search) {return fSearchStatService.flipSearch(search);}

    @GetMapping("/flipAnbieterSearch")
    @Modifying
    public String flipAnbieterSearch(long search) throws JSONException {return fSearchStatService.flipAnbieterSearch(search);}

    @GetMapping("/flipAnbieterSearchByData")
    @Modifying
    public String flipAnbieterSearch(String search, String place) throws JSONException {return fSearchStatService.flipAnbieterSearch(search, place);}

    @GetMapping("/getAllBlocked")
    public String getAllBlocked() throws JSONException {return fSearchStatService.getAllBlocked();}

    @GetMapping("/getAllAnbieterBlocked")
    public String getAllAnbieterBlocked() throws JSONException {return fSearchStatService.getAllAnbieterBlocked();}

    @PostMapping("/deleteDLCById")
    @Modifying
    public void deleteDLCById(long id) {fSearchStatService.deleteDLCById(id);}


    @GetMapping("/getAnbieterNoneFound")
    public String getAnbieterNoneFound(int page, int size) throws JSONException {return fSearchStatService.getAnbieterNoneFound(page, size);}

}

