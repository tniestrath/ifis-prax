package com.analysetool.modells;


import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tag_stat")
public class TagStat {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tag_id")
    private int tagId;

    @Column(name = "views")
    private long views;

    @Column(name = "search_success")
    private int searchSuccess;

    @Column(name = "uni_id")
    private int uniId;

    @Column(name = "hour")
    private int hour;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public int getSearchSuccess() {
        return searchSuccess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagStat tagStat)) return false;
        return getId() == tagStat.getId() && getTagId() == tagStat.getTagId() && getViews() == tagStat.getViews() && getSearchSuccess() == tagStat.getSearchSuccess();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTagId(), getViews(), getSearchSuccess());
    }

    public void setSearchSuccess(int searchSuccess) {
        this.searchSuccess = searchSuccess;
    }

    public TagStat(){}
    public TagStat( int tagId, long views, int searchSuccess, float relevance) {
        this.tagId = tagId;
        this.views = views;
        this.searchSuccess = searchSuccess;
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

// Konstruktor, Getter und Setter (abgekürzt)
}
