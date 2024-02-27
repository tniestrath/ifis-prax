package com.analysetool.repositories;

import com.analysetool.modells.EventSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventSearchRepository extends JpaRepository<EventSearch, Long> {

    Page<EventSearch> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT es FROM EventSearch es WHERE es.resultCount=0")
    List<EventSearch> getEventSearchesWithCountZero();

    @Query("SELECT e FROM EventSearch e ORDER BY e.createdAt DESC")
    List<EventSearch> getEventSearchesOrderedByDatum();
}
