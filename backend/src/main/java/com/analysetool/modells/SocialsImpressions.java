package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "socials_impressions")
public class SocialsImpressions implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uni_id")
    private int uniId;

    @Column(name = "hour")
    private int hour;

    @Column(name = "post_id")
    private Long postId;
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "facebook")
    private Long facebook;

    @Column(name = "twitter")
    private Long twitter;

    @Column(name = "linkedin")
    private Long linkedIn;

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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFacebook() {
        return facebook;
    }

    public void setFacebook(Long facebook) {
        this.facebook = facebook;
    }

    public Long getTwitter() {
        return twitter;
    }

    public void setTwitter(Long twitter) {
        this.twitter = twitter;
    }

    public Long getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(Long linkedIn) {
        this.linkedIn = linkedIn;
    }

}
