package com.analysetool.repositories;

import com.analysetool.modells.SystemLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SystemLoadRepository extends JpaRepository<SystemLoad, Long> {
    List<SystemLoad> findAll();
    //SystemLoad findTopByCpuLoad();
    //SystemLoad findTopByMemoryLoad();

    @Query("SELECT s FROM SystemLoad s WHERE s.id = MAX (s.id)")
    SystemLoad getNetworkNow();

    List<SystemLoad> getTop60ByOrderByTimestampDesc();

    void deleteByTimestampBefore(long timestamp);
}

