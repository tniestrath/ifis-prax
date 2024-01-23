package com.analysetool.modells;


import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "temporary_search_stats")
public class TemporarySearchStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "search_query", nullable = false)
    private String searchQuery;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "search_ip", nullable = false)
    private String searchIp;

    @Column(name = "found_artikel_count")
    private int foundArtikelCount;

    @Column(name = "found_blog_count")
    private int foundBlogCount;

    @Column(name = "found_news_count")
    private int foundNewsCount;

    @Column(name = "found_whitepaper_count")
    private int foundWhitepaperCount;

    @Column(name = "found_ratgeber_count")
    private int foundRatgeberCount;

    @Column(name = "found_podcast_count")
    private int foundPodcastCount;

    @Column(name = "found_anbieter_count")
    private int foundAnbieterCount;

    @Column(name = "found_events_count")
    private int foundEventsCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSearchIp() {
        return searchIp;
    }

    public void setSearchIp(String searchIp) {
        this.searchIp = searchIp;
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