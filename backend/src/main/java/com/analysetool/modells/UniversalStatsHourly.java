package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "universal_stats_hourly")
public class UniversalStatsHourly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uni_stat_id")
    private int uniStatId;

    @Column(name = "stunde")
    private int stunde;

    @Column(name = "besucher_anzahl")
    private Long besucherAnzahl;

    @Column(name = "anbieter_profile_anzahl")
    private Long anbieterProfileAnzahl;
    @Column(name = "anbieter_premium_anzahl")
    private long anbieterPremiumAnzahl;

    @Column(name = "anbieter_basicplus_anzahl")
    private long anbieterBasicPlusAnzahl;

    @Column(name = "anbieter_basic_anzahl")
    private long anbieterBasicAnzahl;

    @Column(name = "anbieter_premium_sponsoren_anzahl")
    private long anbieterPremiumSponsorenAnzahl;

    @Column(name = "anzahl_artikel")
    private Long anzahlArtikel;

    @Column(name = "anbieter_plus_anzahl")
    private Long anbieterPlusAnzahl;

    @Column(name = "anzahl_news")
    private Long anzahlNews;

    @Column(name = "anzahl_blog")
    private Long anzahlBlog;

    @Column(name = "anbieter_abolos_anzahl")
    private long anbieter_abolos_anzahl;

    @Column(name = "total_clicks")
    private Long totalClicks;

    @Column(name="internal_clicks")
    private int internalClicks;

    @Column(name = "server_errors")
    private int serverErrors;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStunde() {
        return stunde;
    }

    public void setStunde(int stunde) {
        this.stunde = stunde;
    }

    public Long getBesucherAnzahl() {
        return besucherAnzahl;
    }

    public void setBesucherAnzahl(Long besucherAnzahl) {
        this.besucherAnzahl = besucherAnzahl;
    }

    public Long getAnbieterProfileAnzahl() {
        return anbieterProfileAnzahl;
    }

    public void setAnbieterProfileAnzahl(Long anbieterProfileAnzahl) {
        this.anbieterProfileAnzahl = anbieterProfileAnzahl;
    }

    public long getAnbieterPremiumAnzahl() {
        return anbieterPremiumAnzahl;
    }

    public void setAnbieterPremiumAnzahl(long anbieterPremiumAnzahl) {
        this.anbieterPremiumAnzahl = anbieterPremiumAnzahl;
    }

    public long getAnbieterPremiumSponsorenAnzahl() {
        return anbieterPremiumSponsorenAnzahl;
    }

    public void setAnbieterPremiumSponsorenAnzahl(long anbieterPremiumSponsorenAnzahl) {
        this.anbieterPremiumSponsorenAnzahl = anbieterPremiumSponsorenAnzahl;
    }

    public Long getAnzahlArtikel() {
        return anzahlArtikel;
    }

    public void setAnzahlArtikel(Long anzahlArtikel) {
        this.anzahlArtikel = anzahlArtikel;
    }

    public Long getAnzahlNews() {
        return anzahlNews;
    }

    public void setAnzahlNews(Long anzahlNews) {
        this.anzahlNews = anzahlNews;
    }

    public Long getAnzahlBlog() {
        return anzahlBlog;
    }

    public void setAnzahlBlog(Long anzahlBlog) {
        this.anzahlBlog = anzahlBlog;
    }

    public long getAnbieter_abolos_anzahl() {
        return anbieter_abolos_anzahl;
    }

    public void setAnbieter_abolos_anzahl(long anbieter_abolos_anzahl) {
        this.anbieter_abolos_anzahl = anbieter_abolos_anzahl;
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public long getAnbieterPlusAnzahl() {
        return anbieterPlusAnzahl;
    }

    public void setAnbieterPlusAnzahl(long anbieterPlusAnzahl) {
        this.anbieterPlusAnzahl = anbieterPlusAnzahl;
    }

    public long getAnbieterBasicPlusAnzahl() {
        return anbieterBasicPlusAnzahl;
    }

    public void setAnbieterBasicPlusAnzahl(long anbieterBasicPlusAnzahl) {
        this.anbieterBasicPlusAnzahl = anbieterBasicPlusAnzahl;
    }

    public long getAnbieterBasicAnzahl() {
        return anbieterBasicAnzahl;
    }

    public void setAnbieterBasicAnzahl(long anbieterBasicAnzahl) {
        this.anbieterBasicAnzahl = anbieterBasicAnzahl;
    }

    public int getInternalClicks() {
        return internalClicks;
    }

    public void setInternalClicks(int internalClicks) {
        this.internalClicks = internalClicks;
    }

    public int getServerErrors() {
        return serverErrors;
    }

    public void setServerErrors(int serverErrors) {
        this.serverErrors = serverErrors;
    }

    public int getUniStatId() {
        return uniStatId;
    }

    public void setUniStatId(int uniStatId) {
        this.uniStatId = uniStatId;
    }
}


