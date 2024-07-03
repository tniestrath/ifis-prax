package com.analysetool.repositories;

import com.analysetool.modells.UniversalStatsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UniversalStatsHourlyRepository extends JpaRepository<UniversalStatsHourly, Integer> {

    @Query("SELECT u FROM UniversalStatsHourly u WHERE u.stunde =:stunde AND u.uniStatId =:uniStatId")
    UniversalStatsHourly getByStundeAndUniStatId(int stunde, int uniStatId);

    @Query("SELECT u FROM UniversalStatsHourly u ORDER BY u.id DESC LIMIT 1")
    UniversalStatsHourly getLast();

    @Query("SELECT u FROM UniversalStatsHourly u ORDER BY u.id DESC LIMIT 24")
    List<UniversalStatsHourly> getLast24();

    @Query("SELECT SUM(u.besucherAnzahl) FROM UniversalStatsHourly u WHERE u.uniStatId=:uniStatId")
    long getSumUsersForUniId(int uniStatId);

    @Query("SELECT u.stunde FROM UniversalStatsHourly u ORDER BY u.id DESC LIMIT 1")
    int getLastStunde();

    @Query("SELECT u FROM UniversalStatsHourly  u")
    List<UniversalStatsHourly> getAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM UniversalStatsHourly u WHERE u.id > 0")
    void truncate();

    @Query("SELECT AVG(u.serverErrors) FROM UniversalStatsHourly u")
    double getAverageServerErrors();

    @Query("SELECT AVG(u.internalClicks) FROM UniversalStatsHourly u")
    double getAverageInternalClicks();

    @Query("SELECT SUM(u.serverErrors) FROM UniversalStatsHourly u")
    long getTotalServerErrors();

    @Query("SELECT SUM(u.internalClicks) FROM UniversalStatsHourly u")
    long getTotalInternalClicks();

    @Query("SELECT u.stunde, SUM(u.serverErrors) as totalErrors FROM UniversalStatsHourly u WHERE u.uniStatId = :uniId GROUP BY u.stunde ORDER BY totalErrors DESC")
    List<Object[]> getHourlyServerErrorRanking( int uniId);

    @Query("SELECT u.stunde, SUM(u.internalClicks) as totalClicks FROM UniversalStatsHourly u WHERE u.uniStatId = :uniId GROUP BY u.stunde ORDER BY totalClicks DESC")
    List<Object[]> getHourlyInternalClicksRanking(int uniStatId);

    @Query("SELECT SUM(u.totalClicks), SUM(u.serverErrors) FROM UniversalStatsHourly u WHERE u.uniStatId = :uniStatId")
    List<Object[]> getTotalClicksAndErrorsForDay(int uniStatId);


}
