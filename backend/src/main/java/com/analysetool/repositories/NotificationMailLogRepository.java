package com.analysetool.repositories;

import com.analysetool.modells.NotificationMailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMailLogRepository extends JpaRepository<NotificationMailLog, Long> {

    @Query("SELECT n FROM NotificationMailLog n WHERE n.sent=0")
    List<NotificationMailLog> findAllUnsent();

}
