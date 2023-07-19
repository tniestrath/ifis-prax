package com.analysetool.util;

import com.analysetool.Application;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public final class MapHelper {
    private static MapHelper INSTANCE;


    private MapHelper() {

    }

    public static MapHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MapHelper();
        }
        return INSTANCE;
    }

    public static void mergeMaps(Map<String, Map<String, Map<String, Long>>> map1, Map<String, Map<String, Map<String, Long>>> map2) {
        for (Map.Entry<String, Map<String, Map<String, Long>>> outerEntry : map2.entrySet()) {
            String outerKey = outerEntry.getKey();
            Map<String, Map<String, Long>> innerMap2 = outerEntry.getValue();
            if (map1.containsKey(outerKey)) {
                Map<String, Map<String, Long>> innerMap1 = map1.get(outerKey);
                mergeInnerMaps(innerMap1, innerMap2);
            } else {
                map1.put(outerKey, innerMap2);
            }
        }
    }

    public static void mergeInnerMaps(Map<String, Map<String, Long>> innerMap1, Map<String, Map<String, Long>> innerMap2) {
        for (Map.Entry<String, Map<String, Long>> innerEntry : innerMap2.entrySet()) {
            String innerKey = innerEntry.getKey();
            Map<String, Long> innermostMap2 = innerEntry.getValue();
            if (innerMap1.containsKey(innerKey)) {
                Map<String, Long> innermostMap1 = innerMap1.get(innerKey);
                mergeInnermostMaps(innermostMap1, innermostMap2);
            } else {
                innerMap1.put(innerKey, innermostMap2);
            }
        }
    }

    public static void mergeInnermostMaps(Map<String, Long> innermostMap1, Map<String, Long> innermostMap2) {
        for (Map.Entry<String, Long> entry : innermostMap2.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            innermostMap1.merge(key, value, Long::sum);
        }
    }
}
