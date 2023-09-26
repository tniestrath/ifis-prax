package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "unique_users")
public class UniqueUsers {

    @Id
    @Column("id")
    private int id;

    @Column("ip_hashed")
    private String ip_hashed;

    @Column("access_time")
    private LocalDateTime access_time;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp_hashed() {
        return ip_hashed;
    }

    public void setIp_hashed(String ip_hashed) {
        this.ip_hashed = ip_hashed;
    }

    public LocalDateTime getAccess_time() {
        return access_time;
    }

    public void setAccess_time(LocalDateTime access_time) {
        this.access_time = access_time;
    }
}
