package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "clicks_by_bundesland_city_dlc")
public class ClicksByBundeslandCitiesDLC implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name= "uni_id")
    private int uni_id;
    @Column(name="bundesland")
    private String bundesland;

    @Column(name = "city")
    private String city;

    @Column(name ="clicks")
    private int clicks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUni_id() {
        return uni_id;
    }

    public void setUni_id(int uni_id) {
        this.uni_id = uni_id;
    }

    public String getBundesland() {
        return bundesland;
    }

    public void setBundesland(String bundesland) {
        this.bundesland = bundesland;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
}
