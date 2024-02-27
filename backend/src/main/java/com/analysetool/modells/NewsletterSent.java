package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "wp_newsletter_sent")
@IdClass(NewsletterSentId.class)
public class NewsletterSent implements Serializable {

    @Id
    @Column(name="email_id")
    private int emailId;

    @Id
    @Column(name="user_id")
    private int userId;

    @Column(name="status")
    private int status;

    @Column(name="open")
    private int open;

    @Column(name="time")
    private int time;

    @Column(name="error")
    private String error;

    @Column(name="ip")
    private String ip;

    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
