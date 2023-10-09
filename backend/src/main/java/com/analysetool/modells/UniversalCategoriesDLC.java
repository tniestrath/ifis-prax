package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "universal_categoriesdlc")
public class UniversalCategoriesDLC {

    @Id
    @Column(name="id")
    private int id;

    @Column(name="besucher_global")
    private int besucherGlobal;

    @Column(name="besucher_article")
    private int besucherArticle;

    @Column(name="besucher_news")
    private int besucherNews;

    @Column(name="besucher_blog")
    private int besucherBlog;

    @Column(name="besucher_podcast")
    private int besucherPodcast;

    @Column(name="besucher_whitepaper")
    private int besucherWhitepaper;

    @Column(name="besucher_ratgeber")
    private int besucherRatgeber;

    @Column(name="views_global")
    private int viewsGlobal;

    @Column(name="views_article")
    private int viewsArticle;

    @Column(name="views_news")
    private int viewsNews;

    @Column(name="views_blog")
    private int viewsBlog;

    @Column(name="views_podcast")
    private int viewsPodcast;

    @Column(name="views_whitepaper")
    private int viewsWhitepaper;

    @Column(name="views_ratgeber")
    private int viewsRatgeber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBesucherGlobal() {
        return besucherGlobal;
    }

    public void setBesucherGlobal(int besucherGlobal) {
        this.besucherGlobal = besucherGlobal;
    }

    public int getBesucherNews() {
        return besucherNews;
    }

    public void setBesucherNews(int besucherNews) {
        this.besucherNews = besucherNews;
    }

    public int getBesucherBlog() {
        return besucherBlog;
    }

    public void setBesucherBlog(int besucherBlog) {
        this.besucherBlog = besucherBlog;
    }

    public int getBesucherPodcast() {
        return besucherPodcast;
    }

    public void setBesucherPodcast(int besucherPodcast) {
        this.besucherPodcast = besucherPodcast;
    }

    public int getBesucherWhitepaper() {
        return besucherWhitepaper;
    }

    public void setBesucherWhitepaper(int besucherWhitepaper) {
        this.besucherWhitepaper = besucherWhitepaper;
    }

    public int getBesucherRatgeber() {
        return besucherRatgeber;
    }

    public void setBesucherRatgeber(int besucherRatgeber) {
        this.besucherRatgeber = besucherRatgeber;
    }

    public int getViewsGlobal() {
        return viewsGlobal;
    }

    public void setViewsGlobal(int viewsGlobal) {
        this.viewsGlobal = viewsGlobal;
    }

    public int getViewsNews() {
        return viewsNews;
    }

    public void setViewsNews(int viewsNews) {
        this.viewsNews = viewsNews;
    }

    public int getViewsBlog() {
        return viewsBlog;
    }

    public void setViewsBlog(int viewsBlog) {
        this.viewsBlog = viewsBlog;
    }

    public int getViewsPodcast() {
        return viewsPodcast;
    }

    public void setViewsPodcast(int viewsPodcast) {
        this.viewsPodcast = viewsPodcast;
    }

    public int getViewsWhitepaper() {
        return viewsWhitepaper;
    }

    public void setViewsWhitepaper(int viewsWhitepaper) {
        this.viewsWhitepaper = viewsWhitepaper;
    }

    public int getViewsRatgeber() {
        return viewsRatgeber;
    }

    public void setViewsRatgeber(int viewsRatgeber) {
        this.viewsRatgeber = viewsRatgeber;
    }

    public int getViewsArticle() {
        return viewsArticle;
    }

    public void setViewsArticle(int viewsArticle) {
        this.viewsArticle = viewsArticle;
    }

    public int getBesucherArticle() {
        return besucherArticle;
    }

    public void setBesucherArticle(int besucherArticle) {
        this.besucherArticle = besucherArticle;
    }
}


