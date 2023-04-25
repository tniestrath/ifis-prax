package com.analysetool.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.analysetool.modells.wp_term_relationships;

@Repository
public interface WpTermRelationshipsRepository extends JpaRepository<wp_term_relationships, Long> {
}
