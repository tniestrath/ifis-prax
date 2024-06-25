package com.analysetool.repositories;

import com.analysetool.modells.LastPing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastPingRepository extends JpaRepository<LastPing, Long> {
}
