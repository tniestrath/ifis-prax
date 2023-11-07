package com.analysetool.repositories;

import com.analysetool.modells.UserGeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGeoRepository extends JpaRepository<UserGeo, Long> {

}
