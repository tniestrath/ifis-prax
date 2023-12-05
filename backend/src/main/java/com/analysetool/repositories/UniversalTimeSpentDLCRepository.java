package com.analysetool.repositories;

import com.analysetool.modells.UniversalTimeSpentDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversalTimeSpentDLCRepository extends JpaRepository<UniversalTimeSpentDLC, Integer> {

}
