package com.analysetool.repositories;

import com.analysetool.modells.SystemLoad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemLoadRepository extends JpaRepository<SystemLoad, Long> {
    List<SystemLoad> findAll();
    //SystemLoad findTopByCpuLoad();
    //SystemLoad findTopByMemoryLoad();

    List<SystemLoad> getTop60ByOrderByTimestampDesc();

    void deleteByTimestampBefore(long timestamp);
}

