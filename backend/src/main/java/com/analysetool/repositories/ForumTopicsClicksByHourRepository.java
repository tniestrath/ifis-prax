package com.analysetool.repositories;

import com.analysetool.modells.ForumTopicsClicksByHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumTopicsClicksByHourRepository extends JpaRepository<ForumTopicsClicksByHour, Integer> {

    List<ForumTopicsClicksByHour> findByUniId(Integer uniId);
    List<ForumTopicsClicksByHour> findByTopicId(Long topicId);
    List<ForumTopicsClicksByHour> findByHour(Integer hour);

    ForumTopicsClicksByHour findByUniIdAndHourAndTopicId(Integer uniId, Integer hour, Long topicId);

    @Query("SELECT SUM(f.clicks) FROM ForumTopicsClicksByHour f WHERE f.topicId =:topicId")
    Long getTotalClicksByTopicId(Long topicId);

    @Query("SELECT SUM(f.clicks) FROM ForumTopicsClicksByHour f WHERE f.topicId = :topicId AND f.uniId =:uniId")
    Long getClicksByTopicAndUniId(Long topicId, Integer uniId);
}
