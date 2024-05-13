package com.analysetool.repositories;

import com.analysetool.modells.PostStats;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostStatsRepository extends JpaRepository<PostStats, Long> {

    @Query("Select s From PostStats s Where s.artId=:artid ORDER BY s.year DESC LIMIT 1")
    PostStats getStatByArtIDLatestYear(long artid);

    @Query("SELECT s FROM PostStats s WHERE s.artId=:artId AND s.year=:year")
    PostStats findByArtIdAndYear(long artId, int year);

    boolean existsByArtIdAndYear(long artid,int year);

    List<PostStats> findByArtId(Long artId);

    @Query("SELECT SUM(p.clicks) FROM PostStats p WHERE p.artId=:artId")
    Integer getSumClicks(long artId);

    boolean existsByArtId(long artId);

    // Get the number of clicks for a given article ID
    @Query("SELECT s.clicks FROM PostStats s WHERE s.artId = :artId ORDER BY s.year DESC LIMIT 1")
    Long getClicksByArtId(Long artId);

    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.clicks = :clicks , s.performance=:performance WHERE s.artId = :artId AND s.year=:year")
    void updateClicksAndPerformanceByArtId( Long clicks,  Long artId, int year, float performance);

    @Modifying
    @Transactional
    @Query("UPDATE PostStats s SET s.clicks = :clicks, s.searchSuccess = :searchSuccess, s.performance = :performance, s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateClicksSearchSuccessAndRatePerformance(Long artId, Long clicks, Long searchSuccess, float performance, float searchSuccessRate);

    @Modifying
    @Transactional
    @Query("UPDATE PostStats s SET s.lettercount =:lettercount WHERE s.artId =:artId AND s.year =:year")
    void updateLetterCount(int lettercount, long artId, int year);

    default void updateClicksSearchSuccessRateAndPerformance(Long artId, Long clicks, Long searchSuccess, float performance) {
        float searchSuccessRate = (float) clicks / searchSuccess;
        updateClicksSearchSuccessAndRatePerformance(artId, clicks, searchSuccess, performance, searchSuccessRate);
    }

    @Query("select s.performance from PostStats s where s.artId=:artId")
    float getPerformanceByArtID(int artId);

    @Query("SELECT MAX(s.performance) FROM PostStats s")
    float getMaxPerformance();

    @Query("SELECT MAX(s.relevance) FROM PostStats s")
    float getMaxRelevance();
    @Query("SELECT s FROM PostStats s ORDER BY s.performance DESC LIMIT 5")
    List<PostStats> getTop5Relevance();

    @Query("SELECT s FROM PostStats s ORDER BY s.performance DESC LIMIT 5")
    List<PostStats> getTop5Performance();

    @Query("SELECT s.artId FROM PostStats s ORDER BY s.relevance DESC")
    List<Long> getTopRelevanceID(int limit);

    @Query("SELECT s.artId FROM PostStats s ORDER BY s.performance DESC")
    List<Long> getTopPerformanceID(int limit);

    @Query("SELECT MAX(s.lettercount) FROM PostStats s WHERE s.artId=:artId")
    Integer getLetterCount(long artId);

    @Query("SELECT MAX(s.relevance) FROM PostStats s WHERE s.artId=:artId")
    float getRelevanceById(long artId);

    @Query("SELECT s.wordcount FROM PostStats s WHERE s.artId =:artId")
    int getWordCount(int artId);

    @Query("SELECT s.artId FROM PostStats s WHERE s.artId NOT IN (SELECT u.post_id FROM PostTypes u)")
    List<Integer> getIdsOfUntyped();

    @Modifying
    @Transactional
    @Query("UPDATE PostStats s SET s.wordcount =:wordcount WHERE s.artId =:artId")
    void updateWordCount(int wordcount, long artId);


    List<PostStats> findAllByOrderByPerformanceDesc();

    List<PostStats> findAllByOrderByRelevanceDesc();

    List<PostStats> findAllByOrderByClicksDesc();

    List<PostStats>findAllByArtIdIn(List<Long> artId);

    // Beispiel für eine separate Methode zur Berechnung der Performance


}
