package com.analysetool.repositories;
import com.analysetool.modells.SearchStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SearchStatsRepository extends JpaRepository<SearchStats, Long> {
    // Hier können Sie benutzerdefinierte Methoden für spezifische Abfragen definieren


    Optional<SearchStats> findByIpHashedAndSearchTime(String ipHashed, LocalDateTime SearchTime);


    Optional<SearchStats> findByIpHashedAndSearchStringAndSearchSuccessFlag(String ipHashed, String SearchString,boolean SearchSuccessFlag);

    @Query("SELECT s FROM SearchStats s WHERE DATE(s.searchTime) = :date")
    List<SearchStats> findAllBySearchDate(@Param("date") LocalDate date);

}
