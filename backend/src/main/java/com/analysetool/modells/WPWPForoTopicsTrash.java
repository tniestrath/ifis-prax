package com.analysetool.modells;

package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "wp_wpforo_topics_trash")
public class WPWPForoTopicsTrash {

    @Id
    @Column(name="topicid")
    private int topicId;

    @Column(name="forumid")
    private int forumId;

    @Column(name = "first_postid")
    private int firstPostId;

    @Column(name = "last_post")
    private int lastPost;

    @Column(name="posts")
    private int posts;

    @Column(name="votes")
    private int votes;

    @Column(name="answers")
    private int answers;

    @Column(name="views")
    private int views;

    @Column(name="userid")
    private int userId;

    @Column(name="title")
    private String title;

    @Column(name="slug")
    private String slug;

    @Column(name="created")
    LocalDateTime created;

    @Column(name="modified")
    LocalDateTime modified;

    @Column(name="meta_key")
    private String metaKey;

    @Column(name="meta_desc")
    private String metaDesc;

    @Column(name="type")
    private int type;

    @Column(name="solved")
    private int solved;

    @Column(name="closed")
    private int closed;

    @Column(name="has_attach")
    private int hasAttach;

    @Column(name="private")
    private int isPrivate;

    @Column(name="status")
    private int status;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="prefix")
    private String prefix;

    @Column(name="tags")
    private String tags;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public int getFirstPostId() {
        return firstPostId;
    }

    public void setFirstPostId(int firstPostId) {
        this.firstPostId = firstPostId;
    }

    public int getLastPost() {
        return lastPost;
    }

    public void setLastPost(int lastPost) {
        this.lastPost = lastPost;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public int getHasAttach() {
        return hasAttach;
    }

    public void setHasAttach(int hasAttach) {
        this.hasAttach = hasAttach;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}

