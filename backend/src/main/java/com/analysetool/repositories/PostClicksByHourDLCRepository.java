package com.analysetool.repositories;

import com.analysetool.modells.PostClicksByHourDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostClicksByHourDLCRepository extends JpaRepository<PostClicksByHourDLC, Integer> {

    List<PostClicksByHourDLC> findAllByPostIdAndUniId(Long postId, Integer uniId);
}

