package com.analysetool.repositories;

import com.analysetool.modells.UniversalAverageClicksDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversalAverageClicksDLCRepository extends JpaRepository<UniversalAverageClicksDLC, Integer> {

    @Query("SELECT u FROM UniversalAverageClicksDLC u ORDER BY u.uni_stat_id DESC LIMIT 1")
    UniversalAverageClicksDLC getLatest();

}
