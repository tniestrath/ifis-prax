package com.analysetool.modells;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "universal_stats")
public class universalStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "besucher_anzahl")
    private Long besucherAnzahl;

    @Column(name = "anbieter_profile_anzahl")
    private Long anbieterProfileAnzahl;

    @Column(name = "anzahl_artikel")
    private Long anzahlArtikel;

    @Column(name = "anzahl_news")
    private Long anzahlNews;

    @Column(name = "anzahl_blog")
    private Long anzahlBlog;

    @Column(name = "datum")
    private Date datum;

    public universalStats() {
    }

    public universalStats(Long besucherAnzahl, Long anbieterProfileAnzahl, Long anzahlArtikel, Long anzahlNews, Long anzahlBlog, Date datum) {
        this.besucherAnzahl = besucherAnzahl;
        this.anbieterProfileAnzahl = anbieterProfileAnzahl;
        this.anzahlArtikel = anzahlArtikel;
        this.anzahlNews = anzahlNews;
        this.anzahlBlog = anzahlBlog;
        this.datum = datum;
    }

    public universalStats(Date datum) {
        this.datum = datum;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof universalStats that)) return false;
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
}

