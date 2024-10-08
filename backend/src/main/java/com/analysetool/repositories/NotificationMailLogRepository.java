package com.analysetool.repositories;

import com.analysetool.modells.NotificationMailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationMailLogRepository extends JpaRepository<NotificationMailLog, Long> {
}
