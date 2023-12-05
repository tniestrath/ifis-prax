package com.analysetool.repositories;

import com.analysetool.modells.UniversalAverageClicksDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversalAverageClicksDLCRepository extends JpaRepository<UniversalAverageClicksDLC, Integer> {

}
