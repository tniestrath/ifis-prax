package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoForumRepository extends JpaRepository<WPWPForoForum, Long> {

    @Query("Select f.forumId FROM WPWPForoForum f  where f.slug =:slug")
    Integer getForumIdBySlug(String slug);

    WPWPForoForum findBySlug(String slug);

    @Query("SELECT COUNT(w) FROM WPWPForoForum w")
    int getCountForums();
}
