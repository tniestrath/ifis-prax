package com.analysetool.util;

import java.util.HashMap;
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

    /**
     *
     * @param map1 this map will be merged into.
     * @param map2 this map will be merged from.
     */
    public static void mergeLocationMaps(Map<String, Map<String, Map<String, Long>>> map1, Map<String, Map<String, Map<String, Long>>> map2) {
        if(map2 == null) {
            return;
        }
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


    /**
     *
     * @param map1 will be merged into.
     * @param map2 will be merged from.
     */
    public static void mergeTimeMaps(Map<String, Long> map1, Map<String, Long> map2) {

        for (Map.Entry<String, Long> entry : map2.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            map1.merge(key, value, Long::sum);
        }
    }

    /**
     *
     * @param viewsByLocation
     * @return
     */
    public static Map<String, Map<String, Map<String, Long>>> initializeViewsByLocation(Map<String, Map<String, Map<String, Long>>> viewsByLocation) {
        String[] germanStates = {"HH", "HB", "BE", "MV", "BB", "SN", "ST", "BY", "SL", "RP", "SH", "TH", "NB", "HE", "BW", "NW"};
        String[] otherCountries = {"NL", "BG", "SW", "AT", "LU"};

        Map<String, Long> zeroMap = new HashMap<>();
        zeroMap.put("gesamt", 0L);

        for (String state : germanStates) {
            viewsByLocation.computeIfAbsent("DE", k -> new HashMap<>()).put(state, new HashMap<>(zeroMap));
        }

        for (String country : otherCountries) {
            viewsByLocation.computeIfAbsent(country, k -> new HashMap<>()).put(country, new HashMap<>(zeroMap));
        }

        return viewsByLocation;
    }


///////////////////////////////////////////////////////////////////////////////////////////////////
    //Only private methods used internally from this point forward.
///////////////////////////////////////////////////////////////////////////////////////////////////

    private static void mergeInnerMaps(Map<String, Map<String, Long>> innerMap1, Map<String, Map<String, Long>> innerMap2) {
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

    private static void mergeInnermostMaps(Map<String, Long> innermostMap1, Map<String, Long> innermostMap2) {
        for (Map.Entry<String, Long> entry : innermostMap2.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            innermostMap1.merge(key, value, Long::sum);
        }
    }



}
