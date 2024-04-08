package com.analysetool.repositories;

import com.analysetool.modells.WPOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WPOptionsRepository extends JpaRepository<WPOptions, Long> {

    @Query("SELECT o.optionValue FROM WPOptions o WHERE o.optionName='bbb_badbots'")
    String getAllBlockedBots();
}
