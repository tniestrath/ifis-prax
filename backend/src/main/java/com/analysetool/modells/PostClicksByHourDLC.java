package com.analysetool.modells;
import jakarta.persistence.*;


import java.io.Serializable;

@Entity
@Table(name="post_clicks_by_hour_dlc")
public class PostClicksByHourDLC implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "clicks")
    private Long clicks;

    public PostClicksByHourDLC() {}

    public PostClicksByHourDLC(Integer uniId, Long postId, Integer hour, Long clicks) {
        this.uniId = uniId;
        this.postId = postId;
        this.hour = hour;
        this.clicks = clicks;
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

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }
}