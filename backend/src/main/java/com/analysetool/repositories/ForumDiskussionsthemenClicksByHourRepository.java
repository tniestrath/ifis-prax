package com.analysetool.repositories;

import com.analysetool.modells.ForumDiskussionsthemenClicksByHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumDiskussionsthemenClicksByHourRepository extends JpaRepository<ForumDiskussionsthemenClicksByHour, Integer> {

    List<ForumDiskussionsthemenClicksByHour> findByUniId(Integer uniId);
    List<ForumDiskussionsthemenClicksByHour> findByForumId(Long forumId);
    List<ForumDiskussionsthemenClicksByHour> findByHour(Integer hour);

    ForumDiskussionsthemenClicksByHour findByUniIdAndHourAndForumId(Integer uniId,Integer hour,Long forumId);
    @Query("Select sum(f.clicks) From ForumDiskussionsthemenClicksByHour f Where f.id=:forumId ")
    Long getClicksAllTimeById(Long forumId);

    @Query("Select sum(f.clicks) From ForumDiskussionsthemenClicksByHour f Where f.id=:forumI AND f.uniId =: uniId")
    Long getClicksOfDay(Long forumId,Integer uniId);

}
