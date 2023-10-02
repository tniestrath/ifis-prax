package com.analysetool.repositories;

import com.analysetool.modells.UniversalStats;
import com.analysetool.modells.UniversalStatsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UniversalStatsHourlyRepository extends JpaRepository<UniversalStatsHourly, Integer> {

    @Query("SELECT u FROM UniversalStatsHourly u WHERE u.stunde =:stunde")
    public UniversalStatsHourly getByStunde(int stunde);

    @Query("SELECT u FROM UniversalStatsHourly  u")
    public List<UniversalStatsHourly> getAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM UniversalStatsHourly u WHERE u.id > 0")
    void truncate();

}
