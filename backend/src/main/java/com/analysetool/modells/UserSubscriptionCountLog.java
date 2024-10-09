package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "user_subscription_count_log")
public class UserSubscriptionCountLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="uni_id")
    private int uniId;

    @Column(name="count")
    private int count;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUniId() {
        return uniId;
    }

    public void setUniId(int uniId) {
        this.uniId = uniId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
