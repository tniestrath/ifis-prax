package com.analysetool.modells;

import java.util.Objects;

public class userWp {

    private Long id;
    private String email;
    private String img;
    private String displayName;

    public userWp(Long id, String email, String displayName, String img){
        this.id=id;
        this.email=email;
        this.displayName=displayName;
        this.img=img;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof userWp userWp)) return false;
        return Objects.equals(getId(), userWp.getId()) && Objects.equals(getEmail(), userWp.getEmail()) && Objects.equals(getImg(), userWp.getImg()) && Objects.equals(getDisplayName(), userWp.getDisplayName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getImg(), getDisplayName());
    }

    @Override
    public String toString() {
        return "userWp{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", img='" + img + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
