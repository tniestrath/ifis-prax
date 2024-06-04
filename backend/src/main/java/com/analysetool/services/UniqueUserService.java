package com.analysetool.services;

import com.analysetool.modells.TrackingBlacklist;
import com.analysetool.modells.UniqueUser;
import com.analysetool.repositories.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;



@Service
public class UniqueUserService {

    @Autowired
    UniqueUserRepository uniqueUserRepo;

    @Autowired
    universalStatsRepository uniRepo;

    @Autowired
    TrackingBlacklistRepository trackBlackRepo;

    public String reconstructClickPath(UniqueUser user) {
        Map<Integer, String> clickMap = new TreeMap<>();
        try{
            processAllCategoryClicks(user, clickMap);
        }
        catch (Exception e){System.out.println("computer sagt nein");}

        return String.join(",", clickMap.values());
    }

    private void processAllCategoryClicks(UniqueUser user, Map<Integer, String> clickMap) throws JSONException {
        processCategoryClicks(user.getArticle(), "article", clickMap);
        processCategoryClicks(user.getBlog(), "blog", clickMap);
        processCategoryClicks(user.getNews(), "news", clickMap);
        processCategoryClicks(user.getWhitepaper(), "whitepaper", clickMap);
        processCategoryClicks(user.getPodcast(), "podcast", clickMap);
        processCategoryClicks(user.getRatgeber(), "ratgeber", clickMap);
        processCategoryClicks(user.getMain(), "main", clickMap);
        processCategoryClicks(user.getUeber(), "ueber", clickMap);
        processCategoryClicks(user.getImpressum(), "impressum", clickMap);
        processCategoryClicks(user.getPreisliste(), "preisliste", clickMap);
        processCategoryClicks(user.getPartner(), "partner", clickMap);
        processCategoryClicks(user.getDatenschutz(), "datenschutz", clickMap);
        processCategoryClicks(user.getNewsletter(), "newsletter", clickMap);
        processCategoryClicks(user.getImage(), "image", clickMap);
        processCategoryClicks(user.getAgb(), "agb", clickMap);
        processCategoryClicks(user.getNonsense(), "nonsense", clickMap);
    }

    private Boolean isConsumer(Map<String,Long> cliokMap,Integer dwellTime){
        Boolean dwellTimeOver5min = (dwellTime >= 300);
        if (dwellTimeOver5min){
            Boolean clickInConsumingCategory =
                    (cliokMap.get("article")>=1) || (cliokMap.get("blog")>=1) || (cliokMap.get("news")>=1) || (cliokMap.get("whitepaper")>=1) || (cliokMap.get("podcast")>=1) || (cliokMap.get("ratgeber")>=1);
            return clickInConsumingCategory;
        }else{return false;}
    }

    public void processCategoryClicks(String categoryData, String categoryName, Map<Integer, String> clickMap) throws JSONException {
        if (categoryData != null && !categoryData.isEmpty()) {
            JSONArray clicksArray = new JSONArray(categoryData);
            for (int i = 0; i < clicksArray.length(); i++) {
                int clickNum = clicksArray.getInt(i);
                if (clickNum > 0) {
                    clickMap.put(clickNum, categoryName);
                }
            }
        }
    }


    public boolean isPotentialBot(UniqueUser user, int repeatedClicks) {
        Map<Integer, String> clickMap = new TreeMap<>();
        try {
            processAllCategoryClicks(user, clickMap);

        } catch (Exception e) {
            System.out.println("Error in processing clicks: " + e.getMessage());
        }

        return hasSuspiciousClickPattern(clickMap,repeatedClicks) || hasNumberOverNonsense(clickMap, repeatedClicks);
    }

    public boolean hasSuspiciousClickPattern(Map<Integer, String> clickMap, int repeatedClicks) {
        String lastCategory = "";
        int repeatCount = 0;

        for (String category : clickMap.values()) {
            if (category.equals(lastCategory)) {
                repeatCount++;
                if (repeatCount >= repeatedClicks) { // mehr Wiederholungen sind verdächtig
                    return true;
                }
            } else {
                lastCategory = category;
                repeatCount = 1;
            }
        }

        return false;
    }

