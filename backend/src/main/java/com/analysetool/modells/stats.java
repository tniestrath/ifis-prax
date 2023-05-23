package com.analysetool.modells;

import jakarta.persistence.*;

import java.util.Objects;


@Entity
@Table(name = "stats")
public class stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "art_id")
    private Long artId;

    @Column(name = "search_success_rate")
    private Float searchSuccessRate;

    @Column(name = "article_referring_rate")
    private Float articleReferringRate;

    @Column(name = "clicks")
    private Long clicks;
    // Constructor, getters, and setters
    @Column(name="search_succes")
    private long searchSuccess;

    @Column(name="refferings")
    private long refferings;

    @Column(name="performance")
    private float performance;

    public stats(){}

    public long getSearchSucces() {
        return searchSuccess;
    }

    public void setSearchSucces(long searchSuccess) {
        this.searchSuccess = searchSuccess;
    }

    public long getReferrings() {
        return refferings;
    }

    public void setReferrings(long referrings) {
        this.refferings = referrings;
    }

    public stats( Long artId, Float searchSuccessRate, Float articleReferringRate, long clicks, long searchSuccess, long refferings,float performance) {
        this.id = id;
        this.artId = artId;
        this.searchSuccessRate = searchSuccessRate;
        this.articleReferringRate = articleReferringRate;
        this.clicks = clicks;
        this.refferings=refferings;
        this.searchSuccess=searchSuccess;
        this.performance = performance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtId() {
        return artId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof stats stats)) return false;
        return getSearchSuccess() == stats.getSearchSuccess() && getReferrings() == stats.getReferrings() && Float.compare(stats.getPerformance(), getPerformance()) == 0 && Objects.equals(getId(), stats.getId()) && Objects.equals(getArtId(), stats.getArtId()) && Objects.equals(getSearchSuccessRate(), stats.getSearchSuccessRate()) && Objects.equals(getArticleReferringRate(), stats.getArticleReferringRate()) && Objects.equals(getClicks(), stats.getClicks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getArtId(), getSearchSuccessRate(), getArticleReferringRate(), getClicks(), getSearchSuccess(), getReferrings(), getPerformance());
    }

    public long getSearchSuccess() {
        return searchSuccess;
    }

    public void setSearchSuccess(long searchSuccess) {
        this.searchSuccess = searchSuccess;
    }

    public float getPerformance() {
        return performance;
    }

    public void setPerformance(float performance) {
        this.performance = performance;
    }

    public void setArtId(Long artId) {
        this.artId = artId;
    }

    public Float getSearchSuccessRate() {
        return searchSuccessRate;
    }

    public void setSearchSuccessRate(Float searchSuccessRate) {
        this.searchSuccessRate = searchSuccessRate;
    }

    public Float getArticleReferringRate() {
        return articleReferringRate;
    }

    public void setArticleReferringRate(Float articleReferringRate) {
        this.articleReferringRate = articleReferringRate;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    @Override
    public String toString() {
        return "stats{" +
                "id=" + id +
                ", artId=" + artId +
                ", searchSuccessRate=" + searchSuccessRate +
                ", articleReferringRate=" + articleReferringRate +
                ", clicks=" + clicks +
                ", searchSuccess=" + searchSuccess +
                ", referrings=" + refferings +
                ", performance=" + performance +
                '}';
    }
}