package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "anbieter_search")
public class AnbieterSearch implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="suchbegriff")
    private String search;

    @Column(name="org_type")
    private String orgType;

    @Column(name="city_name")
    private String city_name;

    @Column(name="plz")
    private int plz;

    @Column(name="cb_de")
    private int cbDe;

    @Column(name="cb_eu")
    private int cbEu;

    @Column(name="umkreis")
    private int umkreis;

    @Column(name ="count_found")
    private int count_found;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public int getUmkreis() {
        return umkreis;
    }

    public void setUmkreis(int umkreis) {
        this.umkreis = umkreis;
    }

    public int getCount_found() {
        return count_found;
    }

    public void setCount_found(int count_found) {
        this.count_found = count_found;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public int getPlz() {
        return plz;
    }

    public void setPlz(int plz) {
        this.plz = plz;
    }

    public int getCbDe() {
        return cbDe;
    }

    public void setCbDe(int cbDe) {
        this.cbDe = cbDe;
    }

    public int getCbEu() {
        return cbEu;
    }

    public void setCbEu(int cbEu) {
        this.cbEu = cbEu;
    }
}
