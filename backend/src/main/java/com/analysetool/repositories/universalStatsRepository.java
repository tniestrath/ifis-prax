package com.analysetool.repositories;

import com.analysetool.modells.UniversalStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface universalStatsRepository extends JpaRepository<UniversalStats, Integer> {

    @Override
    Optional<UniversalStats> findById(Integer integer);

    // Hier können Sie benutzerdefinierte Abfragen hinzufügen, wenn nötig
    Optional<UniversalStats> findByDatum(Date Datum);

    List<UniversalStats> getAllByDatumAfter(Date datum);

    List<UniversalStats> findTop7ByOrderByDatumDesc();

    UniversalStats findTop1ByOrderByDatumDesc();


}

