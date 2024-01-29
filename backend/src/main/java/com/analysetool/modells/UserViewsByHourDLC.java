package com.analysetool.modells;

import jakarta.persistence.*;


import java.io.Serializable;

@Entity
@Table(name="user_views_by_hour_dlc")
public class UserViewsByHourDLC implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="hour")
    private Integer hour;

    @Column(name="views")
    private Long views;

    public UserViewsByHourDLC(Integer uniId, Long userId, Integer hour, Long views) {
        this.uniId = uniId;
        this.userId = userId;
        this.hour = hour;
        this.views = views;
    }

    public UserViewsByHourDLC() {
    }

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

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "UserViewsByHourDLC{" +
                "id=" + id +
                ", uniId=" + uniId +
                ", userId=" + userId +
                ", hour=" + hour +
                ", views=" + views +
                '}';
    }
}
