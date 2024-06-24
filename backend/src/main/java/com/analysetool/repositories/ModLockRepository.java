package com.analysetool.repositories;

import com.analysetool.modells.ModLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModLockRepository extends JpaRepository<ModLock, Long> {

    @Query("SELECT w FROM ModLock w WHERE w.postId=:postId")
    Optional<ModLock> findByPostId(int postId);

    @Query("SELECT w FROM ModLock w WHERE w.byUserId=:userId")
    List<ModLock> findByUserId(int userId);

}
