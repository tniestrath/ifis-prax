package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "wp_ppma_authors_relationships")
public class AuthorsRelationships implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name="category_id")
    private long catId;

    @Column(name="category_slug")
    private String catSlug;

    @Column(name="post_id")
    private long postId;

    @Column(name="author_term_id")
    private long authorTerm;

    @Column(name="author_user_id")
    private long authorUser;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    public String getCatSlug() {
        return catSlug;
    }

    public void setCatSlug(String catSlug) {
        this.catSlug = catSlug;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getAuthorTerm() {
        return authorTerm;
    }

    public void setAuthorTerm(long authorTerm) {
        this.authorTerm = authorTerm;
    }
}
