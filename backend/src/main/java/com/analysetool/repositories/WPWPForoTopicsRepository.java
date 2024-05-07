package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoTopics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoTopicsRepository extends JpaRepository<WPWPForoTopics, Long> {
}
