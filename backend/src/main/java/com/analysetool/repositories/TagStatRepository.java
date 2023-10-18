package com.analysetool.repositories;


import com.analysetool.modells.TagStat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagStatRepository extends JpaRepository<TagStat, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE TagStat s SET s.views = :clicks , s.searchSuccess =:searchSuccess , s.performance=:performance WHERE s.tagId =:tagId")
    void updateClicksSearchSuccessPerformance( Long tagId,  Long clicks, Long searchSuccess, float performance);

    @Transactional
    @Modifying
    @Query("UPDATE TagStat s SET s.views = :clicks , s.performance=:performance WHERE s.tagId =:tagId")
    void updateClicksAndPerformanceByArtId( Long clicks,  Long tagId, float performance);

    @Query("select s.performance from TagStat s where s.tagId=:tagId")
    public float getPerformanceByArtID(int tagId);

    @Query("SELECT MAX(s.performance) FROM TagStat s")
    public float getMaxPerformance();

    @Query("SELECT s FROM TagStat s WHERE s.tagId =:id")
    public TagStat getStatById(int id);

    public boolean existsByTagId(int tagId);

    @Query("SELECT s.tagId FROM TagStat s ORDER BY s.relevance DESC LIMIT 3")
    public List<Long> getTop3Relevance();

    @Query("SELECT s.tagId FROM TagStat s ORDER BY s.performance DESC LIMIT 3")
    public List<Long> getTop3Performance();

    @Query("SELECT MAX(t.relevance) FROM TagStat t")
    public int getMaxRelevance();
}

