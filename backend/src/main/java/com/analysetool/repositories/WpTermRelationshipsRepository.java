package com.analysetool.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.analysetool.modells.wp_term_relationships;

import java.util.List;

@Repository
public interface WpTermRelationshipsRepository extends JpaRepository<wp_term_relationships, Long> {
    @Query("Select s.termTaxonomyId from wp_term_relationships s where s.objectId=:id")
    public List<Long> getTaxIdByObject(long id);
}
