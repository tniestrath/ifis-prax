package com.analysetool.repositories;

import com.analysetool.modells.NewsletterStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsletterStatsRepository extends JpaRepository<NewsletterStats, Long> {

    @Query("SELECT COUNT(n.url) FROM NewsletterStats n WHERE n.emailId=:emailid AND n.url!=''")
    int getCountInteractionsForEmail(String emailId);

    @Query("SELECT n FROM NewsletterStats n WHERE n.emailId=:emailId")
    List<NewsletterStats> getAllNewsletterStatsOfEmail(String emailId);

}
