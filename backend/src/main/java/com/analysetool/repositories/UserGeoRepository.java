package com.analysetool.repositories;

import com.analysetool.modells.UserGeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGeoRepository extends JpaRepository<UserGeo, Long> {

    @Query("SELECT u FROM UserGeo  u WHERE u.user_id=:userId")
    public UserGeo findByUserId(long userId);
}
