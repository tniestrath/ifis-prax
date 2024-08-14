package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iduser_stats;

    @Column(name = "user_id")
    private long userId;
    @Column(name = "profile_view")
    private long profileView;

    @Column(name="content_view")
    private long contentView;

    public UserStats(long userId, long profileView) {
        this.userId = userId;
        this.profileView = profileView;
    }
    public UserStats(){}

    public int getIduser_stats() {
        return iduser_stats;
    }

    public void setIduser_stats(int iduser_stats) {
        this.iduser_stats = iduser_stats;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getProfileView() {
        return profileView;
    }

    public void setProfileView(long profileView) {
        this.profileView = profileView;
    }

    public long getContentView() {
        return contentView;
    }

    public void setContentView(long contentView) {
        this.contentView = contentView;
    }
}

