package com.analysetool.repositories;

import com.analysetool.modells.NewsletterEmails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsletterEmailsRepository extends JpaRepository<NewsletterEmails, Long> {

    @Query("SELECT n FROM NewsletterEmails n WHERE n.status='sent' ORDER BY n.created DESC LIMIT 1")
    NewsletterEmails getLatestNewsletter();

    @Query("SELECT n FROM NewsletterEmails n WHERE n.status='sent' ORDER BY n.created DESC")
    List<NewsletterEmails> getAllSortedByDate(Pageable pageable);

}
