package com.analysetool.repositories;

import com.analysetool.modells.Post;
import com.analysetool.modells.PostStats;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
//import org.springframework.data.repository.query.Param;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostStatsRepository extends JpaRepository<PostStats, Long> {

    @Query("Select S From PostStats S Where S.artId=:artid")
    PostStats getStatByArtID(long artid);

    PostStats findByArtIdAndAndYear(long artid,int year);
    boolean existsByArtIdAndYear(long artid,int year);

    @Modifying
    @Query("UPDATE PostStats s SET s.searchSuccessRate = :searchSuccessRate, s.articleReferringRate = :articleReferringRate, s.clicks = :clicks, s.searchSuccess = :searchSucces, s.refferings = :refferings WHERE s.artId = :artId")
    void updateStats(Long artId,  Float searchSuccessRate,  Float articleReferringRate,  Long clicks,Long searchSucces,  Long refferings);

    // Methode zum Aktualisieren der searchSuccessRate-Spalte
    @Modifying
    @Query("UPDATE PostStats s SET s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateSearchSuccessRate(Long artId, Float searchSuccessRate);

    // Methode zum Aktualisieren der articleReferringRate-Spalte
    @Modifying
    @Query("UPDATE PostStats s SET s.articleReferringRate = :articleReferringRate WHERE s.artId = :artId")
    void updateArticleReferringRate( Long artId,  Float articleReferringRate);

    // Methode zum Aktualisieren der clicks-Spalte
    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.clicks = :clicks WHERE s.artId = :artId")
    void updateClicks( Long artId,  Long clicks);

    // Methode zum Aktualisieren der searchSucces-Spalte
    @Modifying
    @Query("UPDATE PostStats s SET s.searchSuccess = :searchSuccess WHERE s.artId = :artId")
    void updateSearchSucces( Long artId,Long searchSuccess);

    // Methode zum Aktualisieren der refferings-Spalte
    @Modifying
    @Query("UPDATE PostStats s SET s.refferings = :refferings WHERE s.artId = :artId")
    void updateRefferings( Long artId,  Long refferings);

    List<PostStats> findByArtId(Long artId);

    // Zähle die Anzahl der Stats-Objekte anhand der artId
    Long countByArtId(Long artId);

    // Suche nach einem bestimmten Stats-Objekt anhand der artId
    Optional<PostStats> findOneByArtId(Long artId);

    // Aktualisiere die searchSuccessRate-Spalte für einen bestimmten Artikel
    @Modifying
    @Query("UPDATE PostStats s SET s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateSearchSuccessRateByArtId( Float searchSuccessRate,  Long artId);

    // Aktualisiere die articleReferringRate-Spalte für einen bestimmten Artikel
    @Modifying
    @Query("UPDATE PostStats s SET s.articleReferringRate = :articleReferringRate WHERE s.artId = :artId")
    void updateArticleReferringRateByArtId( Float articleReferringRate, Long artId);

    // Aktualisiere die clicks-Spalte für einen bestimmten Artikel
    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.clicks = :clicks WHERE s.artId = :artId")
    void updateClicksByArtId( Long clicks,  Long artId);

    // Aktualisiere die searchSucces-Spalte für einen bestimmten Artikel
    @Modifying
    @Query("UPDATE PostStats s SET s.searchSuccess = :searchSucces WHERE s.artId = :artId")
    void updateSearchSuccesByArtId( Long searchSucces, Long artId);

    // Aktualisiere die refferings-Spalte für einen bestimmten Artikel
    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.refferings = :refferings WHERE s.artId = :artId")
    void updateRefferingsByArtId( Long refferings,  Long artId);

    boolean existsByArtId(Long artId);

    @Query("SELECT s.searchSuccessRate FROM PostStats s WHERE s.artId = :artId")
    public Float getSearchSuccessRateByArtId( Long artId);

    // Get the article referring rate for a given article ID
    @Query("SELECT s.articleReferringRate FROM PostStats s WHERE s.artId = :artId")
    public Float getArticleReferringRateByArtId( Long artId);

    // Get the number of clicks for a given article ID
    @Query("SELECT s.clicks FROM PostStats s WHERE s.artId = :artId")
    public Long getClicksByArtId(Long artId);

    // Get the number of successful searches for a given article ID
    @Query("SELECT s.searchSuccess FROM PostStats s WHERE s.artId = :artId")
    public Long getSearchSuccesByArtId( Long artId);

    // Get the number of referrings for a given article ID
    @Query("SELECT s.refferings FROM PostStats s WHERE s.artId = :artId")
    public Long getReferringsByArtId( Long artId);
    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.clicks = :clicks , s.searchSuccess =:searchSuccess WHERE s.artId = :artId")
    void updateClicksAndSearchSuccess( Long artId,  Long clicks, Long searchSuccess);

    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.refferings = :refferings , s.articleReferringRate=:rate WHERE s.artId = :artId")
    void updateRefferingsAndRateByArtId( Float rate,Long refferings,  Long artId);

    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.clicks = :clicks , s.searchSuccess =:searchSuccess , s.performance=:performance WHERE s.artId = :artId")
    void updateClicksSearchSuccessPerformance( Long artId,  Long clicks, Long searchSuccess, float performance);

    @Transactional
    @Modifying
    @Query("UPDATE PostStats s SET s.clicks = :clicks , s.performance=:performance WHERE s.artId = :artId")
    void updateClicksAndPerformanceByArtId( Long clicks,  Long artId, float performance);

    @Modifying
    @Transactional
    @Query("UPDATE PostStats s SET s.clicks = :clicks, s.searchSuccess = :searchSuccess, s.performance = :performance, s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateClicksSearchSuccessAndRatePerformance(Long artId, Long clicks, Long searchSuccess, float performance, float searchSuccessRate);

    @Modifying
    @Transactional
    @Query("UPDATE PostStats s SET s.lettercount =:lettercount WHERE s.artId =:artId")
    void updateLetterCount(int lettercount, long artId);

    default void updateClicksSearchSuccessRateAndPerformance(Long artId, Long clicks, Long searchSuccess, float performance) {
        float searchSuccessRate = (float) clicks / searchSuccess;
        updateClicksSearchSuccessAndRatePerformance(artId, clicks, searchSuccess, performance, searchSuccessRate);
    }

    @Query("select s.performance from PostStats s where s.artId=:artId")
    public float getPerformanceByArtID(int artId);

    @Query("SELECT MAX(s.performance) FROM PostStats s")
    public float getMaxPerformance();

    @Query("SELECT MAX(s.relevance) FROM PostStats s")
    public float getMaxRelevance();

    @Query("SELECT s.viewsByLocation FROM PostStats s WHERE s.artId=:artId")
    public HashMap getViewsByLocation(int artId);

    @Query("SELECT s.viewsByLocation FROM PostStats s")
    public List<HashMap> getAllViewsByLocation();

    @Query("SELECT s.viewsPerHour FROM PostStats s WHERE s.artId=:artId")
    public HashMap getViewsPerHour(int artId);

    @Query("SELECT s.viewsPerHour FROM PostStats s")
    public List<HashMap> getAllViewsPerHour();

    @Query("SELECT s FROM PostStats s ORDER BY s.performance DESC LIMIT 5")
    public List<PostStats> getTop5Relevance();

    @Query("SELECT s FROM PostStats s ORDER BY s.performance DESC LIMIT 5")
    public List<PostStats> getTop5Performance();

    @Query("SELECT s.artId FROM PostStats s ORDER BY s.performance DESC")
    public List<Long> getTopRelevanceID(int limit);

    @Query("SELECT s.artId FROM PostStats s ORDER BY s.performance DESC")
    public List<Long> getTopPerformanceID(int limit);

    @Query("SELECT s.lettercount FROM PostStats s WHERE s.artId=:artId")
    public Integer getLetterCount(long artId);

    @Query("SELECT s.relevance FROM PostStats s WHERE s.artId=:artId")
    public float getRelevanceById(long artId);

    @Query("SELECT s.wordcount FROM PostStats s WHERE s.artId =:artId")
    public int getWordCount(int artId);

    @Modifying
    @Transactional
    @Query("UPDATE PostStats s SET s.wordcount =:wordcount WHERE s.artId =:artId")
    void updateWordCount(int wordcount, long artId);

    // Beispiel für eine separate Methode zur Berechnung der Performance


}
