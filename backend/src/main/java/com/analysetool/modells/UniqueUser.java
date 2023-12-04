package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "unique_users_today")
public class UniqueUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "first_category")
    private String category;

    @Column(name = "global")
    private int global;

    @Column(name = "article")
    private int article;

    @Column(name = "blog")
    private int blog;

    @Column(name = "news")
    private int news;

    @Column(name = "whitepaper")
    private int whitepaper;

    @Column(name = "podcast")
    private int podcast;

    @Column(name = "ratgeber")
    private int ratgeber;

    @Column(name = "main")
    private int main;

    @Column(name = "ueber")
    private int ueber;

    @Column(name = "impressum")
    private int impressum;

    @Column(name = "preisliste")
    private int preisliste;

    public int getGlobal() {
        return global;
    }

    public void setGlobal(int global) {
        this.global = global;
    }

    public int getArticle() {
        return article;
    }

    public void setArticle(int article) {
        this.article = article;
    }

    public int getBlog() {
        return blog;
    }

    public void setBlog(int blog) {
        this.blog = blog;
    }

    public int getNews() {
        return news;
    }

    public void setNews(int news) {
        this.news = news;
    }

    public int getWhitepaper() {
        return whitepaper;
    }

    public void setWhitepaper(int whitepaper) {
        this.whitepaper = whitepaper;
    }

    public int getPodcast() {
        return podcast;
    }

    public void setPodcast(int podcast) {
        this.podcast = podcast;
    }

    public int getRatgeber() {
        return ratgeber;
    }

    public void setRatgeber(int ratgeber) {
        this.ratgeber = ratgeber;
    }

    public int getMain() {
        return main;
    }

    public void setMain(int main) {
        this.main = main;
    }

    public int getUeber() {
        return ueber;
    }

    public void setUeber(int ueber) {
        this.ueber = ueber;
    }

    public int getImpressum() {
        return impressum;
    }

    public void setImpressum(int impressum) {
        this.impressum = impressum;
    }

    public int getPreisliste() {
        return preisliste;
    }

    public void setPreisliste(int preisliste) {
        this.preisliste = preisliste;
    }

    public int getPartner() {
        return partner;
    }

    public void setPartner(int partner) {
        this.partner = partner;
    }

    public int getDatenschutz() {
        return datenschutz;
    }

    public void setDatenschutz(int datenschutz) {
        this.datenschutz = datenschutz;
    }

    public int getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(int newsletter) {
        this.newsletter = newsletter;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getAgb() {
        return agb;
    }

    public void setAgb(int agb) {
        this.agb = agb;
    }

    @Column(name = "partner")
    private int partner;

    @Column(name = "datenschutz")
    private int datenschutz;

    @Column(name = "newsletter")
    private int newsletter;

    @Column(name = "image")
    private int image;

    @Column(name = "agb")
    private int agb;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
