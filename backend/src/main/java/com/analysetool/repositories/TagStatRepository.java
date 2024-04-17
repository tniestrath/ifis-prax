package com.analysetool.repositories;


import com.analysetool.modells.TagStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagStatRepository extends JpaRepository<TagStat, Long> {

    @Query("SELECT s FROM TagStat s WHERE s.tagId =:id")
    TagStat getStatById(int id);

    boolean existsByTagId(int tagId);

    @Query("SELECT t FROM TagStat t WHERE t.tagId=:tagId AND t.uniId=:uniId AND t.hour=:hour")
    Optional<TagStat> getByTagIdDayAndHour(long tagId, int uniId, int hour);

    @Query("SELECT s.tagId FROM TagStat s WHERE s.uniId + 7 >= (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1) ORDER BY s.views DESC LIMIT 3")
    List<Long> getTop3Relevance();

    @Query("SELECT SUM(t.views) AS relevance FROM TagStat t WHERE t.uniId + 7 >= (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1) ORDER BY relevance DESC LIMIT 1")
    int getMaxRelevance();

    @Query("SELECT SUM(t.views) FROM TagStat t WHERE t.uniId + 7 >= (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1)")
    double getRelevance(int tagId);

    @Query("SELECT SUM(t.views) FROM TagStat t WHERE t.uniId + :daysBack >= (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1)")
    int getViewsDaysBack(int daysBack);

}

