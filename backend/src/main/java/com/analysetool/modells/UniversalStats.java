package com.analysetool.modells;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "universal_stats")
public class UniversalStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "besucher_anzahl")
    private Long besucherAnzahl;

    @Column(name = "anbieter_profile_anzahl")
    private Long anbieterProfileAnzahl;

    @Column(name = "anbieter_basic_anzahl")
    private long anbieterBasicAnzahl;

    @Column(name = "anbieter_plus_anzahl")
    private long anbieterPlusAnzahl;

    @Column(name = "anbieter_basicplus_anzahl")
    private long anbieterBasicPlusAnzahl;

    @Column(name = "anbieter_premium_anzahl")
    private long anbieterPremiumAnzahl;

    @Column(name = "anbieter_premium_sponsoren_anzahl")
    private long anbieterPremiumSponsorenAnzahl;

    @Column(name = "anzahl_artikel")
    private Long anzahlArtikel;

    @Column(name = "anzahl_news")
    private Long anzahlNews;

    @Column(name = "anzahl_blog")
    private Long anzahlBlog;

    @Column(name = "anzahl_whitepaper")
    private Long anzahlWhitepaper;

    @Column(name = "anzahl_podcast")
    private Long anzahlPodcast;

    @Column(name="anzahl_video")
    private long anzahlVideo;

    @Column(name = "datum")
    private Date datum;

    @Column(name = "anbieter_abolos_anzahl")
    private long anbieter_abolos_anzahl;

    @Column(name = "umsatz")
    private long umsatz;

    @Column(name="sensible_clicks")
    private Long sensibleClicks;

    @Column(name = "total_clicks")
    private Long totalClicks;

    @Column(name="internal_clicks")
    private int internalClicks;

    @Column(name= "server_errors")
    private int serverErrors;

    public UniversalStats() {
    }

    public UniversalStats(Date datum) {
        this.datum = datum;
    }

    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public long getAnbieterBasicAnzahl() {
        return anbieterBasicAnzahl;
    }

    public void setAnbieterBasicAnzahl(long anbieterBasicAnzahl) {
        this.anbieterBasicAnzahl = anbieterBasicAnzahl;
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

    public long getAnbieterPremiumAnzahl() {
        return anbieterPremiumAnzahl;
    }

    public void setAnbieterPremiumAnzahl(long anbieterPremiumAnzahl) {
        this.anbieterPremiumAnzahl = anbieterPremiumAnzahl;
    }

    public long getUmsatz() {
        return umsatz;
    }

    public void setUmsatz(long umsatz) {
        this.umsatz = umsatz;
    }

    public long getAnbieterPremiumSponsorenAnzahl() {
        return anbieterPremiumSponsorenAnzahl;
    }

    public void setAnbieterPremiumSponsorenAnzahl(long anbieterPremiumSponsorenAnzahl) {
        this.anbieterPremiumSponsorenAnzahl = anbieterPremiumSponsorenAnzahl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Long getAnzahlWhitepaper() {
        return anzahlWhitepaper;
    }

    public void setAnzahlWhitepaper(Long anzahlWhitepaper) {
        this.anzahlWhitepaper = anzahlWhitepaper;
    }

    public Long getAnzahlPodcast() {
        return anzahlPodcast;
    }

    public void setAnzahlPodcast(Long anzahlPodcast) {
        this.anzahlPodcast = anzahlPodcast;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniversalStats that)) return false;
        return getId() == that.getId() && Objects.equals(getBesucherAnzahl(), that.getBesucherAnzahl()) && Objects.equals(getAnbieterProfileAnzahl(), that.getAnbieterProfileAnzahl()) && Objects.equals(getAnzahlArtikel(), that.getAnzahlArtikel()) && Objects.equals(getAnzahlNews(), that.getAnzahlNews()) && Objects.equals(getAnzahlBlog(), that.getAnzahlBlog()) && Objects.equals(getDatum(), that.getDatum());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBesucherAnzahl(), getAnbieterProfileAnzahl(), getAnzahlArtikel(), getAnzahlNews(), getAnzahlBlog(), getDatum());
    }

    @Override
    public String toString() {
        return "universalStats{" +
                "id=" + id +
                ", besucherAnzahl=" + besucherAnzahl +
                ", anbieterProfileAnzahl=" + anbieterProfileAnzahl +
                ", anzahlArtikel=" + anzahlArtikel +
                ", anzahlNews=" + anzahlNews +
                ", anzahlBlog=" + anzahlBlog +
                ", datum=" + datum +
                '}';
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public long getAnbieter_abolos_anzahl() {
        return anbieter_abolos_anzahl;
    }

    public void setAnbieter_abolos_anzahl(long anbieter_abolos_anzahl) {
        this.anbieter_abolos_anzahl = anbieter_abolos_anzahl;
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

    public Long getSensibleClicks() {
        return sensibleClicks;
    }

    public void setSensibleClicks(Long sensibleClicks) {
        this.sensibleClicks = sensibleClicks;
    }

    public long getAnzahlVideo() {
        return anzahlVideo;
    }

    public void setAnzahlVideo(long anzahlVideo) {
        this.anzahlVideo = anzahlVideo;
    }
}

