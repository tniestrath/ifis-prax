package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "anbieter_failed_search_buffer")
public class AnbieterFailedSearchBuffer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="search")
    private String search;

    @Column(name="city")
    private String city;

    @Column(name="plz")
    private int plz;

    @Column(name="umkreis")
    private int umkreis;

    @Column(name="count")
    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPlz() {
        return plz;
    }

    public void setPlz(int plz) {
        this.plz = plz;
    }

    public int getUmkreis() {
        return umkreis;
    }

    public void setUmkreis(int umkreis) {
        this.umkreis = umkreis;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
