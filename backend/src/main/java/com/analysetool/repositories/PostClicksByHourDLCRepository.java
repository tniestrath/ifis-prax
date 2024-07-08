package com.analysetool.repositories;

import com.analysetool.modells.PostClicksByHourDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostClicksByHourDLCRepository extends JpaRepository<PostClicksByHourDLC, Integer> {

    List<PostClicksByHourDLC> findAllByPostIdAndUniId(Long postId, Integer uniId);
    List<PostClicksByHourDLC> findAllByPostIdAndUniIdIn(Long postId, List<Integer> uniId);
    List<PostClicksByHourDLC> findAllByPostIdInAndUniIdIn(List<Long> postId, List<Integer> uniId);

    @Query("SELECT DISTINCT p.uniId FROM PostClicksByHourDLC p WHERE p.postId =:postId ORDER BY p.uniId ASC")
    List<Long> getUniIdsForPost(long postId);

    @Query("SELECT SUM(p.clicks) FROM PostClicksByHourDLC p WHERE p.uniId=:uniId AND p.postId=:postId")
    Optional<Integer> getSumForDayForPost(long uniId, long postId);


    @Query("SELECT DISTINCT p.uniId FROM PostClicksByHourDLC p WHERE p.id IN :Ids ORDER BY p.uniId DESC")
    List<Integer> getAvailableUniIdIn(@Param("Ids") List<Integer> Ids);

    @Query("SELECT p.uniId FROM PostClicksByHourDLC p ORDER BY p.uniId ASC LIMIT 1")
    Long findOldestUni();


    @Query("SELECT SUM(u.clicks) FROM PostClicksByHourDLC u WHERE u.uniId > (SELECT uh.uniId - 91 FROM PostClicksByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1) AND u.postId IN (SELECT p.id FROM Post p WHERE p.authorId=:userId)")
    Long getSumForUserThisQuarter(int userId);

    @Query("SELECT SUM(u.clicks) FROM PostClicksByHourDLC u WHERE u.uniId < (SELECT uh.uniId - 91 FROM PostClicksByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1) AND u.uniId > (SELECT uh.uniId - 182 FROM PostClicksByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1) AND u.postId IN (SELECT p.id FROM Post p WHERE p.authorId=:userId)")
    Long getSumForUserPreviousQuarter(int userId);
}

