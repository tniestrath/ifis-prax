package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "user_subscriptions")
public class UserSubscriptions implements Serializable {

    @Id
    @Column(name ="user_id")
    private long userId;

    @Column(name="sub_id")
    private long subId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSubId() {
        return subId;
    }

    public void setSubId(long subId) {
        this.subId = subId;
    }
}
