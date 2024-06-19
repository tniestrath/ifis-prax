package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "mod_lock")
public class ModLock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="post_id")
    private long postId;

    @Column(name="locked")
    private int locked;

    @Column(name="by_user")
    private int byUserId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getByUserId() {
        return byUserId;
    }

    public void setByUserId(int byUserId) {
        this.byUserId = byUserId;
    }
}
