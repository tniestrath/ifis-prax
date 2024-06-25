package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPWPForoPostsRepository extends JpaRepository<WPWPForoPosts, Long> {

    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 1 AND w.isPrivate = 0 ORDER BY w.created ASC")
    List<WPWPForoPosts> getUnmoderatedPosts();

    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 0 AND w.isPrivate = 0 ORDER BY w.created ASC")
    List<WPWPForoPosts> getModeratedPosts();


    @Query("SELECT COUNT(w) FROM WPWPForoPosts w WHERE w.isPrivate = 0 AND w.isFirstPost = 1")
    int getCountQuestions();

    @Query("SELECT COUNT(w) FROM WPWPForoPosts w WHERE w.isPrivate = 0 AND w.isFirstPost = 0")
    int getCountAnswers();

    @Query("SELECT COUNT(w) FROM WPWPForoPosts w WHERE w.status = 1 AND w.topicId=:topicId")
    int getCountUnmoderatedInTopic(int topicId);

    @Query("SELECT COUNT(w) FROM WPWPForoPosts w WHERE w.status = 1 AND w.forumId=:forumId OR w.forumId IN (SELECT wc.forumId FROM WPWPForoForum wc WHERE wc.parentId = w.forumId)")
    int getCountUnmoderatedInForum(int forumId);

    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 1 AND w.isPrivate = 0 AND w.forumId IN :filterForum AND (w.title LIKE %:search% OR w.body LIKE %:search% OR w.name LIKE %:search%)")
    List<WPWPForoPosts> geUnmoderatedWithFilter(List<Integer> filterForum, String search);

    //ToDo change behavior for actual category
    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 1 AND w.isPrivate = 0 AND w.forumId IN :filterForum AND w.forumId =:forumCat AND (w.title LIKE %:search% OR w.body LIKE %:search% OR w.name LIKE %:search%)")
    List<WPWPForoPosts> geUnmoderatedWithFilters2(List<Integer> filterForum, int forumCat, String search);

    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 1 AND w.isPrivate = 0 AND w.forumId IN :filterForum AND w.topicId=:forumTopic  AND (w.title LIKE %:search% OR w.body LIKE %:search% OR w.name LIKE %:search%)")
    List<WPWPForoPosts> geUnmoderatedWithFilters3(List<Integer> filterForum, int forumTopic, String search);


    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 0 AND w.isPrivate = 0 AND w.forumId IN :filterForum AND (w.title LIKE %:search% OR w.body LIKE %:search% OR w.name LIKE %:search%)")
    List<WPWPForoPosts> geModeratedWithFilter(List<Integer> filterForum, String search);

    //ToDo change behavior for actual category
    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 0 AND w.isPrivate = 0 AND w.forumId IN :filterForum AND w.forumId=:forumCat AND (w.title LIKE %:search% OR w.body LIKE %:search% OR w.name LIKE %:search%)")
    List<WPWPForoPosts> geModeratedWithFilters2(List<Integer> filterForum, int forumCat, String search);

    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 0 AND w.isPrivate = 0 AND w.forumId IN :filterForum AND w.topicId=:forumTopic  AND (w.title LIKE %:search% OR w.body LIKE %:search% OR w.name LIKE %:search%)")
    List<WPWPForoPosts> getModeratedWithFilters3(List<Integer> filterForum, int forumTopic, String search);

}
