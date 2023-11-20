package com.analysetool.repositories;

import com.analysetool.modells.ClicksByBundeslandCitiesDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClicksByBundeslandCitiesDLCRepository extends JpaRepository<ClicksByBundeslandCitiesDLC, Long> {

    @Query("SELECT u FROM ClicksByBundeslandCitiesDLC u WHERE u.uni_id =:uniStatId AND u.bundesland=:bundesland")
    List<ClicksByBundeslandCitiesDLC> getByUniIDAndBundesland(int uniStatId, String bundesland);

    @Query("SELECT u FROM ClicksByBundeslandCitiesDLC u WHERE u.uni_id =:uniStatId AND u.bundesland=:bundesland AND u.city=:city")
    ClicksByBundeslandCitiesDLC getByUniIDAndBundeslandAndCity(int uniStatId, String bundesland, String city);

    @Query("SELECT u FROM ClicksByBundeslandCitiesDLC u WHERE u.bundesland=:bundesland")
    List<ClicksByBundeslandCitiesDLC> getByBundesland(String bundesland);

    @Query("SELECT COUNT(DISTINCT(u.uni_id)) FROM ClicksByBundeslandCitiesDLC u")
    int getCountDays();

}
