package com.analysetool.repositories;

import com.analysetool.modells.TrackingBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingBlacklistRepository extends JpaRepository<TrackingBlacklist, Long> {

    @Query("SELECT t.ip FROM TrackingBlacklist t")
    List<String> getAllIps();

    @Query("SELECT t FROM TrackingBlacklist t WHERE t.ip=:ip")
    Optional<TrackingBlacklist> findByIp(String ip);
}
