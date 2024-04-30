package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "wp_wpforo_forums")
public class WPWPForoForum {

    @Id
    @Column(name="forumid")
    private int forumId;

    @Column(name="title")
    private String title;

    @Column(name="slug")
    private String slug;

    @Column(name="description")
    private String desc;

    @Column(name="parentid")
    private int parentId;

    @Column(name="icon")
    private String icon;

    @Column(name="cover")
    private int cover;

    @Column(name="cover_height")
    private int coverHeight;

    @Column(name="last_topicid")
    private int lastTopicId;

    @Column(name="last_postid")
    private int lastPostId;

    @Column(name="last_userid")
    private int lastUserId;

    @Column(name="last_post_date")
    private LocalDateTime lastPostDate;

    @Column(name="topics")
    private int topics;

    @Column(name="posts")
    private int posts;

    @Column(name="permissions")
    private String permissions;

    @Column(name="meta_key")
    private String metaKey;

    @Column(name="meta_desc")
    private String metaDesc;

    @Column(name="status")
    private int status;

    @Column(name="is_cat")
    private int isCat;

    @Column(name="layout")
    private int layout;

    @Column(name="order")
    private int order;

    @Column(name="color")
    private String color;

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public int getCoverHeight() {
        return coverHeight;
    }

    public void setCoverHeight(int coverHeight) {
        this.coverHeight = coverHeight;
    }

    public int getLastTopicId() {
        return lastTopicId;
    }

    public void setLastTopicId(int lastTopicId) {
        this.lastTopicId = lastTopicId;
    }

    public int getLastPostId() {
        return lastPostId;
    }

    public void setLastPostId(int lastPostId) {
        this.lastPostId = lastPostId;
    }

    public int getLastUserId() {
        return lastUserId;
    }

    public void setLastUserId(int lastUserId) {
        this.lastUserId = lastUserId;
    }

    public LocalDateTime getLastPostDate() {
        return lastPostDate;
    }

    public void setLastPostDate(LocalDateTime lastPostDate) {
        this.lastPostDate = lastPostDate;
    }

    public int getTopics() {
        return topics;
    }

    public void setTopics(int topics) {
        this.topics = topics;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getMetaDesc() {
        return metaDesc;
    }

    public void setMetaDesc(String metaDesc) {
        this.metaDesc = metaDesc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsCat() {
        return isCat;
    }

    public void setIsCat(int isCat) {
        this.isCat = isCat;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
