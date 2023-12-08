package com.analysetool.services;
import com.analysetool.repositories.UniqueUserRepository;
import com.analysetool.modells.UniqueUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
