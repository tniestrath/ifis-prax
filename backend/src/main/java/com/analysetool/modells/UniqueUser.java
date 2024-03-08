package com.analysetool.modells;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "unique_users_today")
public class UniqueUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "article")
    private String article;

    @Column(name = "blog")
    private String blog;

    @Column(name = "news")
    private String news;

    @Column(name = "whitepaper")
    private String whitepaper;

    @Column(name = "podcast")
    private String podcast;

    @Column(name = "ratgeber")
    private String ratgeber;

    @Column(name = "main")
    private String main;

    @Column(name = "ueber")
    private String ueber;

    @Column(name = "impressum")
    private String impressum;

    @Column(name = "preisliste")
    private String preisliste;

    @Column(name = "first_click")
    private LocalDateTime first_click;

    @Column(name="time_spent")
    private int time_spent;
    @Column(name = "partner")
    private String partner;

    @Column(name = "datenschutz")
    private String datenschutz;

    @Column(name = "newsletter")
    private String newsletter;

    @Column(name = "image")
    private String image;

    @Column(name = "agb")
    private String agb;

    @Column(name = "nonsense")
    private String nonsense;

    @Column(name="amount_of_clicks")
    private int amount_of_clicks;

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

    public LocalDateTime getFirst_click() {
        return first_click;
    }

    public void setFirst_click(LocalDateTime first_click) {
        this.first_click = first_click;
    }

    public int getTime_spent() {
        return time_spent;
    }

    public void setTime_spent(int time_spent) {
        this.time_spent = time_spent;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getWhitepaper() {
        return whitepaper;
    }

    public void setWhitepaper(String whitepaper) {
        this.whitepaper = whitepaper;
    }

    public String getPodcast() {
        return podcast;
    }

    public void setPodcast(String podcast) {
        this.podcast = podcast;
    }

    public String getRatgeber() {
        return ratgeber;
    }

    public void setRatgeber(String ratgeber) {
        this.ratgeber = ratgeber;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getUeber() {
        return ueber;
    }

    public void setUeber(String ueber) {
        this.ueber = ueber;
    }

    public String getImpressum() {
        return impressum;
    }

    public void setImpressum(String impressum) {
        this.impressum = impressum;
    }

    public String getPreisliste() {
        return preisliste;
    }

    public void setPreisliste(String preisliste) {
        this.preisliste = preisliste;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getDatenschutz() {
        return datenschutz;
    }

    public void setDatenschutz(String datenschutz) {
        this.datenschutz = datenschutz;
    }

    public String getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(String newsletter) {
        this.newsletter = newsletter;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAgb() {
        return agb;
    }

    public void setAgb(String agb) {
        this.agb = agb;
    }

    public String getNonsense() {
        return nonsense;
    }

    public void setNonsense(String nonsense) {
        this.nonsense = nonsense;
    }

    public int getAmount_of_clicks() {
        return amount_of_clicks;
    }

    public void setAmount_of_clicks(int amount_of_clicks) {
        this.amount_of_clicks = amount_of_clicks;
    }
}
