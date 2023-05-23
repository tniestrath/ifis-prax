package com.analysetool.modells;


import jakarta.persistence.*;
import org.bson.json.JsonObject;


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

    @Column(name = "relevance")
    private float relevance;

    @Column(name = "performance")
    private float performance;

    @Lob
    @Column(name = "views_last_year", columnDefinition = "JSON")
    private String viewsLastYear;

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
        return getId() == tagStat.getId() && getTagId() == tagStat.getTagId() && getViews() == tagStat.getViews() && getSearchSuccess() == tagStat.getSearchSuccess() && Float.compare(tagStat.getRelevance(), getRelevance()) == 0 && Float.compare(tagStat.getPerformance(), getPerformance()) == 0 && Objects.equals(getViewsLastYear(), tagStat.getViewsLastYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTagId(), getViews(), getSearchSuccess(), getRelevance(), getPerformance(), getViewsLastYear());
    }

    public void setSearchSuccess(int searchSuccess) {
        this.searchSuccess = searchSuccess;
    }

    public float getRelevance() {
        return relevance;
    }

    public void setRelevance(float relevance) {
        this.relevance = relevance;
    }

    public float getPerformance() {
        return performance;
    }

    public void setPerformance(float performance) {
        this.performance = performance;
    }

    public String getViewsLastYear() {
        return viewsLastYear;
    }

    public void setViewsLastYear(String viewsLastYear) {
        this.viewsLastYear = viewsLastYear;
    }

    @Override
    public String toString() {
        return "TagStat{" +
                "id=" + id +
                ", tagId=" + tagId +
                ", views=" + views +
                ", searchSuccess=" + searchSuccess +
                ", relevance=" + relevance +
                ", performance=" + performance +
                ", viewsLastYear='" + viewsLastYear + '\'' +
                '}';
    }

    public TagStat(int id, int tagId, long views, int searchSuccess, float relevance, float performance, String viewsLastYear) {
        this.id = id;
        this.tagId = tagId;
        this.views = views;
        this.searchSuccess = searchSuccess;
        this.relevance = relevance;
        this.performance = performance;
        this.viewsLastYear = viewsLastYear;
    }
    public TagStat(){}
    public TagStat(int id, int tagId, long views, int searchSuccess, float relevance, float performance) {
        this.id = id;
        this.tagId = tagId;
        this.views = views;
        this.searchSuccess = searchSuccess;
        this.relevance = relevance;
        this.performance = performance;
    }


    // Konstruktor, Getter und Setter (abgekürzt)
}
