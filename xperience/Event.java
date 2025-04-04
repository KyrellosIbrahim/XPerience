/**
 * File: Event.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 17, 2025,
 * Professor: Dr. Donahoo
 * Description: This file is the format for the event class.
 * This class represents an event with a name, date, time, and description.
 */
package xperience;

/**
 * This class represents an event with a name, date, time, and description.
 * This works for XPerienceServer and XPerienceServerDB when it cannot connect to the database.
 */
public class Event {
    private final String name; // The name of the event
    private final String date; // The date of the event
    private final String time; // The time of the event
    private final String description; // The details about the event

    /**
     * Constructs an Event object with the specified details.
     *
     * @param name The name of the event.
     * @param date The date of the event.
     * @param time The time of the event.
     * @param description A brief description of the event.
     */
    public Event(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    /**
     * Gets the name of the event.
     *
     * @return The name of the event.
     */
    public String getName() {
        return name;
    }
    /**
     * Gets the date of the event.
     *
     * @return The date of the event.
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the time of the event.
     * @return The time of the event.
     */
    public String getTime() {
        return time;
    }

    /**
     * formats the event details into a string.
     * @return A string representation of the event.
     */
    @Override
    public String toString() {
        return "Event{name='" + name + "', date='" + date + "', time='" + time + "', description='" + description + "'}";
    }
}
