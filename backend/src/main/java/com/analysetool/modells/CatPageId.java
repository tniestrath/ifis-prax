package com.analysetool.modells;

import java.io.Serializable;
import java.util.Objects;

public class CatPageId implements Serializable {

    private int uniId;

    private String cat;

    private int page;

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

    public CatPageId(int uniId, String cat, int page) {
        this.uniId = uniId;
        this.cat = cat;
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatPageId catPageId = (CatPageId) o;
        return getUniId() == catPageId.getUniId() && getPage() == catPageId.getPage() && Objects.equals(getCat(), catPageId.getCat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniId(), getCat(), getPage());
    }
}
