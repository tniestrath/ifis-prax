package com.analysetool.repositories;

import com.analysetool.modells.PostClicksByHourDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostClicksByHourDLCRepository extends JpaRepository<PostClicksByHourDLC, Integer> {

    List<PostClicksByHourDLC> findAllByPostIdAndUniId(Long postId, Integer uniId);
    List<PostClicksByHourDLC> findAllByPostIdAndUniIdIn(Long postId, List<Integer> uniId);
    List<PostClicksByHourDLC> findAllByPostIdInAndUniIdIn(List<Long> postId, List<Integer> uniId);

    /*
        Throws an Error during testing.
    @Query("SELECT SUM(p.clicks) FROM PostClicksByHourDLC p WHERE p.postId IN :userId AND p.uniId IN :uniIds")
    Long sumClicksByPostIdInAndUniIdIn(@Param("postId") List<Long> postId, @Param("uniIds") List<Integer> uniIds);
    */


    @Query("SELECT DISTINCT p.uniId FROM PostClicksByHourDLC p WHERE p.id IN :Ids ORDER BY p.uniId DESC")
    List<Integer> getAvailableUniIdIn(@Param("Ids") List<Integer> Ids);
}

