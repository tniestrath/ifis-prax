package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "ranking_total_content")
public class RankingTotalContent implements Serializable {

    @Id
    @Column(name = "rank")
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
