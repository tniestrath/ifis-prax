package com.analysetool.repositories;

import com.analysetool.modells.TemporarySearchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public interface TemporarySearchStatRepository extends JpaRepository<TemporarySearchStat, Long> {


    @Query("SELECT t from TemporarySearchStat t ")
    CopyOnWriteArrayList<TemporarySearchStat> findAllConcurrent();
}
