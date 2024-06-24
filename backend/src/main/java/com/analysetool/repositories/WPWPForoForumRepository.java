package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPWPForoForumRepository extends JpaRepository<WPWPForoForum, Long> {

    @Query("Select f.forumId FROM WPWPForoForum f  where f.slug =:slug")
    Integer getForumIdBySlug(String slug);

    WPWPForoForum findBySlug(String slug);

    @Query("SELECT COUNT(w) FROM WPWPForoForum w")
    int getCountForums();

    @Query("SELECT w FROM WPWPForoForum w WHERE w.isCat = 0 AND w.forumId!=17 AND w.parentId != 17 AND w.forumId IN (SELECT w.forum_id FROM WPWPForoModsMods w WHERE w.userId=:userId)")
    List<WPWPForoForum> getAllNotCat(int userId);

    @Query("SELECT w FROM WPWPForoForum w WHERE w.isCat = 0 AND w.forumId!=17 AND w.parentId != 17")
    List<WPWPForoForum> getAllNotCatAdmin();

    @Query("SELECT w FROM WPWPForoForum w WHERE w.parentId=:forumId")
    List<WPWPForoForum> getAllChildrenOf(int forumId);

}
