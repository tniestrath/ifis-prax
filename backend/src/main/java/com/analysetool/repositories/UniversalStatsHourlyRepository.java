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

}
