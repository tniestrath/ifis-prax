package com.analysetool.repositories;

import com.analysetool.modells.UserSubscriptionCountLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubscriptionCountLogRepository extends JpaRepository<UserSubscriptionCountLog, Long> {

    @Query("SELECT u.count FROM UserSubscriptionCountLog u ORDER BY u.id DESC LIMIT 1")
    Integer getCountToday();

    @Query("SELECT u.count FROM UserSubscriptionCountLog u ORDER BY u.id DESC")
    List<Integer> getCountPage(Pageable pageable);
}
