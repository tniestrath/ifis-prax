package com.analysetool.repositories;

import com.analysetool.modells.NewsletterEmails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterEmailsRepository extends JpaRepository<NewsletterEmails, Long> {

}
