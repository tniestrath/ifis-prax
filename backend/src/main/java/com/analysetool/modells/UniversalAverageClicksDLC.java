package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "universal_average_clicks_dlc")
public class UniversalAverageClicksDLC {

    @Id
    @Column(name="uni_stat_id")
    private int uni_stat_id;

    @Column(name="article")
    private float article;

    @Column(name="news")
    private float news;

    @Column(name="blog")
    private float blog;

    @Column(name="podcast")
    private float podcast;

    @Column(name="whitepaper")
    private float whitepaper;

    @Column(name="ratgeber")
    private float ratgeber;

    @Column(name="main")
    private float main;

    @Column(name="footer")
    private float footer;

    @Column(name="amount_clicks")
    private int amount_clicks;

    @Column(name="amount_users")
    private int amount_users;

    public int getUni_stat_id() {
        return uni_stat_id;
    }

    public void setUni_stat_id(int uni_stat_id) {
        this.uni_stat_id = uni_stat_id;
    }

    public float getArticle() {
        return article;
    }

    public void setArticle(float article) {
        this.article = article;
    }

    public float getNews() {
        return news;
    }

    public void setNews(float news) {
        this.news = news;
    }

    public float getBlog() {
        return blog;
    }

    public void setBlog(float blog) {
        this.blog = blog;
    }

    public float getPodcast() {
        return podcast;
    }

    public void setPodcast(float podcast) {
        this.podcast = podcast;
    }

    public float getWhitepaper() {
        return whitepaper;
    }

    public void setWhitepaper(float whitepaper) {
        this.whitepaper = whitepaper;
    }

    public float getRatgeber() {
        return ratgeber;
    }

    public void setRatgeber(float ratgeber) {
        this.ratgeber = ratgeber;
    }

    public float getMain() {
        return main;
    }

    public void setMain(float main) {
        this.main = main;
    }

    public float getFooter() {
        return footer;
    }

    public void setFooter(float footer) {
        this.footer = footer;
    }

    public int getAmount_clicks() {
        return amount_clicks;
    }

    public void setAmount_clicks(int amount_clicks) {
        this.amount_clicks = amount_clicks;
    }

    public int getAmount_users() {
        return amount_users;
    }

    public void setAmount_users(int amount_users) {
        this.amount_users = amount_users;
    }
}
