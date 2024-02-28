package com.analysetool.repositories;

import com.analysetool.modells.NewsletterSent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsletterSentRepository extends JpaRepository<NewsletterSent, Long> {

    @Query("SELECT SUM(n.open) FROM NewsletterSent n WHERE n.emailId=:emailId")
    int getSumOpenedForEmail(int emailId);

    @Query("SELECT SUM(n.open) FROM NewsletterSent n")
    int getSumOpened();

    @Query("SELECT COUNT(n) FROM NewsletterSent n WHERE n.emailId=:emailId")
    Optional<Double> getAmountSentOfEmail(int emailId);

    @Query("SELECT COUNT(n) FROM NewsletterSent n")
    Optional<Double> getAmountSent();

    @Query("SELECT COUNT(n) FROM NewsletterSent n WHERE n.emailId=:emailId AND n.open > 0")
    Optional<Double> getAmountOpenedBy(int emailId);

    @Query("SELECT COUNT(n) FROM NewsletterSent n WHERE n.open > 0")
    Optional<Double> getAmountOpenedTotal();

    @Query("SELECT COUNT(n) FROM NewsletterSent n WHERE n.emailId=:emailId AND n.error!=''")
    int getAmountErrorsForEmail(int emailId);

    @Query("SELECT COUNT(n) FROM NewsletterSent n WHERE n.error!=''")
    int getAmountErrors();
}
