package com.analysetool.modells;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "wp_users")
public class WPUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "user_activation_key")
    private String activationKey;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_login")
    private String login;

    @Column(name = "user_nicename")
    private String nicename;

    @Column(name = "user_pass")
    private String password;

    @Column(name = "user_registered")
    private LocalDateTime registered;

    @Column(name = "user_status")
    private Integer status;

    @Column(name = "user_url")
    private String url;
    @Transient
    private String img;
    // Konstruktoren, Getter und Setter

    public WPUser(Long id, String email, String displayName, String img){
        this.id=id;
        this.email=email;
        this.displayName=displayName;
        this.img=img;}

    public WPUser() {
        // default constructor
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNicename() {
        return nicename;
    }

    public void setNicename(String nicename) {
        this.nicename = nicename;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getRegistered() {
        return registered;
    }

    public void setRegistered(LocalDateTime registered) {
        this.registered = registered;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WPUser wpUser)) return false;
        return Objects.equals(getId(), wpUser.getId()) && Objects.equals(getDisplayName(), wpUser.getDisplayName()) && Objects.equals(getActivationKey(), wpUser.getActivationKey()) && Objects.equals(getEmail(), wpUser.getEmail()) && Objects.equals(getLogin(), wpUser.getLogin()) && Objects.equals(getNicename(), wpUser.getNicename()) && Objects.equals(getPassword(), wpUser.getPassword()) && Objects.equals(getRegistered(), wpUser.getRegistered()) && Objects.equals(getStatus(), wpUser.getStatus()) && Objects.equals(getUrl(), wpUser.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDisplayName(), getActivationKey(), getEmail(), getLogin(), getNicename(), getPassword(), getRegistered(), getStatus(), getUrl());

    }

    @Override
    public String toString() {
        return "WPUser{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", activationKey='" + activationKey + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", nicename='" + nicename + '\'' +
                ", password='" + password + '\'' +
                ", registered=" + registered +
                ", status=" + status +
                ", url='" + url + '\'' +
                '}';
    }
}

