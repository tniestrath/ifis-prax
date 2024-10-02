package com.analysetool.repositories;

import com.analysetool.modells.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {

}
