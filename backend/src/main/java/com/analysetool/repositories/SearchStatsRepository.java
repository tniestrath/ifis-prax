package com.analysetool.repositories;
import com.analysetool.modells.SearchStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SearchStatsRepository extends JpaRepository<SearchStats, Long> {
    // Hier können Sie benutzerdefinierte Methoden für spezifische Abfragen definieren


    Optional<SearchStats> findByIpHashedAndAndSearchTime(String ipHashed, LocalDateTime SearchTime);


    Optional<SearchStats> findByIpHashedAndAndSearchString(String ipHashed, String SearchString);
}
