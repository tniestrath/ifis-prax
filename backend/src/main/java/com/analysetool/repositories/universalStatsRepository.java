package com.analysetool.repositories;
import com.analysetool.modells.universalStats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface universalStatsRepository extends JpaRepository<universalStats, Integer> {

    @Override
    Optional<universalStats> findById(Integer integer);

    // Hier können Sie benutzerdefinierte Abfragen hinzufügen, wenn nötig
    Optional<universalStats> findByDatum(Date Datum);


    List<universalStats> findTop7ByOrderByDatumDesc();

}

