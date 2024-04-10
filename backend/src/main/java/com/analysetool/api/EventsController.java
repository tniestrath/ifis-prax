package com.analysetool.api;

import com.analysetool.modells.Events;
import com.analysetool.repositories.EventsRepository;
import com.analysetool.repositories.WPTermRepository;
import com.analysetool.repositories.WpTermRelationshipsRepository;
import com.analysetool.util.Constants;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/events")
public class EventsController {

    @Autowired
    EventsRepository eventsRepo;

    @Autowired
    WpTermRelationshipsRepository relRepo;

    @Autowired
    WPTermRepository termRepo;

    /**
     *  Counts all the Events that are planned and haven't started yet.
     * @return the count explained above.
     */
    @GetMapping("/getCountUpcomingEvents")
    public int getCountOfUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for(Events event : eventsRepo.findAll()) {
            if(event.getEventStart().isAfter(now) && isActive(event)) {
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
            if(event.getEventStart().isBefore(now) && event.getEventEnd().isAfter(now) && isActive(event)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Fetches the amount of registered Events that have already passed.
     * @return a positive integer.
     */
    @GetMapping("/getCountPastEvents")
    public int getCountPastEvents() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for(Events event : eventsRepo.findAll()) {
            if(event.getEventEnd().isBefore(now) && isActive(event)) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event for all events created within the last day.
     */
    @GetMapping("/getAmountOfEventsCreatedYesterday")
    public List<String> getAmountOfEventsCreatedYesterday() {
        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.findAll();
        LocalDate today = LocalDate.now();

        for (Events e : allEvents) {
            LocalDate createdDate = e.getEventDateCreated().toLocalDate();

            if (createdDate.isBefore(today) && isActive(e)) {
                if(isCurrent(e)) {
                    events.add("c|" + getEventType(e));
                } else if(isUpcoming(e)) {
                    events.add("u|" + getEventType(e));
                }

            }
        }
        return events;
    }

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event for all Events.
     */
    @GetMapping("/getAmountOfEvents")
    public List<String> getAmountOfEvents() {
        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.findAll();

        for (Events e : allEvents) {
            if(isActive(e)) {
                if (isCurrent(e)) {
                    events.add("c|" + getEventType(e));
                } else if (isUpcoming(e)) {
                    events.add("u|" + getEventType(e));
                }
            }

        }
        return events;
    }

    /**
     * Utility function to assess whether an Event is currently being held.
     * (Starting Point has been passed, End Point has not been.)
     * @param e the event-row to assess for.
     * @return true, if current - otherwise false.
     */
    public boolean isCurrent(Events e) {
        LocalDateTime now = LocalDateTime.now();
        return e.getEventStart().isBefore(now) && e.getEventEnd().isAfter(now);
    }

    /**
     * Utility function to assess whether an Event is in the future.
     * (Starting Point has not been passed, End Point has not been passed.)
     * @param e the event-row to assess for.
     * @return true, if upcoming - otherwise false.
     */
    public boolean isUpcoming(Events e) {
        LocalDateTime now = LocalDateTime.now();
        return e.getEventStart().isAfter(now);
    }

    /**
     *
     * @param e the event you want the type for.
     * @return a char, representing its type 'o' (sonstige),'k' (kongresse), 'm' (messe), 's'(schulungen), 'w' (workshop)
     */
    public String getEventType(Events e) {
        List<Long> termIds = relRepo.existsByObjectId(e.getPostID()) ? relRepo.getTaxIdByObject(e.getPostID()) : null;

        Constants c = Constants.getInstance();
        if(termIds != null && isActive(e)) {
            //sonstige
            if(termIds.contains(c.getSonstigeEventsTermId())) {
                return "r";
            }
            //Messen
            if(termIds.contains(c.getMessenTermId())) {
                return "m";
            }
            //Kongresse
            if(termIds.contains(c.getKongressTermId())) {
                return "k";
            }
            //Seminare/Schulungen
            if(termIds.contains(c.getSchulungTermId())) {
                return "s";
            }
            //Workshops
            if(termIds.contains(c.getWorkshopTermId())) {
                return "w";
            }
        } else {
            return "o";
        }
        return "o";
    }

    public long getTermIdFromFrontendType(String type) {
        long returnal = -1;
        Constants c = Constants.getInstance();
        switch(type) {
            case " " -> returnal = 0;
            case "KG" -> returnal = c.getKongressTermId();
            case "ME" -> returnal = c.getMessenTermId();
            case "SE" -> returnal = c.getSchulungTermId();
            case "WS" -> returnal = c.getWorkshopTermId();
            case "SO" -> returnal = c.getSonstigeEventsTermId();
        }
        return returnal;
    }

    /**
     * Fetches all Events that were created during the current day.
     * @return a JSON-Array String containing the EventNames.
     */
    @GetMapping("/getNewEvents")
    public String getNewEvents() {
        List<Events> listEvents = eventsRepo.findAll();
        List<String> listNewEvents = new ArrayList<>();
        for(Events e : listEvents) {
            if (e.getEventDateCreated().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT))){
                listNewEvents.add(e.getEventName());
            }
        }
        return new JSONArray(listNewEvents).toString();
    }

    /**
     * Fetches whether an Event is set to be active.
     * @param e the Event to fetch for.
     * @return true, if active - otherwise false.
     */
    public boolean isActive(Events e) {
        return e.getEventStatus() == 1;
    }


}
