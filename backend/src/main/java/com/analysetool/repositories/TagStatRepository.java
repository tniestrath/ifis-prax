package com.analysetool.repositories;


import com.analysetool.modells.TagStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagStatRepository extends JpaRepository<TagStat, Long> {

    @Query("SELECT s FROM TagStat s WHERE s.tagId =:id")
    List<TagStat> getStatById(int id);

    boolean existsByTagId(int tagId);

    @Query("SELECT t FROM TagStat t WHERE t.tagId=:tagId AND t.uniId=:uniId AND t.hour=:hour")
    Optional<TagStat> getByTagIdDayAndHour(long tagId, int uniId, int hour);

    @Query("SELECT s.tagId FROM TagStat s WHERE s.uniId + 7 >= (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1) ORDER BY s.views DESC LIMIT 3")
    List<Long> getTop3Relevance();

    @Query("SELECT SUM(t.views) AS relevance FROM TagStat t WHERE t.uniId + 7 >= (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1) ORDER BY relevance DESC LIMIT 1")
    int getMaxRelevance();

    @Query("SELECT SUM(t.views) FROM TagStat t WHERE t.tagId =:tagId AND  t.uniId + 7 >= (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1)")
    double getRelevance(int tagId);

    @Query("SELECT SUM(t.views) FROM TagStat t WHERE t.tagId=:id AND t.uniId + :daysBack = (SELECT u.id FROM UniversalStats u ORDER BY u.id DESC LIMIT 1)")
    Integer getViewsDaysBack(int id, int daysBack);

    @Query("SELECT SUM(t.views) FROM TagStat t WHERE t.tagId=:tagId AND t.uniId=:uniId")
    Integer getViewsByTagIdAndUniId(int tagId, int uniId);

    @Query("SELECT SUM(t.views) FROM TagStat t WHERE t.tagId=:tagId")
    Integer getSumOfViewsForTag(int tagId);

    @Query("SELECT t.uniId FROM TagStat t ORDER BY t.uniId ASC LIMIT 1")
    int getEarliestTrackingForTag(int tagId);

    @Query("SELECT ts.tagId FROM TagStat AS ts LEFT JOIN TagCatStat AS tcs ON ts.tagId=tcs.tagId AND ts.uniId=tcs.uniId AND ts.hour=tcs.hour JOIN WPTerm t ON ts.tagId=t.id WHERE (t.name LIKE %:search%) OR (t.slug LIKE %:search%) GROUP BY ts.tagId ORDER BY (SUM(ts.views) + SUM(tcs.views)) DESC")
    List<Long> getOrderedByTotalViews(String search, Pageable pageable);

}

