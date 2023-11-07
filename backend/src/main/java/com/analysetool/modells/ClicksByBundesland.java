package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "clicks_by_bundesland")
public class ClicksByBundesland implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bundesland")
    private String city_name;

    @Column(name = "uni_stat_id")
    private int uniStatId;

    @Column(name = "clicks")
    private int clicks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getUmkreis() {
        return umkreis;
    }

    public void setUmkreis(int umkreis) {
        this.umkreis = umkreis;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public int getUniStatId() {
        return uniStatId;
    }

    public void setUniStatId(int uniStatId) {
        this.uniStatId = uniStatId;
    }
}
