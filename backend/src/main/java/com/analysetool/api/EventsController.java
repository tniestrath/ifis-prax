package com.analysetool.api;

import com.analysetool.modells.Events;
import com.analysetool.repositories.EventsRepository;
import com.analysetool.repositories.WPTermRepository;
import com.analysetool.repositories.WpTermRelationshipsRepository;
import com.analysetool.services.EventsService;
import com.analysetool.util.Constants;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/events", "/0wB4P2mly-xaRmeeDOj0_g/events"}, method = RequestMethod.GET, produces = "application/json")
public class EventsController {

    @Autowired
    private EventsService eventsService;


    /**
     *  Counts all the Events that are planned and haven't started yet.
     * @return the count explained above.
     */
    @GetMapping("/getCountUpcomingEvents")
    public int getCountOfUpcomingEvents() {return eventsService.getCountOfUpcomingEvents();}

    /**
     * Counts all the events that are currently running.
     * @return count of all events that are currently running.
     */
    @GetMapping("/getCountCurrentEvents")
    public int getCountCurrentEvents() {return eventsService.getCountCurrentEvents();}

    /**
     * Fetches the amount of registered Events that have already passed.
     * @return a positive integer.
     */
    @GetMapping("/getCountPastEvents")
    public int getCountPastEvents() {return eventsService.getCountPastEvents();}

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event for all events created within the last day.
     */
    @GetMapping("/getAmountOfEventsCreatedYesterday")
    public List<String> getAmountOfEventsCreatedYesterday() {return eventsService.getAmountOfEventsCreatedYesterday();}

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event for all Events.
     */
    @GetMapping("/getAmountOfEvents")
    public List<String> getAmountOfEvents() {return eventsService.getAmountOfEvents();}


    /**
     * Fetches all Events that were created during the current day.
     * @return a JSON-Array String containing the EventNames.
     */
    @GetMapping("/getNewEvents")
    public String getNewEvents() {return eventsService.getNewEvents();}

}
