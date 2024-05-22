package com.analysetool.repositories;

import com.analysetool.modells.ForumSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ForumSearchRepository extends JpaRepository<ForumSearch, Long> {

    @Query("SELECT s.suchbegriff, COUNT(s.suchbegriff) as suchCount " +
            "FROM ForumSearch s " +
            "GROUP BY s.suchbegriff " +
            "ORDER BY suchCount DESC")
    List<Object[]> findRankedSearchTerms(Pageable pageable);
}
