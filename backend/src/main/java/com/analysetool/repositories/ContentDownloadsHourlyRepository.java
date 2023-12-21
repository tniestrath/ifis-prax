package com.analysetool.repositories;

import com.analysetool.modells.ContentDownloadsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentDownloadsHourlyRepository extends JpaRepository<ContentDownloadsHourly, Integer> {
    List<ContentDownloadsHourly> findAllByPostId(Long postId);
    List<ContentDownloadsHourly> findAllByPostIdAndUniId(Long postId,Integer uniId);
    boolean existsByPostId(long postId);

    @Query("SELECT c.uniId FROM ContentDownloadsHourly c WHERE c.postId=:postId ORDER BY c.uniId ASC LIMIT 1")
    long getFirstUniIdByPostId(long postId);

    @Query("SELECT c.uniId FROM ContentDownloadsHourly c ORDER BY c.uniId DESC LIMIT 1")
    long getLastUniId();
    @Query("SELECT c.uniId FROM ContentDownloadsHourly c ORDER BY c.uniId DESC LIMIT 7")
    List<Integer> getLast7Uni();

}
