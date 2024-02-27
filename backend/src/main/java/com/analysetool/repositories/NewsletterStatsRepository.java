package com.analysetool.repositories;

import com.analysetool.modells.NewsletterStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterStatsRepository extends JpaRepository<NewsletterStats, Long> {

}
