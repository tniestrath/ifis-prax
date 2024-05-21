package com.analysetool.modells;


import jakarta.persistence.*;

@Entity
@Table(name = "forum_search")
public class ForumSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long searchId;

    @Column(name = "suchbegriff")
    private String suchbegriff;

    @Column(name = "suchtyp")
    private String suchtyp;

    @Column(name = "zeitraum_in_tagen")
    private Integer zeitraumInTagen;

    @Column(name = "sortieren_nach")
    private String sortierenNach;

    @Column(name = "reihenfolge")
    private String reihenfolge;

    @Column(name = "seitenzahl")
    private Integer seitenzahl;

    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name = "stunde")
    private Integer stunde;

    @Column(name = "land")
    private String land;

    @Column(name = "region")
    private String region;

    @Column(name = "stadt")
    private String stadt;

    public Long getSearchId() {
        return searchId;
    }

    public void setSearchId(Long searchId) {
        this.searchId = searchId;
    }

    public String getSuchbegriff() {
        return suchbegriff;
    }

    public void setSuchbegriff(String suchbegriff) {
        this.suchbegriff = suchbegriff;
    }

    public String getSuchtyp() {
        return suchtyp;
    }

    public void setSuchtyp(String suchtyp) {
        this.suchtyp = suchtyp;
    }

    public int getZeitraumInTagen() {
        return zeitraumInTagen;
    }

    public void setZeitraumInTagen(int zeitraumInTagen) {
        this.zeitraumInTagen = zeitraumInTagen;
    }

    public String getSortierenNach() {
        return sortierenNach;
    }

    public void setSortierenNach(String sortierenNach) {
        this.sortierenNach = sortierenNach;
    }

    public String getReihenfolge() {
        return reihenfolge;
    }

    public void setReihenfolge(String reihenfolge) {
        this.reihenfolge = reihenfolge;
    }

    public int getSeitenzahl() {
        return seitenzahl;
    }

    public void setSeitenzahl(int seitenzahl) {
        this.seitenzahl = seitenzahl;
    }

    public int getUniId() {
        return uniId;
    }

    public void setUniId(int uniId) {
        this.uniId = uniId;
    }

    public Integer getStunde() {
        return stunde;
    }

    public void setStunde(Integer stunde) {
        this.stunde = stunde;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStadt() {
        return stadt;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }

    public ForumSearch(String suchbegriff, String suchtyp, int zeitraumInTagen, String sortierenNach, String reihenfolge, int seitenzahl, int uniId, Integer stunde, String land, String region, String stadt) {
        this.suchbegriff = suchbegriff;
        this.suchtyp = suchtyp;
        this.zeitraumInTagen = zeitraumInTagen;
        this.sortierenNach = sortierenNach;
        this.reihenfolge = reihenfolge;
        this.seitenzahl = seitenzahl;
        this.uniId = uniId;
        this.stunde = stunde;
        this.land = land;
        this.region = region;
        this.stadt = stadt;
    }

    public ForumSearch() {}
}