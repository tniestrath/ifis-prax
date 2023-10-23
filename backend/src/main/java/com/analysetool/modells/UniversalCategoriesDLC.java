package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "universal_categoriesdlc")
public class UniversalCategoriesDLC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="uni_stat_id")
    private int uniStatId;

    @Column(name="stunde")
    private int stunde;

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

    @Column(name="besucher_main")
    private int besucherMain;

    @Column(name="besucher_ueber")
    private int besucherUeber;

    @Column(name="besucher_impressum")
    private int besucherImpressum;

    @Column(name="besucher_preisliste")
    private int besucherPreisliste;

    @Column(name="besucher_partner")
    private int besucherPartner;

    @Column(name="besucher_datenschutz")
    private int besucherDatenschutz;

    @Column(name="besucher_newsletter")
    private int besucherNewsletter;

    @Column(name="besucher_image")
    private int besucherImage;

    @Column(name="besucher_agbs")
    private int besucherAGBS;

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

    @Column(name="views_main")
    private int viewsMain;

    @Column(name="views_ueber")
    private int viewsUeber;

    @Column(name="views_impressum")
    private int viewsImpressum;

    @Column(name="views_preisliste")
    private int viewsPreisliste;

    @Column(name="views_partner")
    private int viewsPartner;

    @Column(name="views_agbs")
    private int viewsAGBS;

    public int getBesucherMain() {
        return besucherMain;
    }

    public void setBesucherMain(int besucherMain) {
        this.besucherMain = besucherMain;
    }

    public int getBesucherUeber() {
        return besucherUeber;
    }

    public void setBesucherUeber(int besucherUeber) {
        this.besucherUeber = besucherUeber;
    }

    public int getBesucherImpressum() {
        return besucherImpressum;
    }

    public void setBesucherImpressum(int besucherImpressum) {
        this.besucherImpressum = besucherImpressum;
    }

    public int getBesucherPreisliste() {
        return besucherPreisliste;
    }

    public void setBesucherPreisliste(int besucherPreisliste) {
        this.besucherPreisliste = besucherPreisliste;
    }

    public int getBesucherPartner() {
        return besucherPartner;
    }

    public void setBesucherPartner(int besucherPartner) {
        this.besucherPartner = besucherPartner;
    }

    public int getBesucherDatenschutz() {
        return besucherDatenschutz;
    }

    public void setBesucherDatenschutz(int besucherDatenschutz) {
        this.besucherDatenschutz = besucherDatenschutz;
    }

    public int getBesucherNewsletter() {
        return besucherNewsletter;
    }

    public void setBesucherNewsletter(int besucherNewsletter) {
        this.besucherNewsletter = besucherNewsletter;
    }

    public int getBesucherImage() {
        return besucherImage;
    }

    public void setBesucherImage(int besucherImage) {
        this.besucherImage = besucherImage;
    }

    public int getViewsMain() {
        return viewsMain;
    }

    public void setViewsMain(int viewsMain) {
        this.viewsMain = viewsMain;
    }

    public int getViewsUeber() {
        return viewsUeber;
    }

    public void setViewsUeber(int viewsUeber) {
        this.viewsUeber = viewsUeber;
    }

    public int getViewsImpressum() {
        return viewsImpressum;
    }

    public void setViewsImpressum(int viewsImpressum) {
        this.viewsImpressum = viewsImpressum;
    }

    public int getViewsPreisliste() {
        return viewsPreisliste;
    }

    public void setViewsPreisliste(int viewsPreisliste) {
        this.viewsPreisliste = viewsPreisliste;
    }

    public int getViewsPartner() {
        return viewsPartner;
    }

    public void setViewsPartner(int viewsPartner) {
        this.viewsPartner = viewsPartner;
    }

    public int getViewsDatenschutz() {
        return viewsDatenschutz;
    }

    public void setViewsDatenschutz(int viewsDatenschutz) {
        this.viewsDatenschutz = viewsDatenschutz;
    }

    public int getViewsNewsletter() {
        return viewsNewsletter;
    }

    public void setViewsNewsletter(int viewsNewsletter) {
        this.viewsNewsletter = viewsNewsletter;
    }

    public int getViewsImage() {
        return viewsImage;
    }

    public void setViewsImage(int viewsImage) {
        this.viewsImage = viewsImage;
    }

    @Column(name="views_datenschutz")
    private int viewsDatenschutz;

    @Column(name="views_newsletter")
    private int viewsNewsletter;

    @Column(name="views_image")
    private int viewsImage;

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

    public int getUniStatId() {
        return uniStatId;
    }

    public void setUniStatId(int uniStatId) {
        this.uniStatId = uniStatId;
    }

    public int getStunde() {
        return stunde;
    }

    public void setStunde(int stunde) {
        this.stunde = stunde;
    }

    public int getBesucherAGBS() {
        return besucherAGBS;
    }

    public void setBesucherAGBS(int besucherAGBS) {
        this.besucherAGBS = besucherAGBS;
    }

    public int getViewsAGBS() {
        return viewsAGBS;
    }

    public void setViewsAGBS(int viewsAGBS) {
        this.viewsAGBS = viewsAGBS;
    }
}


