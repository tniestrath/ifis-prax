package com.analysetool.repositories;

import com.analysetool.modells.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsRepository extends JpaRepository<UserStats, Integer> {
    // Add custom queries or methods if needed
    boolean existsByUserId(Long userId);

    UserStats findByUserId(Long userId);
}