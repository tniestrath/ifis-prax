package com.analysetool.modells;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "event_search")
public class EventSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "veranstaltung_name", nullable = false)
    private String veranstaltungName;

    @Column(name = "veranstaltung_start_datum", nullable = false)
    private Date veranstaltungStartDatum;

    @Column(name = "veranstaltung_end_datum", nullable = false)
    private Date veranstaltungEndDatum;

    @Column(name = "kategorien", nullable = false)
    private String kategorien;

    @Column(name = "veranstaltung_plz", nullable = false)
    private String veranstaltungPlz;

    @Column(name = "veranstaltung_ort", nullable = false)
    private String veranstaltungOrt;

    @Column(name = "umkreis", nullable = false)
    private int umkreis;

    @Column(name = "bundesland", nullable = false)
    private String bundesland;

    @Column(name = "result_count", nullable = false)
    private int resultCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVeranstaltungName() {
        return veranstaltungName;
    }

    public void setVeranstaltungName(String veranstaltungName) {
        this.veranstaltungName = veranstaltungName;
    }

    public Date getVeranstaltungStartDatum() {
        return veranstaltungStartDatum;
    }

    public void setVeranstaltungStartDatum(Date veranstaltungStartDatum) {
        this.veranstaltungStartDatum = veranstaltungStartDatum;
    }

    public Date getVeranstaltungEndDatum() {
        return veranstaltungEndDatum;
    }

    public void setVeranstaltungEndDatum(Date veranstaltungEndDatum) {
        this.veranstaltungEndDatum = veranstaltungEndDatum;
    }

    public String getKategorien() {
        return kategorien;
    }

    public void setKategorien(String kategorien) {
        this.kategorien = kategorien;
    }

    public String getVeranstaltungPlz() {
        return veranstaltungPlz;
    }

    public void setVeranstaltungPlz(String veranstaltungPlz) {
        this.veranstaltungPlz = veranstaltungPlz;
    }

    public String getVeranstaltungOrt() {
        return veranstaltungOrt;
    }

    public void setVeranstaltungOrt(String veranstaltungOrt) {
        this.veranstaltungOrt = veranstaltungOrt;
    }

    public int getUmkreis() {
        return umkreis;
    }

    public void setUmkreis(int umkreis) {
        this.umkreis = umkreis;
    }

    public String getBundesland() {
        return bundesland;
    }

    public void setBundesland(String bundesland) {
        this.bundesland = bundesland;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
