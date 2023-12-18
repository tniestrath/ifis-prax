package com.analysetool.services;
import com.analysetool.repositories.UniqueUserRepository;
import com.analysetool.modells.UniqueUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class UniqueUserService {

    @Autowired
    UniqueUserRepository uniqueUserRepo;
    public String reconstructClickPath(UniqueUser user) {
        Map<Integer, String> clickMap = new TreeMap<>();
        try{
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
            processCategoryClicks(user.getAgb(), "agb", clickMap);}
        catch (Exception e){System.out.println("computer sagt nein");}

        return clickMap.values().stream()
                .collect(Collectors.joining(","));
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


    public boolean isPotentialBot(UniqueUser user) {
        Map<Integer, String> clickMap = new TreeMap<>();
        try {
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

        } catch (Exception e) {
            System.out.println("Error in processing clicks: " + e.getMessage());
        }

        return hasSuspiciousClickPattern(clickMap);
    }

    public boolean hasSuspiciousClickPattern(Map<Integer, String> clickMap) {
        String lastCategory = "";
        int repeatCount = 0;

        for (String category : clickMap.values()) {
            if (category.equals(lastCategory)) {
                repeatCount++;
                if (repeatCount >= 3) { // Annahme: 3 oder mehr Wiederholungen sind verdächtig
                    return true;
                }
            } else {
                lastCategory = category;
                repeatCount = 1;
            }
        }

        return false;
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
     *
     * This method scans through all users in the database and applies a filtering logic to identify
     * potential bots. A user is considered a potential bot if their click behavior shows a pattern of
     * repeatedly clicking on the same category in a short sequence, e.g., 'main, main, main...'.
     * The method uses a threshold of three consecutive clicks on the same category as a criterion for
     * suspicious behavior, which can be adjusted as per requirements.
     *
     * @return List<UniqueUser> containing users who are identified as potential bots based on the defined criteria.
     */
    public List<UniqueUser> getPossibleBots() {
        List<UniqueUser> users = uniqueUserRepo.findAll();
        return users.stream()
                .filter(this::isPotentialBot)
                .collect(Collectors.toList());
    }

}