package com.analysetool.repositories;

import com.analysetool.modells.ClicksByBundesland;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClicksByBundeslandRepository extends JpaRepository<ClicksByBundesland, Long> {

    @Query("SELECT u FROM ClicksByBundesland u WHERE u.uniStatId =:uniStatid AND u.bundesland=:bundesland")
    public ClicksByBundesland getByUniIDAndBundesland(int uniStatId, String bundesland);
}
