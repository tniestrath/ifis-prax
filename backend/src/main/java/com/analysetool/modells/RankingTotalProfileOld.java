package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "ranking_total_profile")
public class RankingTotalProfileOld implements Serializable {

    @Id
    @Column(name = "ranking")
    private int rank;

    @Column(name = "user_id")
    private int userId;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
