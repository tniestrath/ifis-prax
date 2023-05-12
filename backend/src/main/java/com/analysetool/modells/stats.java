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
    public stats(){}
    public stats(Long id, Long artId, Float searchSuccessRate, Float articleReferringRate, Long clicks) {
        this.id = id;
        this.artId = artId;
        this.searchSuccessRate = searchSuccessRate;
        this.articleReferringRate = articleReferringRate;
        this.clicks = clicks;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof stats stats)) return false;
        return Objects.equals(getId(), stats.getId()) && Objects.equals(getArtId(), stats.getArtId()) && Objects.equals(getSearchSuccessRate(), stats.getSearchSuccessRate()) && Objects.equals(getArticleReferringRate(), stats.getArticleReferringRate()) && Objects.equals(getClicks(), stats.getClicks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getArtId(), getSearchSuccessRate(), getArticleReferringRate(), getClicks());
    }

    @Override
    public String toString() {
        return "stats{" +
                "id=" + id +
                ", artId=" + artId +
                ", searchSuccessRate=" + searchSuccessRate +
                ", articleReferringRate=" + articleReferringRate +
                ", clicks=" + clicks +
                '}';
    }
}