package com.analysetool.repositories;

import com.analysetool.modells.UniqueUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UniqueUserRepository extends JpaRepository<UniqueUser, Long> {

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.nonsense!='[0]' OR " +
            "(u.newsletter!='[0]' OR u.news!='[0]' OR u.datenschutz!='[0]' OR u.partner!='[0]' " +
            "OR u.preisliste!='[0]' OR u.ratgeber!='[0]' OR u.podcast!='[0]' OR u.whitepaper!='[0]'  " +
            "OR u.article!='[0]' OR u.agb!='[0]' OR u.blog!='[0]' OR u.image!='[0]' OR u.impressum!='[0]' OR u.ueber!='[0]')")
    public int getUserCountGlobal();

    @Query("SELECT u FROM UniqueUser u WHERE u.ip = :ip")
    public UniqueUser findByIP(String ip);

    // Methode, um die durchschnittliche Verweildauer aller Nutzer zurückzugeben
    @Query("SELECT AVG(u.time_spent) FROM UniqueUser u")
    Double getAverageTimeSpent();

    // Methode, um die durchschnittliche Verweildauer der Nutzer für einen Tag zurückzugeben
    @Query("SELECT AVG(u.time_spent) FROM UniqueUser u WHERE u.first_click >= :startOfDay AND u.first_click <= :endOfDay")
    Double getAverageTimeSpentBetweenDates(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT u FROM UniqueUser u WHERE u.amount_of_clicks >= 2")
    List<UniqueUser> findTopByMoreThanTwoClicks(Pageable pageable);

    @Query("SELECT u FROM UniqueUser u WHERE u.amount_of_clicks >= 2")
    List<UniqueUser> findAllByMoreThanTwoClicks();

    @Query("SELECT count(u) FROM UniqueUser u WHERE u.amount_of_clicks = 0")
    Long getCountOfZeroClicksUser();

    @Query("SELECT count(u) FROM UniqueUser u ")
    Long getCountOfAllUser();

}
