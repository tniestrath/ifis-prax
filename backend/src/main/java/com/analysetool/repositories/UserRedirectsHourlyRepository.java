package com.analysetool.repositories;


import com.analysetool.modells.UserRedirectsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRedirectsHourlyRepository extends JpaRepository<UserRedirectsHourly, Integer> {

    List<UserRedirectsHourly> findAllByUserId(Long userId);
    List<UserRedirectsHourly> findAllByUserIdAndUniId(Long userId, Integer uniId);
    boolean existsByUserId(long userId);

    @Query("SELECT u.uniId FROM UserRedirectsHourly u ORDER BY u.uniId DESC LIMIT 7")
    List<Integer> getLast7Uni();
    @Query("SELECT u.uniId FROM UserRedirectsHourly u ORDER BY u.uniId DESC LIMIT 1")
    long getLastUniId();
    @Query("SELECT u.uniId FROM UserRedirectsHourly u WHERE u.userId=:userId ORDER BY u.uniId ASC LIMIT 1")
    long getFirstUniIdByUserId(Long userId);
    @Query("SELECT u.userId, SUM(u.redirects) FROM UserRedirectsHourly u GROUP BY u.userId")
    List<Object[]> getUserIdAndRedirectsSum();
    @Query("SELECT SUM(u.redirects) FROM UserRedirectsHourly u WHERE u.userId=:userId")
    Long getAllRedirectsOfUserIdSummed(Long userId);
    @Query("SELECT SUM(u.redirects) FROM UserRedirectsHourly u WHERE u.userId IN :userIds")
    Long findSumOfRedirectsForUserIds(List<Long> userIds);
    List<UserRedirectsHourly> findAllByUserIdInAndUniId(List<Long> userIds, Integer uniId);
    @Query("SELECT SUM(u.redirects) FROM UserRedirectsHourly u")
    Long getAllRedirectsSummed();

    Optional<UserRedirectsHourly> getByUniIdAndHourAndUserId(int uniId, int hour, long userId);

    @Query("SELECT SUM(u.redirects) FROM UserRedirectsHourly u WHERE u.userId=:userId AND u.uniId > (SELECT uh.uniId - 91 FROM UserViewsByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1)")
    Long getSumForUserThisQuarter(int userId);

    @Query("SELECT SUM(u.redirects) FROM UserRedirectsHourly u WHERE u.userId=:userId AND u.uniId < (SELECT uh.uniId - 91 FROM UserViewsByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1) AND u.uniId > (SELECT uh.uniId - 182 FROM UserViewsByHourDLC uh ORDER BY uh.uniId DESC LIMIT 1)")
    Long getSumForUserPreviousQuarter(int userId);

    @Query("SELECT SUM(u.redirects) FROM UserRedirectsHourly u WHERE u.userId IN (:userIds)")
    int getSumRedirectsOfUsersInList(List<Long> userIds);

}
