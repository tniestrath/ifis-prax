package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "feature_wishes")
public class FeatureWishes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="is_new")
    private boolean isNew;

    @Column(name = "feature")
    private String feature;

    @Column(name = "team")
    private String team;

    @Column(name="email")
    private String email;

    @Column(name="fixed")
    private boolean isFixed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }
}
