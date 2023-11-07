package com.analysetool.repositories;

import com.analysetool.modells.ClicksByBundesland;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClicksByBundeslandRepository extends JpaRepository<ClicksByBundesland, Long> {

}
