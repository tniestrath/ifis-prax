package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="forum_topics_clicks_by_hour")
public class ForumTopicsClicksByHour implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name = "topic_id")
    private Long topicId;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "clicks")
    private Long clicks;

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

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
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

    public ForumTopicsClicksByHour(Integer uniId, Long topicId, Integer hour, Long clicks) {
        this.uniId = uniId;
        this.topicId = topicId;
        this.hour = hour;
        this.clicks = clicks;
    }

    public ForumTopicsClicksByHour() {}


}
