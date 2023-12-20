package com.analysetool.modells;

import jakarta.persistence.*;


import java.io.Serializable;

@Entity
@Table(name="content_downloads_hourly")
public class ContentDownloadsHourly implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "downloads")
    private Long downloads;

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

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

    public ContentDownloadsHourly(Integer uniId, Long postId, Integer hour, Long downloads) {
        this.uniId = uniId;
        this.postId = postId;
        this.hour = hour;
        this.downloads = downloads;
    }
    public ContentDownloadsHourly(){}

}