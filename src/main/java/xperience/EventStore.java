/**
 * File: EventStore.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 18, 2025,
 * Professor: Dr. Donahoo
 * Description: This file contains the EventStore interface. It allows for different
 * storage implementations to be used.
 */
package xperience;

import java.util.List;

/**
 * Interface for event storage operations, allowing different storage implementations.
 *
 * @author Kyrellos Ibrahim
 * @version 1.0
 */
public interface EventStore {
    /**
     * Adds an event to the store.
     *
     * @param name the name of the event
     * @param date the date of the event
     * @param time the time of the event
     * @param description the details of the event
     * @return true if the event was added successfully, false otherwise
     */
    boolean addEvent(String name, String date, String time, String description);

    /**
     * Check if an event with the given name already exists
     * @param name The name of the event to check
     * @return true if the name is available (not taken), false if already exists
     */
    boolean isNameAvailable(String name);

    /**
     * Get the total count of events in the store
     * @return The number of events
     */
    int getEventCount();

    /**
     * Get all events in the store
     * @return List of all events
     */
    List<Event> getAllEvents();

}
