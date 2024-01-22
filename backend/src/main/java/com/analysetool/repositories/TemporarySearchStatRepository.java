package com.analysetool.repositories;

import com.analysetool.modells.TemporarySearchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporarySearchStatRepository extends JpaRepository<TemporarySearchStat, Long> {

}
