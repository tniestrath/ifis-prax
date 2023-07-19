package com.analysetool.repositories;


import com.analysetool.modells.TagStat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
//import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

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

    //ToDo Toten Code aufräumen
    /*@Modifying
    @Transactional
    @Query("UPDATE TagStat  s SET s.views =:clicks, s.searchSuccess =:searchSuccess, s.performance =:performance, s.searchSuccessRate =:searchSuccessRate WHERE s.artId = :artId")
    void updateClicksSearchSuccessAndRatePerformance(Long artId, Long clicks, Long searchSuccess, float performance, float searchSuccessRate);

    default void updateClicksSearchSuccessRateAndPerformance(Long artId, Long clicks, Long searchSuccess, float performance) {
        float searchSuccessRate = (float) clicks / searchSuccess;
        updateClicksSearchSuccessAndRatePerformance(artId, clicks, searchSuccess, performance, searchSuccessRate);
    }*/

    @Query("select s.performance from TagStat s where s.tagId=:tagId")
    public float getPerformanceByArtID(int tagId);

    @Query("SELECT MAX(s.performance) FROM TagStat s")
    public float getMaxPerformance();

    @Query("Select s from TagStat s Where s.tagId =:id")
    public TagStat getStatById(int id);

    public boolean existsByTagId(int tagId);
}

