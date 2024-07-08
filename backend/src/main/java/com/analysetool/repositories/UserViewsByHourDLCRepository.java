package com.analysetool.repositories;

import com.analysetool.modells.UserViewsByHourDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserViewsByHourDLCRepository extends JpaRepository<UserViewsByHourDLC, Long> {




    List<UserViewsByHourDLC> findByUserIdAndUniId(long userId, int uniId);
    List<UserViewsByHourDLC> findByUserIdAndUniIdIn(long userId, List<Integer> uniId);
    @Query("SELECT SUM(u.views) FROM UserViewsByHourDLC u WHERE u.userId = :userId AND u.uniId IN :uniIds")
    Long sumViewsByUserIdAndUniIdIn(@Param("userId") long userId, @Param("uniIds") List<Integer> uniIds);

    @Query("SELECT u FROM UserViewsByHourDLC u WHERE u.userId=:userId ORDER BY u.uniId, u.hour ASC")
    List<UserViewsByHourDLC> findByUserId(long userId);

    @Query("SELECT SUM(u.views) FROM UserViewsByHourDLC u WHERE u.uniId=:uniId AND u.userId=:userId")
    Integer getSumByUniIdAndUserId(long uniId, long userId);

    @Query("SELECT DISTINCT u.uniId FROM UserViewsByHourDLC u WHERE u.userId=:userId")
    List<Integer> getUniIdsForUser(long userId);

    boolean existsByUserId(long userId);

    @Query("SELECT u.uniId FROM UserViewsByHourDLC u WHERE u.userId=:userId ORDER BY u.uniId ASC LIMIT 1")
    long getFirstUniIdByUserid(long userId);

    @Query("SELECT u.uniId FROM UserViewsByHourDLC u ORDER BY u.uniId DESC LIMIT 1")
    long getLastUniId();

    @Query("SELECT u.uniId FROM UserViewsByHourDLC u ORDER BY u.uniId DESC LIMIT 7")
    List<Integer> getLast7Uni();


    @Query("SELECT DISTINCT u.uniId FROM UserViewsByHourDLC u WHERE u.id IN :Ids ORDER BY u.uniId DESC")
    List<Integer> getAvailableUniIdIn(@Param("Ids") List<Integer> Ids);

    @Query("SELECT SUM(u.views) FROM UserViewsByHourDLC u WHERE u.userId=:userId")
    Long getSumForUser(long userId);

    @Query("SELECT SUM(u.views) FROM UserViewsByHourDLC u WHERE u.userId=:userId AND u.uniId > (SELECT uh.uniId - 91 FROM UserViewsByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1)")
    Long getSumForUserThisQuarter(int userId);

    @Query("SELECT SUM(u.views) FROM UserViewsByHourDLC u WHERE u.userId=:userId AND u.uniId < (SELECT uh.uniId - 91 FROM UserViewsByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1) AND u.uniId > (SELECT uh.uniId - 182 FROM UserViewsByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1)")
    Long getSumForUserPreviousQuarter(int userId);

}