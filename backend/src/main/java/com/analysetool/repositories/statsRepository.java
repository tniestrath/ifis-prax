package com.analysetool.repositories;

import com.analysetool.modells.stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.analysetool.modells.stats;

import java.util.List;
import java.util.Optional;

@Repository
public interface statsRepository extends JpaRepository<stats, Long> {

    @Query("Select S From stats Where artId=:artid")
    stats getStatByArtID(long artid);



    @Query("UPDATE stats s SET s.searchSuccessRate = :searchSuccessRate, s.articleReferringRate = :articleReferringRate, s.clicks = :clicks, s.searchSucces = :searchSucces, s.refferings = :refferings WHERE s.artId = :artId")
    void updateStats(Long artId,  Float searchSuccessRate,  Float articleReferringRate,  Long clicks,Long searchSucces,  Long refferings);

    // Methode zum Aktualisieren der searchSuccessRate-Spalte

    @Query("UPDATE stats s SET s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateSearchSuccessRate(Long artId, Float searchSuccessRate);

    // Methode zum Aktualisieren der articleReferringRate-Spalte

    @Query("UPDATE stats s SET s.articleReferringRate = :articleReferringRate WHERE s.artId = :artId")
    void updateArticleReferringRate( Long artId,  Float articleReferringRate);

    // Methode zum Aktualisieren der clicks-Spalte

    @Query("UPDATE stats s SET s.clicks = :clicks WHERE s.artId = :artId")
    void updateClicks( Long artId,  Long clicks);

    // Methode zum Aktualisieren der searchSucces-Spalte

    @Query("UPDATE stats s SET s.searchSucces = :searchSucces WHERE s.artId = :artId")
    void updateSearchSucces( Long artId,Long searchSucces);

    // Methode zum Aktualisieren der refferings-Spalte

    @Query("UPDATE stats s SET s.refferings = :refferings WHERE s.artId = :artId")
    void updateRefferings( Long artId,  Long refferings);

    List<stats> findByArtId(Long artId);

    // Zähle die Anzahl der Stats-Objekte anhand der artId
    Long countByArtId(Long artId);

    // Suche nach einem bestimmten Stats-Objekt anhand der artId
    Optional<stats> findOneByArtId(Long artId);

    // Aktualisiere die searchSuccessRate-Spalte für einen bestimmten Artikel

    @Query("UPDATE Stats s SET s.searchSuccessRate = :searchSuccessRate WHERE s.artId = :artId")
    void updateSearchSuccessRateByArtId( Float searchSuccessRate,  Long artId);

    // Aktualisiere die articleReferringRate-Spalte für einen bestimmten Artikel

    @Query("UPDATE Stats s SET s.articleReferringRate = :articleReferringRate WHERE s.artId = :artId")
    void updateArticleReferringRateByArtId( Float articleReferringRate, Long artId);

    // Aktualisiere die clicks-Spalte für einen bestimmten Artikel

    @Query("UPDATE Stats s SET s.clicks = :clicks WHERE s.artId = :artId")
    void updateClicksByArtId( Long clicks,  Long artId);

    // Aktualisiere die searchSucces-Spalte für einen bestimmten Artikel

    @Query("UPDATE Stats s SET s.searchSucces = :searchSucces WHERE s.artId = :artId")
    void updateSearchSuccesByArtId( Long searchSucces, Long artId);

    // Aktualisiere die refferings-Spalte für einen bestimmten Artikel

    @Query("UPDATE Stats s SET s.refferings = :refferings WHERE s.artId = :artId")
    void updateRefferingsByArtId( Long refferings,  Long artId);

    boolean existsByArtId(Long artId);

}
