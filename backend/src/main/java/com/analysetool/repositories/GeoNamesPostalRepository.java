package com.analysetool.repositories;

import com.analysetool.modells.GeoNamesPostal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoNamesPostalRepository extends JpaRepository<GeoNamesPostal, Long> {

    @Query("SELECT c.placeName FROM GeoNamesPostal c WHERE c.postalCode=:plz")
    String getCityByPlz(String plz);
}
