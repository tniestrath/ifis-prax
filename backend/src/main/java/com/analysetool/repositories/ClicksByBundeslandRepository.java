package com.analysetool.repositories;

import com.analysetool.modells.ClicksByBundesland;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClicksByBundeslandRepository extends JpaRepository<ClicksByBundesland, Long> {

    @Query("SELECT u FROM ClicksByBundesland u WHERE u.uniStatId =:uniStatId AND u.bundesland=:bundesland")
    public ClicksByBundesland getByUniIDAndBundesland(int uniStatId, String bundesland);

    @Query("SELECT u FROM ClicksByBundesland u WHERE u.uniStatId =:uniStatId")
    List<ClicksByBundesland> getByUniID(int uniStatId);
}
