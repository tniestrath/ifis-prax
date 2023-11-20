package com.analysetool.repositories;

import com.analysetool.modells.ClicksByBundeslandCitiesDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClicksByBundeslandCitiesDLCRepository extends JpaRepository<ClicksByBundeslandCitiesDLC, Long> {

    @Query("SELECT u FROM ClicksByBundeslandCitiesDLC u WHERE u.uni_id =:uniStatId AND u.bundesland=:bundesland")
    ClicksByBundeslandCitiesDLC getByUniIDAndBundesland(int uniStatId, String bundesland);

}
