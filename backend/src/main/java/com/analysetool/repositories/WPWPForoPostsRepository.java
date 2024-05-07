package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPWPForoPostsRepository extends JpaRepository<WPWPForoPosts, Long> {

    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 1 AND w.isPrivate = 0")
    List<WPWPForoPosts> getUnmoderatedPosts();

    @Query("SELECT w FROM WPWPForoPosts w WHERE w.status = 0 AND w.isPrivate = 0")
    List<WPWPForoPosts> getModeratedPosts();
}
