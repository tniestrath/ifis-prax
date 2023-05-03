package com.analysetool.repositories;
import com.analysetool.modells.WpTermTaxonomy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WpTermTaxonomyRepository extends JpaRepository<WpTermTaxonomy, Long> {

    @Query("SELECT p.termId FROM WpTermTaxonomy p WHERE p.taxonomy = 'post_tag'")
    List<Long> getAllPostTags();
}
