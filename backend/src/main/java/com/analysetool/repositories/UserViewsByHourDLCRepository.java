package com.analysetool.repositories;

import com.analysetool.modells.UserViewsByHourDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserViewsByHourDLCRepository extends JpaRepository<UserViewsByHourDLC, Long> {

}