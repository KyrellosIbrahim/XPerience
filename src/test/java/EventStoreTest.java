/**
 * File: EventStoreTest.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 31, 2025,
 * Professor: Dr. Donahoo
 * Description: JUnit test for EventStore interface using EventStoreMemory implementation.
 */
package xperience;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

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
    public void testAddEventResponse() throws IOException {
        // Create a temporary password file for testing
        Path tempPasswordFile = Files.createTempFile("test-passwords", ".txt");
        Files.write(tempPasswordFile, List.of("testpassword"));

        PasswordList ps = new PasswordList(tempPasswordFile.toString());
        XPerienceServer server = new XPerienceServer(eventStore, ps);

        // Use ByteArrayOutputStream to capture output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out, true);

        // Simulate client request with correct format and include password
        String input = "TestEvent#2025-04-01#12:30#TestDescription#testpassword#";
        server.processClientRequest(new Scanner(input).useDelimiter("#"), writer);

        // Flush the writer to ensure all output is written
        writer.flush();

        String response = out.toString().trim();
        System.out.println("Response: " + response);
        assertEquals("Accept#1#", response);

        // Clean up temporary file
        Files.delete(tempPasswordFile);
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
