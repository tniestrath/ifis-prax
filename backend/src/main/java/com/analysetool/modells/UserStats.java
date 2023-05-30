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

    public UserStats( long userId, float averagePerformance, float averageRelevance, long profileView) {

        this.userId = userId;
        this.averagePerformance = averagePerformance;
        this.averageRelevance = averageRelevance;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStats userStats)) return false;
        return getIduser_stats() == userStats.getIduser_stats() && getUserId() == userStats.getUserId() && Float.compare(userStats.getAveragePerformance(), getAveragePerformance()) == 0 && Float.compare(userStats.getAverageRelevance(), getAverageRelevance()) == 0 && getProfileView() == userStats.getProfileView();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIduser_stats(), getUserId(), getAveragePerformance(), getAverageRelevance(), getProfileView());
    }

    @Override
    public String toString() {
        return "UserStats{" +
                "iduser_stats=" + iduser_stats +
                ", userId=" + userId +
                ", averagePerformance=" + averagePerformance +
                ", averageReliability=" + averageRelevance +
                ", profileView=" + profileView +
                '}';
    }

    // Constructors, getters, and setters
}

