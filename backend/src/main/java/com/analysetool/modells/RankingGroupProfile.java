package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "ranking_group_profile")
@IdClass(RankingGroupKey.class)
public class RankingGroupProfile {

    @Id
    @Column(name ="type")
    private String type;

    @Id
    @Column(name ="rank")
    private int rank;

    @Column(name="user_id")
    private int userId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
