package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "blocked_searches")
public class BlockedSearches implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="final_search_stats_id")
    private Long blocked_search_id;

    @Column(name="search")
    private String search;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBlocked_search_id() {
        return blocked_search_id;
    }

    public void setBlocked_search_id(Long blocked_search_id) {
        this.blocked_search_id = blocked_search_id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
