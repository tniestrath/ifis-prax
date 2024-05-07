package com.analysetool.modells;

import jakarta.persistence.*;


import java.io.Serializable;

@Entity
@Table(name="forum_discussion_clicks_by_hour")
public class ForumDiskussionsthemenClicksByHour implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name = "forum_id")
    private Long forumId;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "clicks")
    private Long clicks;

    public ForumDiskussionsthemenClicksByHour() {}

    public ForumDiskussionsthemenClicksByHour(Integer uniId, Long forumId, Integer hour, Long clicks) {
        this.uniId = uniId;
        this.forumId = forumId;
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

    public Long getForumId() {
        return forumId;
    }

    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    public Integer getForumIdInteger() {
        return forumId.intValue();
    }

    public void setForumIdInteger(Integer forumId) {
        this.forumId = forumId.longValue();
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