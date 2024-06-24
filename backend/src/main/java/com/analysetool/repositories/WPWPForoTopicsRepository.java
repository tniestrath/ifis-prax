package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoTopics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPWPForoTopicsRepository extends JpaRepository<WPWPForoTopics, Long> {

    @Query("Select t.topicId FROM WPWPForoTopics t  where t.slug =:slug")
    Integer getTopicIdBySlug(String slug);

    @Query("Select t.forumId FROM WPWPForoTopics t  where t.topicId =:id")
    Integer getForumIdByTopicId(Integer id);

    @Query("SELECT t.topicId FROM WPWPForoTopics t WHERE t.firstPostId=:firstPostId")
    Integer getTopicByFirstPost(int firstPostId);

    @Query("SELECT COUNT(w) FROM WPWPForoTopics w")
    int getCountTopicsTotal();

    @Query("SELECT COUNT(w) FROM WPWPForoTopics w WHERE w.closed = 1")
    int getCountTopicsClosed();

    @Query("SELECT COUNT(w) FROM WPWPForoTopics w WHERE w.solved = 1")
    int getCountTopicsAnswered();

    @Query("SELECT w FROM WPWPForoTopics w WHERE w.forumId=:forumId")
    List<WPWPForoTopics> getAllTopicsInForum(int forumId);

}
