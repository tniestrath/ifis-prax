package com.analysetool.modells;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "wp_comments")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "comment_post_ID")
    private Long postId;

    @Column(name = "comment_author")
    private String author;

    @Column(name = "comment_author_email")
    private String authorEmail;

    @Column(name = "comment_author_url")
    private String authorUrl;

    @Column(name = "comment_author_IP")
    private String authorIp;

    @Column(name = "comment_date")
    private Date date;

    @Column(name = "comment_date_gmt")
    private Date dateGmt;

    @Column(name = "comment_content")
    private String content;

    @Column(name = "comment_karma")
    private Integer karma;

    @Column(name = "comment_approved")
    private String approved;

    @Column(name = "comment_agent")
    private String agent;

    @Column(name = "comment_type")
    private String type;

    @Column(name = "comment_parent")
    private Long parentCommentId;

    @Column(name = "user_id")
    private Long userId;

    // Konstruktoren, Getter und Setter

    // Konstruktor ohne Argumente für die Verwendung durch JPA
    public Comments() {
    }

    public Comments(Long postId, String author, String authorEmail, String authorUrl, String authorIp, Date date, Date dateGmt, String content, Integer karma, String approved, String agent, String type, Long parentCommentId, Long userId) {
        this.postId = postId;
        this.author = author;
        this.authorEmail = authorEmail;
        this.authorUrl = authorUrl;
        this.authorIp = authorIp;
        this.date = date;
        this.dateGmt = dateGmt;
        this.content = content;
        this.karma = karma;
        this.approved = approved;
        this.agent = agent;
        this.type = type;
        this.parentCommentId = parentCommentId;
        this.userId = userId;
    }

    // Getter und Setter für alle Attribute
    // ...

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getAuthorIp() {
        return authorIp;
    }

    public void setAuthorIp(String authorIp) {
        this.authorIp = authorIp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateGmt() {
        return dateGmt;
    }

    public void setDateGmt(Date dateGmt) {
        this.dateGmt = dateGmt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getKarma() {
        return karma;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comments comments = (Comments) o;
        return Objects.equals(commentId, comments.commentId) && Objects.equals(postId, comments.postId) && Objects.equals(author, comments.author) && Objects.equals(authorEmail, comments.authorEmail) && Objects.equals(authorUrl, comments.authorUrl) && Objects.equals(authorIp, comments.authorIp) && Objects.equals(date, comments.date) && Objects.equals(dateGmt, comments.dateGmt) && Objects.equals(content, comments.content) && Objects.equals(karma, comments.karma) && Objects.equals(approved, comments.approved) && Objects.equals(agent, comments.agent) && Objects.equals(type, comments.type) && Objects.equals(parentCommentId, comments.parentCommentId) && Objects.equals(userId, comments.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, postId, author, authorEmail, authorUrl, authorIp, date, dateGmt, content, karma, approved, agent, type, parentCommentId, userId);
    }

    @Override
    public String toString() {
        return "Comments{" +
                "commentId=" + commentId +
                ", postId=" + postId +
                ", author='" + author + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", authorIp='" + authorIp + '\'' +
                ", date=" + date +
                ", dateGmt=" + dateGmt +
                ", content='" + content + '\'' +
                ", karma=" + karma +
                ", approved='" + approved + '\'' +
                ", agent='" + agent + '\'' +
                ", type='" + type + '\'' +
                ", parentCommentId=" + parentCommentId +
                ", userId=" + userId +
                '}';
    }
}
