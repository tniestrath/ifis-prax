package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "anbieter_search")
public class AnbieterSearch implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="city_name")
    private String city_name;

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
}
