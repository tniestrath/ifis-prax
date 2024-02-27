package com.analysetool.modells;

import java.io.Serializable;
import java.util.Objects;

public class NewsletterSentId implements Serializable {

    private int emailId;
    private int userId;

    // Constructors, equals, and hashCode methods


    public NewsletterSentId(int emailId, int userId) {
        this.emailId = emailId;
        this.userId = userId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsletterSentId that = (NewsletterSentId) o;
        return getEmailId() == that.getEmailId() && getUserId() == that.getUserId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmailId(), getUserId());
    }
}

