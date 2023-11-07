package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "ips_by_user")
public class IPsByUser implements Serializable {

    @Id
    private Long user_id;

    @Column(name = "ips")
    private String ips;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long post_id) {
        this.user_id = post_id;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }
}
