package com.analysetool.repositories;
import com.analysetool.modells.SearchStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchStatsRepository extends JpaRepository<SearchStats, Long> {
    // Hier können Sie benutzerdefinierte Methoden für spezifische Abfragen definieren
}
