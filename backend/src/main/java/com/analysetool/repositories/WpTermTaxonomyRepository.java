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

    @Query("SELECT p.count from WpTermTaxonomy p where p.taxonomy = 'post_tag'")
    List<Integer> getCount();

    @Query("SELECT p.termId FROM WpTermTaxonomy p WHERE p.taxonomy='post_tag' AND p.count >= (SELECT (:percentage * 0.01) * SUM(s.count) FROM WpTermTaxonomy s WHERE s.taxonomy = 'post_tag') ORDER BY p.count DESC")
    List<Long> getCountAbove(int percentage);

    @Query("SELECT p.count from WpTermTaxonomy p where p.termId = :id")
    Long getCountById(int id);

    WpTermTaxonomy findByTermId(int termId);
    List<WpTermTaxonomy> findByTermTaxonomyId(Long termTaxonomyId);

    @Query("SELECT t.termId FROM WpTermTaxonomy t WHERE t.taxonomy = 'post_tag'")
    List<Long> getTermIdsOfPostTags();

}
