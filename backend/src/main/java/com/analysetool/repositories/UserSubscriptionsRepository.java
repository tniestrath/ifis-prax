package com.analysetool.repositories;

import com.analysetool.modells.UserSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionsRepository extends JpaRepository<UserSubscriptions, Long> {

    @Query("SELECT us FROM UserSubscriptions us WHERE us.userId=:userId AND us.subId=:subId")
    Optional<UserSubscriptions> findByUserIdAndSubId(long userId, long subId);

}
