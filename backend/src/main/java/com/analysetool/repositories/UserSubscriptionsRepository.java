package com.analysetool.repositories;

import com.analysetool.modells.UserSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionsRepository extends JpaRepository<UserSubscriptions, Long> {

}
