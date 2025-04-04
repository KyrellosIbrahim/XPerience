/**
 * File: EventStoreMemory.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 17, 2025,
 * Professor: Dr. Donahoo
 * Description: This file implements EventStore interface.
 * It is an in-memory implementation of EventStore that stores events in an ArrayList.
 */
package xperience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * In-memory implementation of EventStore that stores events in an ArrayList.
 * @author Kyrellos Ibrahim
 * @version 1.0
 */
public class EventStoreMemory implements EventStore {
    private static final Logger logger = Logger.getLogger("xperience");
    private final List<Event> eventList = new ArrayList<>();

    /**
     * Adds an event to the in-memory storage.
     * @param name the name of the event
     * @param date the date of the event
     * @param time the time of the event
     * @param description the details of the event
     * @return true if the event was added successfully, false otherwise.
     */
    @Override
    public boolean addEvent(String name, String date, String time, String description) {
        Event event = new Event(name, date, time, description);
        eventList.add(event);
        logger.info("Added event to memory store: " + event);
        return true;
    }

    /**
     * Checks if an event name is available (not already in use).
     * @param name the name of the event
     * @return true if the name is available, false otherwise
     */
    @Override
    public boolean isNameAvailable(String name) {
        return eventList.stream().noneMatch(event -> event.getName().equalsIgnoreCase(name));
    }

    /**
     * Gets the number of events stored.
     * @return the number of events
     */
    @Override
    public int getEventCount() {
        return eventList.size();
    }

    /**
     * Gets all events stored.
     * @return an unmodifiable list of events
     */
    @Override
    public List<Event> getAllEvents() {
        return Collections.unmodifiableList(eventList);
    }
}
