package com.analysetool.modells;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iduser_stats;

    @Column(name = "user_id")
    private long userId;
    @Column(name = "profile_view")
    private long profileView;

    @Lob
    @Column(name = "views_per_hour", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Long> viewsPerHour = new HashMap<>();

    @Transient
    String viewsPerHourString = "{\"0\":0,\"1\":0,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0,\"13\":0,\"14\":0,\"15\":0,\"16\":0,\"17\":0,\"18\":0,\"19\":0,\"20\":0,\"21\":0,\"22\":0,\"23\":0}";


    public UserStats(long userId, long profileView) {
        this.userId = userId;
        this.profileView = profileView;
        this.viewsPerHour=setJson();
    }
    public UserStats(){}

    public Map<String,Long> setJson(){
        String temp ="";
        //temp = viewsPerDay.substring(1, viewsPerDay.length() - 1);
        temp = viewsPerHourString.substring(1, viewsPerHourString.length() - 1);
        // Teile den String an den Kommas auf, um die einzelnen Schlüssel-Wert-Paare zu erhalten
        String[] keyValuePairs = temp.split(",");

        // Erstelle eine HashMap, um die Schlüssel-Wert-Paare zu speichern
        HashMap<String, Long> map = new HashMap<>();

        // Iteriere über die einzelnen Schlüssel-Wert-Paare und füge sie der HashMap hinzu
        for (String pair : keyValuePairs) {
            // Teile den Schlüssel-Wert-Paar-String an den Doppelpunkten auf
            String[] entry = pair.split(":");

            // Entferne führende und abschließende Anführungszeichen von Schlüssel und Wert
            String key = entry[0].trim().replaceAll("\"", "");
            long value = Long.parseLong(entry[1].trim());

            // Füge das Schlüssel-Wert-Paar der HashMap hinzu
            map.put(key, value);
        }
        return (Map) map;
    }

    public int getIduser_stats() {
        return iduser_stats;
    }

    public void setIduser_stats(int iduser_stats) {
        this.iduser_stats = iduser_stats;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getProfileView() {
        return profileView;
    }

    public void setProfileView(long profileView) {
        this.profileView = profileView;
    }

    public Map<String, Long> getViewsPerHour() {
        return viewsPerHour;
    }

    public void setViewsPerHour(Map<String, Long> viewsPerHour) {
        this.viewsPerHour = viewsPerHour;
    }
}

