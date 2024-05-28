package com.analysetool.repositories;

import com.analysetool.modells.ForumModLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumModLogRepository extends JpaRepository<ForumModLog, Integer> {

}
