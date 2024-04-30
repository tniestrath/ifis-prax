package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoPostsRepository extends JpaRepository<WPWPForoPosts, Long> {
}
