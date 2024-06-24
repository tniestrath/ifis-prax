package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoModsMods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WPWPForoModsModsRepositoryRepo extends JpaRepository<WPWPForoModsMods, Long> {

    @Query("SELECT w FROM WPWPForoModsMods w WHERE w.userId=:userId")
    List<Integer> getAllForumByUser(int userId);

}
