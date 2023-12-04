package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "wp_postmeta")
public class PostMeta implements Serializable {

    @Id
    @Column(name = "meta_id")
    private Long meta_id;

    @Column(name = "post_id")
    private Long post_id;

    @Column(name = "meta_key")
    private String meta_key;

    @Column(name = "meta_value")
    private String meta_value;


    public PostMeta(Long meta_id, Long post_id, String meta_key, String meta_value) {
        this.meta_id = meta_id;
        this.post_id = post_id;
        this.meta_key = meta_key;
        this.meta_value = meta_value;
    }

    public PostMeta(){}

    public Long getMeta_id() {
        return meta_id;
    }

    public void setMeta_id(Long meta_id) {
        this.meta_id = meta_id;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public String getMeta_key() {
        return meta_key;
    }

    public void setMeta_key(String meta_key) {
        this.meta_key = meta_key;
    }

    public String getMeta_value() {
        return meta_value;
    }

    public void setMeta_value(String meta_value) {
        this.meta_value = meta_value;
    }
}
