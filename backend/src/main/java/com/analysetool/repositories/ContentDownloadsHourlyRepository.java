package com.analysetool.repositories;

import com.analysetool.modells.ContentDownloadsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentDownloadsHourlyRepository extends JpaRepository<ContentDownloadsHourly, Integer> {
    List<ContentDownloadsHourly> findAllByPostId(Long postId);
    List<ContentDownloadsHourly> findAllByPostIdAndUniId(Long postId,Integer uniId);
    boolean existsByPostId(long postId);
    @Query("SELECT SUM(c.downloads) FROM ContentDownloadsHourly c")
    Long getAllDownloadsSummed();
    @Query("SELECT SUM(c.downloads) FROM ContentDownloadsHourly c WHERE c.postId=:postId")
    Long getAllDownloadsOfPostIdSummed(Long postId);
    @Query("SELECT SUM(c.downloads) FROM ContentDownloadsHourly c WHERE c.postId IN :postIds")
    Long findSumOfDownloadsForPostIds(List<Long> postIds);
    @Query("SELECT c.postId, SUM(c.downloads) FROM ContentDownloadsHourly c GROUP BY c.postId")
    List<Object[]> getPostIdAndDownloadsSum();
    @Query("SELECT c.postId FROM ContentDownloadsHourly c")
    List<Long> findAllPostIds();
    @Query("SELECT c.uniId FROM ContentDownloadsHourly c WHERE c.postId=:postId ORDER BY c.uniId ASC LIMIT 1")
    long getFirstUniIdByPostId(long postId);
    @Query("SELECT c.uniId FROM ContentDownloadsHourly c ORDER BY c.uniId DESC LIMIT 1")
    long getLastUniId();
    @Query("SELECT c.uniId FROM ContentDownloadsHourly c ORDER BY c.uniId DESC LIMIT 7")
    List<Integer> getLast7Uni();
    List<ContentDownloadsHourly> findAllByPostIdInAndUniId(List<Long> postIds, Integer uniId);

    @Query("SELECT c FROM ContentDownloadsHourly c WHERE c.postId=:postId AND c.uniId=:uniId AND c.hour=:hour")
    Optional<ContentDownloadsHourly> getByPostIdUniIdHour(long postId, long uniId, int hour);
}
