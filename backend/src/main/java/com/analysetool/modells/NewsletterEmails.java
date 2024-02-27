package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "wp_newsletter_emails")
public class NewsletterEmails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="language")
    private String language;

    @Column(name="subject")
    private String subject;

    @Column(name="message")
    private String message;

    @Column(name="created")
    private Timestamp created;

    @Column(name="status")
    private String status;

    @Column(name="total")
    private int total;

    @Column(name="last_id")
    private int lastId;

    @Column(name="sent")
    private int sent;

    @Column(name="track")
    private int track;

    @Column(name="list")
    private int list;

    @Column(name="type")
    private String type;

    @Column(name="query")
    private String query;

    @Column(name="editor")
    private int editor;

    @Column(name="sex")
    private String sex;

    @Column(name="theme")
    private String theme;

    @Column(name="message_text")
    private String messageText;

    @Column(name="preferences")
    private String preferences;

    @Column(name="send_on")
    private int sendOn;

    @Column(name="token")
    private String token;

    @Column(name="options")
    private String options;

    @Column(name="private")
    private String privacy;

    @Column(name="click_count")
    private int clickCount;

    @Column(name="version")
    private String version;

    @Column(name="open_count")
    private int openCount;

    @Column(name="unsub_count")
    private int unsubCount;

    @Column(name="error_count")
    private String errorCount;

    @Column(name="stats_time")
    private int statsTime;

    @Column(name="updated")
    private String updated;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public int getList() {
        return list;
    }

    public void setList(int list) {
        this.list = list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getEditor() {
        return editor;
    }

    public void setEditor(int editor) {
        this.editor = editor;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public int getSendOn() {
        return sendOn;
    }

    public void setSendOn(int sendOn) {
        this.sendOn = sendOn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getOpenCount() {
        return openCount;
    }

    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }

    public int getUnsubCount() {
        return unsubCount;
    }

    public void setUnsubCount(int unsubCount) {
        this.unsubCount = unsubCount;
    }

    public String getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(String errorCount) {
        this.errorCount = errorCount;
    }

    public int getStatsTime() {
        return statsTime;
    }

    public void setStatsTime(int statsTime) {
        this.statsTime = statsTime;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
