package com.analysetool.modells;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "user_redirects_hourly")
public class UserRedirectsHourly implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "redirects")
    private Long redirects;

    public UserRedirectsHourly() {}

    public UserRedirectsHourly(Integer uniId, Long userId, Integer hour, Long redirects) {
        this.uniId = uniId;
        this.userId = userId;
        this.hour = hour;
        this.redirects = redirects;
    }

    // Getters and setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUniId() {
        return uniId;
    }

    public void setUniId(Integer uniId) {
        this.uniId = uniId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Long getRedirects() {
        return redirects;
    }

    public void setRedirects(Long redirects) {
        this.redirects = redirects;
    }
}
