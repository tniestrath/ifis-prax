package com.analysetool.repositories;

import com.analysetool.modells.FinalSearchStat;
import com.analysetool.modells.FinalSearchStatDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinalSearchStatRepository extends JpaRepository<FinalSearchStat, Long> {
    // Hier können Sie bei Bedarf benutzerdefinierte Abfragemethoden hinzufügen
    List<FinalSearchStat> findAllByCity(String city);

    List<FinalSearchStat> findAllByState(String state);

    List<FinalSearchStat> findAllByCountry(String country);
}
