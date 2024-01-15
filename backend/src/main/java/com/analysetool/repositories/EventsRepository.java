package com.analysetool.repositories;

import com.analysetool.modells.Events;
import com.analysetool.modells.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {

    public Events getByEventID(long eventID);

    @Query("SELECT e.eventStart FROM Events e WHERE e.eventID =:eventID")
    public LocalDateTime getStartByEventID(long eventID);

    @Query("SELECT e FROM Events e")
    public List<Events> getAll();

    @Query("SELECT e FROM Events e WHERE e.postSlug =:slug AND e.eventStatus=1")
    Optional<Events> getActiveEventBySlug(String slug);
}