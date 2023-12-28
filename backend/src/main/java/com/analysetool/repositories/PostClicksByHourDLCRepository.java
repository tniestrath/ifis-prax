package com.analysetool.repositories;

import com.analysetool.modells.PostClicksByHourDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostClicksByHourDLCRepository extends JpaRepository<PostClicksByHourDLC, Integer> {

}

