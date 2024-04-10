package com.analysetool.repositories;

import com.analysetool.modells.ClicksByCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClicksByCountryRepository extends JpaRepository<ClicksByCountry, Long> {

    @Query("SELECT u FROM ClicksByCountry u WHERE u.uniStatId =:uniStatId AND u.country=:country")
    ClicksByCountry getByUniIDAndCountry(int uniStatId, String country);

    @Query("SELECT u FROM ClicksByCountry u WHERE u.uniStatId =:uniStatId")
    List<ClicksByCountry> getByUniID(int uniStatId);

    @Query("SELECT SUM(u.clicks) FROM ClicksByCountry u WHERE u.uniStatId=:uniStatId")
    int getClicksAusland(int uniStatId);
}
