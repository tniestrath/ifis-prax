package com.analysetool.repositories;
import com.analysetool.modells.WpTermTaxonomy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WpTermTaxonomyRepository extends JpaRepository<WpTermTaxonomy, Long> {
}
