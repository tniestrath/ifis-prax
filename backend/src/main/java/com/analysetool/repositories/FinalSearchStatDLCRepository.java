package com.analysetool.repositories;

import com.analysetool.modells.FinalSearchStatDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalSearchStatDLCRepository extends JpaRepository<FinalSearchStatDLC, Long> {

    @Query("SELECT f.finalSearchId from FinalSearchStatDLC f where f.postId=:postId and f.finalSearchId > 0")
    List<Long> getFinalSearchStatIdsByPostId(Long postId);

    @Query("SELECT f.finalSearchId from FinalSearchStatDLC f where f.userId=:userId and f.finalSearchId > 0")
    List<Long> getFinalSearchStatIdsByUserId(Long userId);


    List<FinalSearchStatDLC> findAllByFinalSearchId(Long id);
}
