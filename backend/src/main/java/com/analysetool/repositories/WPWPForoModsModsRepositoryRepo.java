package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoModsMods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPWPForoModsModsRepositoryRepo extends JpaRepository<WPWPForoModsMods, Long> {

    @Query("SELECT w.forum_id FROM WPWPForoModsMods w WHERE w.userId=:userId")
    List<Integer> getAllForumByUser(int userId);


    @Query("SELECT w.forum_id FROM WPWPForoModsMods w")
    List<Integer> getAllForumForAdmin();

    @Query("SELECT u.displayName FROM WPWPForoModsMods w JOIN WPUser u ON w.userId=u.id WHERE u.displayName LIKE %:start% ORDER BY u.displayName LIMIT 5")
    List<String> fetchAllModeratorSuggestions(String start);
}
