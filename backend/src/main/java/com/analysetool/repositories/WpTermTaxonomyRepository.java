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

    @Query("SELECT p  FROM WpTermTaxonomy p GROUP BY p.termTaxonomyId ORDER BY p.count DESC LIMIT 10")
    List<WpTermTaxonomy> findTop10TermIdsByCount();

    @Query("select p.termId from WpTermTaxonomy p where p.termTaxonomyId IN :ids")
    List<Long> getTermIdByTaxId(List<Long> ids);


}
