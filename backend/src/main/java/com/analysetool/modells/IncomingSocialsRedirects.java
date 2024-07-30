package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="incoming_socials_redirects")
public class IncomingSocialsRedirects implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uni_id")
    private Integer uniId;

    @Column(name = "hour")
    private Integer hour;
    @Column(name = "linkedin")
    private Long linkedin;
    @Column(name = "facebook")
    private Long facebook;

    @Column(name = "twitter")
    private Long twitter;

    @Column(name = "youtube")
    private Long youtube;


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

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Long getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(Long linkedin) {
        this.linkedin = linkedin;
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

    public Long getYoutube() {
        return youtube;
    }

    public void setYoutube(Long youtube) {
        this.youtube = youtube;
    }

    @Override
    public String toString() {
        return "OutgoingSocialsRedirects{" +
                "id=" + id +
                ", uniId=" + uniId +
                ", hour=" + hour +
                ", linkedin=" + linkedin +
                ", facebook=" + facebook +
                ", twitter=" + twitter +
                ", youtube=" + youtube +
                '}';
    }
}
