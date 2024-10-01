package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "ranking_group_profile_old")
@IdClass(RankingGroupKey.class)
public class RankingGroupProfileOld implements Serializable {

    @Id
    @Column(name ="type")
    private String type;

    @Id
    @Column(name ="ranking")
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
