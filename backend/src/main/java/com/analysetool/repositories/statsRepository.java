package com.analysetool.repositories;

import com.analysetool.modells.stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface statsRepository extends JpaRepository<stats, Long> {
}
