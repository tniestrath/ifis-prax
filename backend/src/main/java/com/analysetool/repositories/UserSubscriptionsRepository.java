package com.analysetool.repositories;

import com.analysetool.modells.UserSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionsRepository extends JpaRepository<UserSubscriptions, Long> {

    @Query("SELECT us FROM UserSubscriptions us WHERE us.userId=:userId AND us.subId=:subId")
    Optional<UserSubscriptions> findByUserIdAndSubId(long userId, long subId);

    @Query("SELECT us FROM UserSubscriptions us WHERE us.subId=:subId")
    List<UserSubscriptions> findBySubId(long subId);

    @Query("SELECT us FROM UserSubscriptions us WHERE us.userId=:userId")
    List<UserSubscriptions> findByUserId(long userId);

    @Query("SELECT COUNT(u) FROM UserSubscriptions u JOIN Subscriptions s ON u.subId=s.id WHERE s.tag=:tagId")
    Integer getCountUserSubsInTag(long tagId);

    @Query("SELECT COUNT(u) FROM UserSubscriptions u " +
            "JOIN Subscriptions s ON u.subId=s.id " +
            "JOIN WPTerm t ON t.id=s.author " +
            "JOIN WPUser user ON user.nicename=t.slug WHERE user.id=:anbieterId")
    Integer getCountUserSubsOnAnbieter(long anbieterId);

    @Query("SELECT COUNT(u) FROM UserSubscriptions u")
    Integer getCountSubsTotal();

    @Query("SELECT COUNT(u) FROM UserSubscriptions u WHERE u.subId IN :subIds")
    Integer getCountSubsBySubId(List<Long> subIds);

}
