package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "wp_wpforo_tags")
public class WPWPForoTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tagid")
    private Long id;

    @Column(name ="tag")
    private String tag;

    @Column(name="prefix")
    private boolean prefix;

    @Column(name="count")
    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
