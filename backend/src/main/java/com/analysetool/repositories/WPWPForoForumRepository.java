package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoForumRepository extends JpaRepository<WPWPForoForum, Long> {
}
