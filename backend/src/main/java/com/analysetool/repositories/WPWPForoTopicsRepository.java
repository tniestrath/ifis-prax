package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoTopics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoTopicsRepository extends JpaRepository<WPWPForoTopics, Long> {

    @Query("Select t.topicId FROM WPWPForoTopics t  where t.slug =:slug")
    Integer getTopicIdBySlug(String slug);

    @Query("Select t.forumId FROM WPWPForoTopics t  where t.topicId =:id")
    Integer getForumIdByTopicId(Integer id);

}
