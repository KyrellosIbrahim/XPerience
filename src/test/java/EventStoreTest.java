/**
 * File: EventStoreTest.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 31, 2025,
 * Professor: Dr. Donahoo
 * Description: JUnit test for EventStore interface using EventStoreMemory implementation.
 */
package xperience;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * JUnit test class for EventStore interface using EventStoreMemory implementation.
 *
 * @author Kyrellos Ibrahim
 * @version 1.0
 */
public class EventStoreTest {
    private EventStore eventStore; // EventStore instance for testing
    String[] event1 = {"Test Event", "2025-04-01", "12:30", "A test event description"}; // Test event 1
    String[] event2 = {"Another Event", "2025-04-02", "16:45", "Another test event"}; // Test event 2

    @BeforeEach
    public void setup() {
        eventStore = new EventStoreMemory(); // Initialize EventStoreMemory instance
    }

    @Test
    public void addEventTest() {
        // event 1
        assertTrue(eventStore.addEvent(event1[0], event1[1], event1[2], event1[3]));
        assertEquals(1, eventStore.getEventCount());
        // event 2
        assertTrue(eventStore.addEvent(event2[0], event2[1], event2[2], event2[3]));
        assertEquals(2, eventStore.getEventCount());
    }

    @Test
    public void testNameAvailability() {
        // all names should be available at first
        assertTrue(eventStore.isNameAvailable(event1[0]));
        // add an event with the name to test
        eventStore.addEvent(event1[0], event1[1], event1[2], event1[3]);
        // now the name should not be available
        assertFalse(eventStore.isNameAvailable(event1[0]));
        // other names should still be available
        assertTrue(eventStore.isNameAvailable(event2[0]));
    }

    @Test
    public void testGetEventCount() {
        // count should be 0 at first
        assertEquals(0, eventStore.getEventCount());
        // add an event
        eventStore.addEvent(event1[0], event1[1], event1[2], event1[3]);
        assertEquals(1, eventStore.getEventCount());
        // and another event
        eventStore.addEvent(event2[0], event2[1], event2[2], event2[3]);
        assertEquals(2, eventStore.getEventCount());
    }

    @Test
    public void testGetAllEvents() {
        // empty list at first
        List<Event> events = eventStore.getAllEvents();
        assertTrue(events.isEmpty());
        // add some events
        eventStore.addEvent(event1[0], event1[1], event1[2], event1[3]);
        eventStore.addEvent(event2[0], event2[1], event2[2], event2[3]);
        // get all events
        events = eventStore.getAllEvents();
        assertEquals(2, events.size());

        // verify event properties
        Event event1 = events.getFirst();
        assertEquals("Test Event", event1.getName());
        assertEquals("2025-04-01", event1.getDate());
        assertEquals("12:30", event1.getTime());

        Event event2 = events.get(1);
        assertEquals("Another Event", event2.getName());
        assertEquals("2025-04-02", event2.getDate());
        assertEquals("16:45", event2.getTime());
    }
    
    @Test
    public void testInvalidEventName() {
        String invalidEventName = "A".repeat(301);  // Name too long
        assertFalse(eventStore.addEvent(invalidEventName, event1[1], event1[2], event1[3]));
    }
}
