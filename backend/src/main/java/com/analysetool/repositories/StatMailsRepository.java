package com.analysetool.repositories;

import com.analysetool.modells.StatMails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatMailsRepository extends JpaRepository<StatMails, Long> {

    StatMails findByUserId(long userId);

}
