package com.analysetool.repositories;

import com.analysetool.modells.UniqueUsers;
import com.analysetool.modells.WPUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface UniqueUserRepository extends JpaRepository<UniqueUsers, Long> {

    @Query("SELECT s.access_time FROM UniqueUsers s WHERE s.ip_hashed = :ip")
    public List<LocalDateTime> getAccessTimesByIPHash(String ip);

    @Query("SELECT DISTINCT s.ip_hashed FROM UniqueUsers s")
    public List<String> getAllIPs();



}
