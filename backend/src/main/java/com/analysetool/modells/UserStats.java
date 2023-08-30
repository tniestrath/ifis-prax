package com.analysetool.modells;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iduser_stats;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "avg_perf")
    private float averagePerformance;

    @Column(name = "avg_rel")
    private float averageRelevance;

    @Column(name = "profile_view")
    private long profileView;

    @Column(name = "impressions")
    private long impressions;

    @Column(name = "interaction_rate")
    private float interactionRate;

    @Column(name = "refferings")
    private Long refferings;
    @Column(name = "ref_rate")
    private float refferingRate;

    public Long getRefferings() {
        return refferings;
    }

    public void setRefferings(Long refferings) {
        this.refferings = refferings;
    }

    public UserStats(long userId, float averagePerformance, float averageRelevance, long profileView, float interactionRate, float refferingRate, float postFrequence, Long refferings) {

        this.userId = userId;
        this.averagePerformance = averagePerformance;
        this.averageRelevance = averageRelevance;
        this.profileView = profileView;
        this.interactionRate = interactionRate;
        this.refferingRate = refferingRate;
        this.postFrequence = postFrequence;
        this.refferings=refferings;
    }

    public UserStats(long userId, long profileView, long impressions) {
        this.userId = userId;
        this.profileView = profileView;
        this.impressions = impressions;
    }

    public float getRefferingRate() {
        return refferingRate;
    }

    public void setRefferingRate(float refferingRate) {
        this.refferingRate = refferingRate;
    }

    public float getInteractionRate() {
        return interactionRate;
    }

    public void setInteractionRate(float interactionRate) {
        this.interactionRate = interactionRate;
    }

    public float getPostFrequence() {
        return postFrequence;
    }

    public void setPostFrequence(float postFrequence) {
        this.postFrequence = postFrequence;
    }

    @Column(name = "post_freq")
    private float postFrequence;

    @Override
    public String toString() {
        return "UserStats{" +
                "iduser_stats=" + iduser_stats +
                ", userId=" + userId +
                ", averagePerformance=" + averagePerformance +
                ", averageRelevance=" + averageRelevance +
                ", profileView=" + profileView +
                ", interactionRate=" + interactionRate +
                ", refferingRate=" + refferingRate +
                ", postFrequence=" + postFrequence +
                '}';
    }

    public UserStats(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStats userStats)) return false;
        return getIduser_stats() == userStats.getIduser_stats() && getUserId() == userStats.getUserId() && Float.compare(userStats.getAveragePerformance(), getAveragePerformance()) == 0 && Float.compare(userStats.getAverageRelevance(), getAverageRelevance()) == 0 && getProfileView() == userStats.getProfileView() && Float.compare(userStats.getInteractionRate(), getInteractionRate()) == 0 && Float.compare(userStats.getRefferingRate(), getRefferingRate()) == 0 && Float.compare(userStats.getPostFrequence(), getPostFrequence()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIduser_stats(), getUserId(), getAveragePerformance(), getAverageRelevance(), getProfileView(), getInteractionRate(), getRefferingRate(), getPostFrequence());
    }

    public long getImpressions() {
        return impressions;
    }

    public void setImpressions(long impressions) {
        this.impressions = impressions;
    }

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

    public float getAveragePerformance() {
        return averagePerformance;
    }

    public void setAveragePerformance(float averagePerformance) {
        this.averagePerformance = averagePerformance;
    }

    public float getAverageRelevance() {
        return averageRelevance;
    }

    public void setAverageRelevance(float averageRelevance) {
        this.averageRelevance = averageRelevance;
    }

    public long getProfileView() {
        return profileView;
    }

    public void setProfileView(long profileView) {
        this.profileView = profileView;
    }

    // Constructors, getters, and setters
}

