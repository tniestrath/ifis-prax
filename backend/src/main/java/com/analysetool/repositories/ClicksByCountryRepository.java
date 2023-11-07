package com.analysetool.repositories;

import com.analysetool.modells.ClicksByCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClicksByCountryRepository extends JpaRepository<ClicksByCountry, Long> {

    @Query("SELECT u FROM ClicksByCountry u WHERE u.uniStatId =:id AND u.country=:country")
    public ClicksByCountry getByUniIDAndCountry(int uniStatId, String country);
}
