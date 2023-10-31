package com.analysetool.api;

import com.analysetool.modells.Events;
import com.analysetool.repositories.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/events")
public class EventsController {

    @Autowired
    EventsRepository eventsRepo;

    /**
     *  Counts all the Events that are planned and haven't started yet.
     * @return the count explained above.
     */
    @GetMapping("/getCountUpcomingEvents")
    public int getCountOfUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for(Events event : eventsRepo.findAll()) {
            if(event.getEventStart().isAfter(now)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts all the events that are currently running.
     * @return count of all events that are currently running.
     */
    @GetMapping("/getCountCurrentEvents")
    public int getCountCurrentEvents() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for(Events event : eventsRepo.findAll()) {
            if(event.getEventStart().isBefore(now) && event.getEventEnd().isAfter(now)) {
                count++;
            }
        }
        return count;
    }

    @GetMapping("/getCountPastEvents")
    public int getCountPastEvents() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for(Events event : eventsRepo.findAll()) {
            if(event.getEventEnd().isBefore(now)) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event.
     */
    @GetMapping("/getAmountOfEventsCreatedYesterday")
    public List<String> getAmountOfEventsCreatedYesterday() {
        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.findAll();
        LocalDate today = LocalDate.now();

        for (Events e : allEvents) {
            LocalDate createdDate = e.getEventDateCreated().toLocalDate();

            if (createdDate.isBefore(today)) {
                if(isCurrent(e)) {
                    events.add("c|" + e.getEventName());
                } else if(isUpcoming(e)) {
                    events.add("u|" + e.getEventName());
                }

            }
        }
        return events;
    }

    private boolean isCurrent(Events e) {
        LocalDateTime now = LocalDateTime.now();
        return e.getEventStart().isBefore(now) && e.getEventEnd().isAfter(now);
    }

    private boolean isUpcoming(Events e) {
        LocalDateTime now = LocalDateTime.now();
        return e.getEventStart().isAfter(now);
    }

}
