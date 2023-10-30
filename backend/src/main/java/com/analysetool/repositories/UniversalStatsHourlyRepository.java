package com.analysetool.repositories;

import com.analysetool.modells.UniversalStatsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UniversalStatsHourlyRepository extends JpaRepository<UniversalStatsHourly, Integer> {

    @Query("SELECT u FROM UniversalStatsHourly u WHERE u.stunde =:stunde AND u.uniStatId =:uniStatId")
    public UniversalStatsHourly getByStundeAndUniStatId(int stunde, int uniStatId);

    @Query("SELECT u FROM UniversalStatsHourly u ORDER BY u.id DESC LIMIT 1")
    public UniversalStatsHourly getLast();

    @Query("SELECT u FROM UniversalStatsHourly u ORDER BY u.id DESC LIMIT 24")
    public List<UniversalStatsHourly> getLast24();

    @Query("SELECT u.stunde FROM UniversalStatsHourly u ORDER BY u.id DESC LIMIT 1")
    public int getLastStunde();

    @Query("SELECT u FROM UniversalStatsHourly  u")
    public List<UniversalStatsHourly> getAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM UniversalStatsHourly u WHERE u.id > 0")
    void truncate();

}
