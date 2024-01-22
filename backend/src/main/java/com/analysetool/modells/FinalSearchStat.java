package com.analysetool.modells;

import jakarta.persistence.*;


@Entity
@Table(name = "final_search_stats")
public class FinalSearchStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uniId", nullable = false)
    private int uniId;

    @Column(name = "hour", nullable = false)
    private int hour;

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Column(name = "found_artikel_count", nullable = false)
    private int foundArtikelCount;

    @Column(name = "found_blog_count", nullable = false)
    private int foundBlogCount;

    @Column(name = "found_news_count", nullable = false)
    private int foundNewsCount;

    @Column(name = "found_whitepaper_count", nullable = false)
    private int foundWhitepaperCount;

    @Column(name = "found_ratgeber_count", nullable = false)
    private int foundRatgeberCount;

    @Column(name = "found_podcast_count", nullable = false)
    private int foundPodcastCount;

    @Column(name = "found_anbieter_count", nullable = false)
    private int foundAnbieterCount;

    @Column(name = "found_events_count", nullable = false)
    private int foundEventsCount;

    public FinalSearchStat() {}

    public FinalSearchStat(int uniId, int hour, String country, String state, String city, int foundArtikelCount, int foundBlogCount, int foundNewsCount, int foundWhitepaperCount, int foundRatgeberCount, int foundPodcastCount, int foundAnbieterCount, int foundEventsCount) {
        this.uniId = uniId;
        this.hour = hour;
        this.country = country;
        this.state = state;
        this.city = city;
        this.foundArtikelCount = foundArtikelCount;
        this.foundBlogCount = foundBlogCount;
        this.foundNewsCount = foundNewsCount;
        this.foundWhitepaperCount = foundWhitepaperCount;
        this.foundRatgeberCount = foundRatgeberCount;
        this.foundPodcastCount = foundPodcastCount;
        this.foundAnbieterCount = foundAnbieterCount;
        this.foundEventsCount = foundEventsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUniId() {
        return uniId;
    }

    public void setUniId(int uniId) {
        this.uniId = uniId;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getFoundArtikelCount() {
        return foundArtikelCount;
    }

    public void setFoundArtikelCount(int foundArtikelCount) {
        this.foundArtikelCount = foundArtikelCount;
    }

    public int getFoundBlogCount() {
        return foundBlogCount;
    }

    public void setFoundBlogCount(int foundBlogCount) {
        this.foundBlogCount = foundBlogCount;
    }

    public int getFoundNewsCount() {
        return foundNewsCount;
    }

    public void setFoundNewsCount(int foundNewsCount) {
        this.foundNewsCount = foundNewsCount;
    }

    public int getFoundWhitepaperCount() {
        return foundWhitepaperCount;
    }

    public void setFoundWhitepaperCount(int foundWhitepaperCount) {
        this.foundWhitepaperCount = foundWhitepaperCount;
    }

    public int getFoundRatgeberCount() {
        return foundRatgeberCount;
    }

    public void setFoundRatgeberCount(int foundRatgeberCount) {
        this.foundRatgeberCount = foundRatgeberCount;
    }

    public int getFoundPodcastCount() {
        return foundPodcastCount;
    }

    public void setFoundPodcastCount(int foundPodcastCount) {
        this.foundPodcastCount = foundPodcastCount;
    }

    public int getFoundAnbieterCount() {
        return foundAnbieterCount;
    }

    public void setFoundAnbieterCount(int foundAnbieterCount) {
        this.foundAnbieterCount = foundAnbieterCount;
    }

    public int getFoundEventsCount() {
        return foundEventsCount;
    }

    public void setFoundEventsCount(int foundEventsCount) {
        this.foundEventsCount = foundEventsCount;
    }

}
