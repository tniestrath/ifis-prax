package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "bounce_house")
public class Bounce implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="uni_id")
    private int uniId;

    @Column(name="bounce_rate")
    private double bounceRate;

    @Column(name="total_bounces")
    private int totalBounces;

    public Bounce(){}

    public Bounce (int uniId, double bounceRate, int totalBounces) {
        this.uniId = uniId;
        this.totalBounces = totalBounces;
        this.bounceRate = bounceRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUniId() {
        return uniId;
    }

    public void setUniId(int uniId) {
        this.uniId = uniId;
    }

    public double getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(double bounceRate) {
        this.bounceRate = bounceRate;
    }

    public int getTotalBounces() {
        return totalBounces;
    }

    public void setTotalBounces(int totalBounces) {
        this.totalBounces = totalBounces;
    }
}
