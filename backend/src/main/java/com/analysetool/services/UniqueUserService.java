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

import java.time.LocalDateTime;
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
    @Autowired
    UniqueUserService uniqueUserService;

    /**
     * Fetches the average time spent of all users.
     * @return .
     */
    public String getAverageTimeSpent() {
        Double averageTimeSpent = uniqueUserRepo.getAverageTimeSpent();
        return averageTimeSpent != null ? String.format("%.2f", averageTimeSpent) : "Daten nicht verfügbar";
    }

    /**
     * Fetches the average time spent of all users today.
     * @return .
     */
    public String getTodayAverageTimeSpent() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        Double todayAverageTimeSpent = uniqueUserRepo.getAverageTimeSpentBetweenDates(startOfDay, endOfDay);
        return todayAverageTimeSpent != null ? String.format("%.2f", todayAverageTimeSpent) : "Daten nicht verfügbar";
    }



    private String reconstructClickPath(UniqueUser user) {
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

    private void processCategoryClicks(String categoryData, String categoryName, Map<Integer, String> clickMap) throws JSONException {
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

    /**
     * Checks whether a user is potentially a bot.
     * @param user the user to check for.
     * @param repeatedClicks how many repeated clicks in a single category are allowed.
     * @return whether the user is potentially a bot.
     */
    public boolean isPotentialBot(UniqueUser user, int repeatedClicks) {
        Map<Integer, String> clickMap = new TreeMap<>();
        try {
            processAllCategoryClicks(user, clickMap);

        } catch (Exception e) {
            System.out.println("Error in processing clicks: " + e.getMessage());
        }

        return hasSuspiciousClickPattern(clickMap,repeatedClicks) || hasNumberOverNonsense(clickMap, repeatedClicks);
    }

    /**
     * Checks whether the user-click-pattern is suspicious.
     * @param clickMap a click map.
     * @param repeatedClicks how many repeated clicks are allowed.
     * @return whether the user is suspicious.
     */
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

    /**
     * Checks whether a user has more clicks than accepted in "nonsense".
     * @param clickMap the map to check in.
     * @param nonsenseClicks the amount of nonsense-clicks that are allowed.
     * @return whether the user is over the limit.
     */
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

    /**
     * Retrieves a list of users who are potentially bots, by checking their click behavior.
     * @return a List of IPs.
     * @throws JSONException .
     */
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

    /**
     * Fetches a String representation of user-click-paths, that have had more than 2 clicks.
     * @param limit the amount of users to fetch at most.
     * @return String representation of user-click-paths.
     */
    public String getUserPaths(int limit) {
        Pageable topLimit = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        List<UniqueUser> users = uniqueUserRepo.findTopByMoreThanTwoClicks(topLimit);

        return users.stream()
                .map(this::reconstructClickPath)
                .collect(Collectors.joining(", "));
    }

    /**
     * Fetches all user click-paths.
     * @return a String representation of all user-click-paths.
     */
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

    /**
     * Finds all Users that have been clicking more often than once per second.
     * @return a List of UniqueUsers.
     */
    public List<UniqueUser> getBotsByClicksOverTime() {
        List<UniqueUser> users = uniqueUserRepo.findAll();
        List<UniqueUser> bots = new ArrayList<>();

        for(UniqueUser user : users) {
            if(user.getAmount_of_clicks() > 5 && user.getAmount_of_clicks() > user.getTime_spent()) {
                bots.add(user);
            }
        }
        return bots;
    }

    /**
     * Fetch the category the clicks of a user have been tracked in.
     * @param user the user to look for.
     * @return ??
     */
    public Map<String, Long> getClicksCategory(UniqueUser user) throws JSONException {
        Map<Integer, String> clickMap = new TreeMap<>();
        // Wiederholung des Prozesses, um die Klicks in Kategorien zu erfassen
        processAllCategoryClicks(user, clickMap);

        // Zählen, wie oft in jeder Kategorie geklickt wurde
        Map<String, Long> categoryClicksCount = clickMap.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return categoryClicksCount;
    }

    /**
     * Checks whether all clicks in a map have been in a single category.
     * @param clickMap the map to look in.
     * @return whether all clicks have been in a single category.
     */
    public boolean areClicksInSingleCategory(Map<String, Long> clickMap) {

        Set<String> uniqueCategories = new HashSet<>(clickMap.keySet());


        return uniqueCategories.size() == 1;
    }

    /**
     * Fetch the category the clicks in a map have been tracked in.
     * @param clickMap the map to look in.
     * @return ??
     */
    public String getCategoryOfClicks(Map<String, Long> clickMap) {
        Set<String> uniqueCategories = new HashSet<>(clickMap.keySet());

        //noinspection OptionalGetWithoutIsPresent
        return uniqueCategories.stream().findFirst().get();

    }

    /**
     * Fetch the bounce-rate in the current day.
     * @return a String representation of the bounce-rate today.
     */
    public double getBounceRateToday(){

        Long allUserCount = uniqueUserRepo.getCountOfAllUser();
        Long zeroClicksUserCount = uniqueUserRepo.getCountOfZeroClicksUser();

        return (double)zeroClicksUserCount/allUserCount;
    }

    /**
     * Fetch all IPs today.
     * @return a List of IP Addresses as String.
     */
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

    /**
     * Fetch click depths of users in segments, mapped to the amount of users in the depth-segment.
     * @return a JSON-Object mapping depth-segments to amount of users in depth-segment.
     */
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

}
