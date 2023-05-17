package com.analysetool.repositories;

import com.analysetool.modells.stats;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.analysetool.modells.stats;

import java.util.List;
import java.util.Optional;

@Repository
public interface statsRepository extends JpaRepository<stats, Long> {

    @Query("Select S From stats S Where S.artId=:artid")
    stats getStatByArtID(long artid);


    @Modifying
    @Query("UPDATE stats s SET s.searchSuccessRate = :searchSuccessRate, s.articleReferringRate = :articleReferringRate, s.clicks = :clicks, s.searchSuccess = :searchSucces, s.refferings = :refferings WHERE s.artId = :artId")
    void updateStats(Long artId,  Float searchSuccessRate,  Float articleReferringRate,  Long clicks,Long searchSucces,  Long refferings);

    // Methode zum Aktualisieren der searchSuccessRate-Spalte
    @Modifying
    @Query("UPDATE stats s SET s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateSearchSuccessRate(Long artId, Float searchSuccessRate);

    // Methode zum Aktualisieren der articleReferringRate-Spalte
    @Modifying
    @Query("UPDATE stats s SET s.articleReferringRate = :articleReferringRate WHERE s.artId = :artId")
    void updateArticleReferringRate( Long artId,  Float articleReferringRate);

    // Methode zum Aktualisieren der clicks-Spalte
    @Transactional
    @Modifying
    @Query("UPDATE stats s SET s.clicks = :clicks WHERE s.artId = :artId")
    void updateClicks( Long artId,  Long clicks);

    // Methode zum Aktualisieren der searchSucces-Spalte
    @Modifying
    @Query("UPDATE stats s SET s.searchSuccess = :searchSuccess WHERE s.artId = :artId")
    void updateSearchSucces( Long artId,Long searchSuccess);

    // Methode zum Aktualisieren der refferings-Spalte
    @Modifying
    @Query("UPDATE stats s SET s.refferings = :refferings WHERE s.artId = :artId")
    void updateRefferings( Long artId,  Long refferings);

    List<stats> findByArtId(Long artId);

    // Zähle die Anzahl der Stats-Objekte anhand der artId
    Long countByArtId(Long artId);

    // Suche nach einem bestimmten Stats-Objekt anhand der artId
    Optional<stats> findOneByArtId(Long artId);

    // Aktualisiere die searchSuccessRate-Spalte für einen bestimmten Artikel
    @Modifying
    @Query("UPDATE stats s SET s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateSearchSuccessRateByArtId( Float searchSuccessRate,  Long artId);

    // Aktualisiere die articleReferringRate-Spalte für einen bestimmten Artikel
    @Modifying
    @Query("UPDATE stats s SET s.articleReferringRate = :articleReferringRate WHERE s.artId = :artId")
    void updateArticleReferringRateByArtId( Float articleReferringRate, Long artId);

    // Aktualisiere die clicks-Spalte für einen bestimmten Artikel
    @Transactional
    @Modifying
    @Query("UPDATE stats s SET s.clicks = :clicks WHERE s.artId = :artId")
    void updateClicksByArtId( Long clicks,  Long artId);

    // Aktualisiere die searchSucces-Spalte für einen bestimmten Artikel
    @Modifying
    @Query("UPDATE stats s SET s.searchSuccess = :searchSucces WHERE s.artId = :artId")
    void updateSearchSuccesByArtId( Long searchSucces, Long artId);

    // Aktualisiere die refferings-Spalte für einen bestimmten Artikel
    @Transactional
    @Modifying
    @Query("UPDATE stats s SET s.refferings = :refferings WHERE s.artId = :artId")
    void updateRefferingsByArtId( Long refferings,  Long artId);

    boolean existsByArtId(Long artId);

    @Query("SELECT s.searchSuccessRate FROM stats s WHERE s.artId = :artId")
    public Float getSearchSuccessRateByArtId( Long artId);

    // Get the article referring rate for a given article ID
    @Query("SELECT s.articleReferringRate FROM stats s WHERE s.artId = :artId")
    public Float getArticleReferringRateByArtId( Long artId);

    // Get the number of clicks for a given article ID
    @Query("SELECT s.clicks FROM stats s WHERE s.artId = :artId")
    public Long getClicksByArtId(Long artId);

    // Get the number of successful searches for a given article ID
    @Query("SELECT s.searchSuccess FROM stats s WHERE s.artId = :artId")
    public Long getSearchSuccesByArtId( Long artId);

    // Get the number of referrings for a given article ID
    @Query("SELECT s.refferings FROM stats s WHERE s.artId = :artId")
    public Long getReferringsByArtId( Long artId);
    @Transactional
    @Modifying
    @Query("UPDATE stats s SET s.clicks = :clicks , s.searchSuccess =:searchSuccess WHERE s.artId = :artId")
    void updateClicksAndSearchSuccess( Long artId,  Long clicks, Long searchSuccess);
}
