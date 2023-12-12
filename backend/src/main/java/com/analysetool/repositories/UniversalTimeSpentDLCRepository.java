package com.analysetool.repositories;

import com.analysetool.modells.UniversalTimeSpentDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversalTimeSpentDLCRepository extends JpaRepository<UniversalTimeSpentDLC, Integer> {

    @Query("SELECT u FROM UniversalTimeSpentDLC u ORDER BY u.uni_stat_id DESC LIMIT 1")
    UniversalTimeSpentDLC getLatest();

}
