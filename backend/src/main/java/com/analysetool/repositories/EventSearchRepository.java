package com.analysetool.repositories;

import com.analysetool.modells.EventSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSearchRepository extends JpaRepository<EventSearch, Long> {

    Page<EventSearch> findAllByOrderByIdDesc(Pageable pageable);

}
