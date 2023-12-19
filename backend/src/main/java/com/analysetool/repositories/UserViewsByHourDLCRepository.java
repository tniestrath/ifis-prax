package com.analysetool.repositories;

import com.analysetool.modells.UserViewsByHourDLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserViewsByHourDLCRepository extends JpaRepository<UserViewsByHourDLC, Long> {




    List<UserViewsByHourDLC> findByUserIdAndUniId(long userId, int uniId);

    List<UserViewsByHourDLC> findByUserIdAndUniIdRange(int userId, int fromUniId, int toUniId);

    List<UserViewsByHourDLC> findByUserId(long userId);

    boolean existsByUserId(long userId);

    @Query("SELECT u.uniId FROM UserViewsByHourDLC u WHERE u.userId=:userId ORDER BY u.uniId ASC LIMIT 1")
    long getFirstUniIdByUserid(long userId);

    @Query("SELECT u.uniId FROM UserViewsByHourDLC u ORDER BY u.uniId DESC LIMIT 1")
    long getLastUniId();

    @Query("SELECT u.uniId FROM UserViewsByHourDLC u ORDER BY u.uniId DESC LIMIT 7")
    List<Integer> getLast7Uni();

}