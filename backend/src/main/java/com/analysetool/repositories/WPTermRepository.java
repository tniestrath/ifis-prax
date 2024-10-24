package com.analysetool.repositories;

import com.analysetool.modells.Post;
import com.analysetool.modells.WPTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPTermRepository extends JpaRepository<WPTerm, Long> {

    @Query("SELECT count(tr) from wp_term_relationships tr Where tr.termTaxonomyId = :id")
    long getPostCount(String id);

    @Query("SELECT p.name FROM WPTerm p WHERE p.id = :id")
    String getNameById(int id);

    WPTerm findBySlug(String slug);
}