    public boolean hasNumberOverNonsense(Map<Integer, String> clickMap, int nonsenseClicks) {
        int repeatCount = 0;

        for (String category : clickMap.values()) {
            if (category.equals("nonsense")) {
                repeatCount++;
                if (repeatCount >= nonsenseClicks) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<String> getIpsOfPotBots() throws JSONException {
        List<String> potBots = new ArrayList<>();
        for(UniqueUser u : uniqueUserRepo.findAllByMoreThanTwoClicks()) {
            JSONArray array = new JSONArray(u.getNonsense());
            if(array.length() > 10 || (array.length() > 3 && u.getAmount_of_clicks() < 10)) {
                potBots.add(u.getIp());
            } else {
                if(u.getAmount_of_clicks() / 2 < array.length()) {
                    potBots.add(u.getIp());
                }
            }
        }
        return potBots;
    }

    public String getUserPaths(int limit) {
        Pageable topLimit = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        List<UniqueUser> users = uniqueUserRepo.findTopByMoreThanTwoClicks(topLimit);

        return users.stream()
                .map(this::reconstructClickPath)
                .collect(Collectors.joining(", "));
    }

    public String getAllUserPaths() {
        List<UniqueUser> users = uniqueUserRepo.findAllByMoreThanTwoClicks();

        return users.stream()
                .map(this::reconstructClickPath)
                .collect(Collectors.joining(", "));
    }

    /**
     * Retrieves a list of users who are potentially bots based on their click patterns.
     * This method scans through all users in the database and applies a filtering logic to identify
     * potential bots. A user is considered a potential bot if their click behavior shows a pattern of
     * repeatedly clicking on the same category in a short sequence, e.g., 'main, main, main...'.
     * The method uses a threshold of three consecutive clicks in the same category as a criterion for
     * suspicious behavior, which can be adjusted as per requirements.
     *
     * @return List<UniqueUser> containing users who are identified as potential bots based on the defined criteria.
     */
    public List<UniqueUser> getPossibleBots(int repeatedClicks) {
        List<UniqueUser> users = uniqueUserRepo.findAll();
        return users.stream()
                .filter(user -> isPotentialBot(user, repeatedClicks)) // Use lambda to pass both parameters
                .collect(Collectors.toList());
    }


    public Map<String, Long> getClicksCategory(UniqueUser user) throws JSONException {
        Map<Integer, String> clickMap = new TreeMap<>();
        // Wiederholung des Prozesses, um die Klicks in Kategorien zu erfassen
        processAllCategoryClicks(user, clickMap);

        // Zählen, wie oft in jeder Kategorie geklickt wurde
        Map<String, Long> categoryClicksCount = clickMap.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return categoryClicksCount;
    }

    public boolean areClicksInSingleCategory(Map<String, Long> clickMap) {

        Set<String> uniqueCategories = new HashSet<>(clickMap.keySet());


        return uniqueCategories.size() == 1;
    }

    public String getCategoryOfClicks(Map<String, Long> clickMap) {
        Set<String> uniqueCategories = new HashSet<>(clickMap.keySet());

        //noinspection OptionalGetWithoutIsPresent
        return uniqueCategories.stream().findFirst().get();

    }


    public double getBounceRateToday(){

        Long allUserCount = uniqueUserRepo.getCountOfAllUser();
        Long zeroClicksUserCount = uniqueUserRepo.getCountOfZeroClicksUser();

        return (double)zeroClicksUserCount/allUserCount;
    }

    public List<String> getIpsToday(){
       return uniqueUserRepo.getAllIps();
    }

    /**
     * Filtert die Liste aller UniqueUser um diejenigen auszuschließen, die in der TrackingBlacklist enthalten sind.
     *
     * @param uniques Die vollständige Liste von UniqueUser Objekten.
     * @param blocked Die vollständige Liste von TrackingBlacklist Objekten.
     * @return Eine gefilterte Liste von UniqueUser Objekten, die nicht in der TrackingBlacklist enthalten sind.
     */
    public List<UniqueUser> filterOutBlocked(List<UniqueUser> uniques, List<TrackingBlacklist> blocked) {

        Set<String> blockedIPs = new HashSet<>();
        for (TrackingBlacklist b : blocked) {
            blockedIPs.add(b.getIp());
        }

        List<UniqueUser> filtered = new ArrayList<>();
        for (UniqueUser u : uniques) {
            if (!blockedIPs.contains(u.getIp())) {
                filtered.add(u);
            }
        }

        return filtered;
    }

    /**
     * Gliedert die Verweildauer aller nicht blockierten UniqueUser in definierte Zeitsegmente
     * und zählt die Anzahl der Benutzer in jedem Segment. Die Ergebnisse werden in einem JSON-Objekt gespeichert.
     *
     * @return Ein String im JSON-Format, der die Zeitsegmente als Schlüssel und die Anzahl der Benutzer als Werte enthält.
     *         Beispiel: {"0-10 Sekunden": 5, "11-30 Sekunden": 15, "31-60 Sekunden": 20, "61-180 Sekunden": 7, "181-600 Sekunden": 2, "601-1800 Sekunden": 1, "1800+ Sekunden": 0}
     */
    public String getSessionTimeInTimeSegments() throws JSONException {
        List<UniqueUser> allUniques = uniqueUserRepo.findAll();
        List<TrackingBlacklist> allBlocked = trackBlackRepo.findAll();

        List<UniqueUser> filteredUniques = filterOutBlocked(allUniques, allBlocked);
        JSONObject obj = new JSONObject();

        // Segmentierung der Verweildauer in Sekunden
        int[] segments = {0, 11, 31, 61, 181, 601, 1801}; // Die oberen Grenzen für jedes Segment (exclusive das letzte Element)
        int[] counts = new int[segments.length];

        for (UniqueUser user : filteredUniques) {
            int timeSpent = user.getTime_spent();

            // Segment finden und Zähler inkrementieren
            for (int i = 0; i < segments.length; i++) {
                if (timeSpent < segments[i]) {
                    counts[i]++;
                    break;
                } else if (i == segments.length - 1 && timeSpent >= segments[i]) {
                    counts[i]++;
                }
            }
        }


        String[] segmentLabels = {"0-10 Sekunden", "11-30 Sekunden", "31-60 Sekunden", "61-180 Sekunden", "181-600 Sekunden", "601-1800 Sekunden", "1800+ Sekunden"};
        for (int i = 0; i < segmentLabels.length; i++) {
            obj.put(segmentLabels[i], counts[i]);
        }

        return obj.toString();
    }

    public String getVisitorsDepthInSegments() throws JSONException {
        List<UniqueUser> allUniques = uniqueUserRepo.findAll();
        List<TrackingBlacklist> allBlocked = trackBlackRepo.findAll();

        List<UniqueUser> filteredUniques = filterOutBlocked(allUniques, allBlocked);
        JSONObject obj = new JSONObject();

        // Segmentierung der Tiefe
        int[] segments = {1, 2, 3, 4, 5};
        int[] counts = new int[segments.length + 1];

        for (UniqueUser user : filteredUniques) {
            int clickDepth = user.getAmount_of_clicks();

            // Segment finden und Zähler inkrementieren
            boolean segmented = false;
            for (int i = 0; i < segments.length; i++) {
                if (clickDepth <= segments[i]) {
                    counts[i]++;
                    segmented = true;
                    break;
                }
            }
            if (!segmented) {
                counts[segments.length]++;
            }
        }

        String[] segmentLabels = {"1 Klick", "2 Klicks", "3 Klicks", "4 Klicks", "5 Klicks", "6+ Klicks"};
        for (int i = 0; i < segmentLabels.length; i++) {
            obj.put(segmentLabels[i], counts[i]);
        }

        return obj.toString();
    }

    public String getVisitorAndConsumerAndProsumerCounts() throws JSONException {
        List<UniqueUser> allUniques = uniqueUserRepo.findAll();
        List<TrackingBlacklist> allBlocked = trackBlackRepo.findAll();

        List<UniqueUser> filteredUniques = filterOutBlocked(allUniques, allBlocked);
        JSONObject obj = new JSONObject();

        Long visitorCount = 0L;
        Long consumerCount = 0L;
        //prosumer anhand von unique temporäre Suchen (kommt noch)
        Long prosumerCount = 0L;

        for(UniqueUser u : filteredUniques) {
            Map<String,Long> clicksMap = getClicksCategory(u);
            if(isConsumer(clicksMap,u.getTime_spent())){
                consumerCount++;
            }else{visitorCount++;}
        }

        obj.put("Visitors",visitorCount);
        obj.put("Consumers",consumerCount);

        return obj.toString();
    }
}
