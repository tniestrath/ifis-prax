package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "final_search_stat_dlc")
public class FinalSearchStatDLC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "final_search_id")
    private Long finalSearchId;
    @Column(name = "uni_id")
    private int uniId;
    @Column(name = "hour")
    private int hour;
    @Column(name = "clicked_post_id")
    private Long postId;

    public FinalSearchStatDLC() {
    }

    public FinalSearchStatDLC(Long finalSearchId, int uniId, int hour, Long postId) {
        this.finalSearchId = finalSearchId;
        this.uniId = uniId;
        this.hour = hour;
        this.postId = postId;
    }

    public FinalSearchStatDLC(int uniId, int hour, Long postId) {
        this.uniId = uniId;
        this.hour = hour;
        this.postId = postId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFinalSearchId() {
        return finalSearchId;
    }

    public void setFinalSearchId(Long finalSearchId) {
        this.finalSearchId = finalSearchId;
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

    @Override
    public String toString() {
        return "FinalSearchStatDLC{" +
                "id=" + id +
                ", finalSearchId=" + finalSearchId +
                ", uniId=" + uniId +
                ", hour=" + hour +
                ", postId=" + postId +
                '}';
    }

}
