package com.analysetool.repositories;

import com.analysetool.modells.FinalSearchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinalSearchStatRepository extends JpaRepository<FinalSearchStat, Long> {
    // Hier können Sie bei Bedarf benutzerdefinierte Abfragemethoden hinzufügen
}
