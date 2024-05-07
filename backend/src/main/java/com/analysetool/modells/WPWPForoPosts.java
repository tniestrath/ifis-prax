package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "wp_wpforo_posts")
public class WPWPForoPosts {

    @Id
    @Column(name="postid")
    private int postId;

    @Column(name="parentid")
    private int parentId;

    @Column(name="forumid")
    private int forumId;

    @Column(name="topicid")
    private int topicId;

    @Column(name="userid")
    private int userId;

    @Column(name="title")
    private String title;

    @Column(name="body")
    private String body;

    @Column(name="created")
    private LocalDateTime created;

    @Column(name="modified")
    private LocalDateTime modified;

    @Column(name="likes")
    private int likes;

    @Column(name="votes")
    private int votes;

    @Column(name="is_answer")
    private int isAnswer;

    @Column(name="is_first_post")
    private int isFirstPost;

    @Column(name="status")
    private int status;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="private")
    private int isPrivate;

    @Column(name="root")
    private int root;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getIsAnswer() {
        return isAnswer;
    }

    public void setIsAnswer(int isAnswer) {
        this.isAnswer = isAnswer;
    }

    public int getIsFirstPost() {
        return isFirstPost;
    }

    public void setIsFirstPost(int isFirstPost) {
        this.isFirstPost = isFirstPost;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }
}
