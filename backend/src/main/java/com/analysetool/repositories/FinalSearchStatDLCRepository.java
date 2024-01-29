package com.analysetool.repositories;

import com.analysetool.modells.FinalSearchStatDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinalSearchStatDLCRepository extends JpaRepository<FinalSearchStatDLC, Long> {

}
