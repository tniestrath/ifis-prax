package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "wp_posts")
public class Post implements Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "post_author")
    private Long authorId;

    @Column(name = "post_date")
    private LocalDateTime date;

    @Column(name = "post_date_gmt")
    private LocalDateTime dateGmt;

    @Column(name = "post_content")
    private String content;

    @Column(name = "post_title")
    private String title;

    @Column(name = "post_excerpt")
    private String excerpt;

    @Column(name = "post_status")
    private String status;

    @Column(name = "comment_status")
    private String commentStatus;

    @Column(name = "ping_status")
    private String pingStatus;

    @Column(name = "post_password")
    private String password;

    @Column(name = "post_name")
    private String slug;

    @Column(name = "to_ping")
    private String toPing;

    @Column(name = "pinged")
    private String pinged;

    @Column(name = "post_modified")
    private LocalDateTime modified;

    @Column(name = "post_modified_gmt")
    private LocalDateTime modifiedGmt;

    @Column(name = "post_content_filtered")
    private String contentFiltered;

    @Column(name = "post_parent")
    private Long parentId;

    @Column(name = "guid")
    private String guid;

    @Column(name = "menu_order")
    private Integer menuOrder;

    @Column(name = "post_type")
    private String type;

    @Column(name = "post_mime_type")
    private String mimeType;

    @Column(name = "comment_count")
    private Long commentCount;

    // Konstruktoren, Getter und Setter

    public Post(Long id, Long authorId, LocalDateTime date, LocalDateTime dateGmt, String content, String title, String excerpt, String status, String commentStatus, String pingStatus, String password, String slug, String toPing, String pinged, LocalDateTime modified, LocalDateTime modifiedGmt, String contentFiltered, Long parentId, String guid, Integer menuOrder, String type, String mimeType, Long commentCount) {
        this.id = id;
        this.authorId = authorId;
        this.date = date;
        this.dateGmt = dateGmt;
        this.content = content;
        this.title = title;
        this.excerpt = excerpt;
        this.status = status;
        this.commentStatus = commentStatus;
        this.pingStatus = pingStatus;
        this.password = password;
        this.slug = slug;
        this.toPing = toPing;
        this.pinged = pinged;
        this.modified = modified;
        this.modifiedGmt = modifiedGmt;
        this.contentFiltered = contentFiltered;
        this.parentId = parentId;
        this.guid = guid;
        this.menuOrder = menuOrder;
        this.type = type;
        this.mimeType = mimeType;
        this.commentCount = commentCount;
    }

    public Post(){}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDateGmt() {
        return dateGmt;
    }

    public void setDateGmt(LocalDateTime dateGmt) {
        this.dateGmt = dateGmt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getToPing() {
        return toPing;
    }

    public void setToPing(String toPing) {
        this.toPing = toPing;
    }

    public String getPinged() {
        return pinged;
    }

    public void setPinged(String pinged) {
        this.pinged = pinged;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public LocalDateTime getModifiedGmt() {
        return modifiedGmt;
    }

    public void setModifiedGmt(LocalDateTime modifiedGmt) {
        this.modifiedGmt = modifiedGmt;
    }

    public String getContentFiltered() {
        return contentFiltered;
    }

    public void setContentFiltered(String contentFiltered) {
        this.contentFiltered = contentFiltered;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(Integer menuOrder) {
        this.menuOrder = menuOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post post)) return false;
        return Objects.equals(getId(), post.getId()) && Objects.equals(getAuthorId(), post.getAuthorId()) && Objects.equals(getDate(), post.getDate()) && Objects.equals(getDateGmt(), post.getDateGmt()) && Objects.equals(getContent(), post.getContent()) && Objects.equals(getTitle(), post.getTitle()) && Objects.equals(getExcerpt(), post.getExcerpt()) && Objects.equals(getStatus(), post.getStatus()) && Objects.equals(getCommentStatus(), post.getCommentStatus()) && Objects.equals(getPingStatus(), post.getPingStatus()) && Objects.equals(getPassword(), post.getPassword()) && Objects.equals(getSlug(), post.getSlug()) && Objects.equals(getToPing(), post.getToPing()) && Objects.equals(getPinged(), post.getPinged()) && Objects.equals(getModified(), post.getModified()) && Objects.equals(getModifiedGmt(), post.getModifiedGmt()) && Objects.equals(getContentFiltered(), post.getContentFiltered()) && Objects.equals(getParentId(), post.getParentId()) && Objects.equals(getGuid(), post.getGuid()) && Objects.equals(getMenuOrder(), post.getMenuOrder()) && Objects.equals(getType(), post.getType()) && Objects.equals(getMimeType(), post.getMimeType()) && Objects.equals(getCommentCount(), post.getCommentCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthorId(), getDate(), getDateGmt(), getContent(), getTitle(), getExcerpt(), getStatus(), getCommentStatus(), getPingStatus(), getPassword(), getSlug(), getToPing(), getPinged(), getModified(), getModifiedGmt(), getContentFiltered(), getParentId(), getGuid(), getMenuOrder(), getType(), getMimeType(), getCommentCount());
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", date=" + date +
                ", dateGmt=" + dateGmt +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", excerpt='" + excerpt + '\'' +
                ", status='" + status + '\'' +
                ", commentStatus='" + commentStatus + '\'' +
                ", pingStatus='" + pingStatus + '\'' +
                ", password='" + password + '\'' +
                ", slug='" + slug + '\'' +
                ", toPing='" + toPing + '\'' +
                ", pinged='" + pinged + '\'' +
                ", modified=" + modified +
                ", modifiedGmt=" + modifiedGmt +
                ", contentFiltered='" + contentFiltered + '\'' +
                ", parentId=" + parentId +
                ", guid='" + guid + '\'' +
                ", menuOrder=" + menuOrder +
                ", type='" + type + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", commentCount=" + commentCount +
                '}';
    }
}

