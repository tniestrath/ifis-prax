package com.analysetool.modells;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "search_stats")
public class SearchStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchId;

    @Column(length = 250)
    private String ipHashed;

    @Column
    private Boolean searchSuccessFlag;


    @Column(columnDefinition = "TEXT")
    private String searchString;

    @Column
    private LocalDateTime searchTime;

    @Column(length = 45)
    private String location;

    @Column(length = 100)
    private String clickedPost;

    @Column
    private LocalDateTime search_success_time;

    @Column
    private LocalTime dwell_time;

    public SearchStats(String ipHashed, Boolean searchSuccessFlag, String searchString, LocalDateTime searchTime, String location, String clickedPost) {
        this.ipHashed = ipHashed;
        this.searchSuccessFlag = searchSuccessFlag;
        this.searchString = searchString;
        this.searchTime = searchTime;
        this.location = location;
        this.clickedPost = clickedPost;
    }

    public SearchStats(String ipHashed, String searchString, LocalDateTime searchTime, String location) {
        this.ipHashed = ipHashed;
        this.searchString = searchString;
        this.searchTime = searchTime;
        this.location = location;
        this.searchSuccessFlag=false;
    }

    public SearchStats(){}

    public Long getSearchId() {
        return searchId;
    }

    public void setSearchId(Long searchId) {
        this.searchId = searchId;
    }

    public String getIpHashed() {
        return ipHashed;
    }

    public void setIpHashed(String ipHashed) {
        this.ipHashed = ipHashed;
    }

    public Boolean getSearchSuccessFlag() {
        return searchSuccessFlag;
    }

    public void setSearchSuccessFlag(Boolean searchSuccessFlag) {
        this.searchSuccessFlag = searchSuccessFlag;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public LocalDateTime getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(LocalDateTime searchTime) {
        this.searchTime = searchTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getClickedPost() {
        return clickedPost;
    }

    public void setClickedPost(String clickedPost) {
        this.clickedPost = clickedPost;
    }

    public LocalDateTime getSearch_success_time() {
        return search_success_time;
    }

    public void setSearch_success_time(LocalDateTime search_success_time) {
        this.search_success_time = search_success_time;
    }

    public LocalTime getDwell_time() {
        return dwell_time;
    }

    public void setDwell_time(LocalTime dwell_time) {
        this.dwell_time = dwell_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchStats that)) return false;
        return Objects.equals(getSearchId(), that.getSearchId()) && Objects.equals(getIpHashed(), that.getIpHashed()) && Objects.equals(getSearchSuccessFlag(), that.getSearchSuccessFlag()) && Objects.equals(getSearchString(), that.getSearchString()) && Objects.equals(getSearchTime(), that.getSearchTime()) && Objects.equals(getLocation(), that.getLocation()) && Objects.equals(getClickedPost(), that.getClickedPost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSearchId(), getIpHashed(), getSearchSuccessFlag(), getSearchString(), getSearchTime(), getLocation(), getClickedPost());
    }

    @Override
    public String toString() {
        return "SearchStats{" +
                "searchId=" + searchId +
                ", ipHashed='" + ipHashed + '\'' +
                ", searchSuccessFlag=" + searchSuccessFlag +
                ", searchString='" + searchString + '\'' +
                ", searchTime=" + searchTime +
                ", location='" + location + '\'' +
                ", clickedPost='" + clickedPost + '\'' +
                '}';
    }
}
