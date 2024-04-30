package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "cat_page_views")
@IdClass(CatPageId.class)
public class CatPageViews implements Serializable {

    @Id
    @Column(name="uni_id")
    private int uniId;

    @Id
    @Column(name="cat")
    private String cat;

    @Id
    @Column(name = "page")
    private int page;

    @Column(name="views")
    private int views;

    public int getUniId() {
        return uniId;
    }

    public void setUniId(int uniId) {
        this.uniId = uniId;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
