package com.analysetool.repositories;
import com.analysetool.modells.PodCastStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PodCastStatsRepository extends JpaRepository<PodCastStats, Long> {
    // Define custom query methods here
}
