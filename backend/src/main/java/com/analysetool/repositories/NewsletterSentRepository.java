package com.analysetool.repositories;

import com.analysetool.modells.NewsletterSent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterSentRepository extends JpaRepository<NewsletterSent, Long> {

}
