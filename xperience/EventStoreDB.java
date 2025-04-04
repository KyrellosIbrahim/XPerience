/**
 * File: EventStoreDB.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 17, 2025,
 * Professor: Dr. Donahoo
 * Description: This file implements EventStore interface.
 * It is a database implementation of EventStore that stores events in a MySQL database.
 */
package xperience;

import donabase.DonaBaseConnection;
import donabase.DonaBaseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In-memory implementation of EventStore that stores events in an ArrayList.
 * @author Kyrellos Ibrahim
 * @version 1.0
 */
public class EventStoreDB implements EventStore {
    private static final Logger logger = Logger.getLogger("xperience");
    private final DonaBaseConnection dbConn;

    /**
     * Constructor that initializes database connection
     *
     * @param dbServer Database server address
     * @param dbPort Database port
     * @param dbName Database name
     * @param username Database username
     * @param password Database user password
     * @throws IllegalStateException if database connection fails
     */
    public EventStoreDB(String dbServer, int dbPort, String dbName, String username, String password) {
        try {
            this.dbConn = new DonaBaseConnection(dbServer, dbPort, dbName, username, password);
            logger.info("Connected to database at " + dbServer);

            // Test connection
            testConnection();
        } catch (DonaBaseException e) {
            logger.log(Level.SEVERE, "Failed to connect to the database: " + e.getMessage(), e);
            throw new IllegalStateException("Database connection failed", e);
        }
    }

    /**
     * Test database connection with a simple query
     */
    private void testConnection() throws DonaBaseException {
        logger.info("Testing database connection...");
        var result = dbConn.query("SELECT 1");
        logger.info("Database connection test result: " + result);
    }

    /**
     * attempts to add an event to the database.
     * @param name the name of the event
     * @param date the date of the event
     * @param time the time of the event
     * @param description the details of the event
     * @return true if the event was added successfully, false otherwise.
     */
    @Override
    public boolean addEvent(String name, String date, String time, String description) {
        try {
            String formattedTime = time.contains(":") && time.split(":").length == 2 ?
                    time + ":00" : time;

            String insertStmt = String.format(
                    "INSERT INTO Event (name, date, time, description) VALUES ('%s', '%s', '%s', '%s')",
                    name, date, formattedTime, description);

            boolean success = dbConn.insert(insertStmt);
            if (success) {
                logger.info("Added event to database: " + name);
            } else {
                logger.warning("Failed to add event to database: " + name);
            }
            return success;
        } catch (DonaBaseException e) {
            logger.log(Level.SEVERE, "Database error adding event: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if an event name is not already in use.
     * @param name The name of the event to check
     * @return true if the name is available, false otherwise
     */
    @Override
    public boolean isNameAvailable(String name) {
        try {
            String queryStmt = String.format("SELECT COUNT(*) FROM Event WHERE name = '%s'", name);
            var result = dbConn.query(queryStmt);
            return result.isEmpty() || result.getFirst().isEmpty() ||
                    Integer.parseInt(result.getFirst().getFirst()) == 0;
        } catch (DonaBaseException e) {
            logger.log(Level.WARNING, "Database error checking name: " + e.getMessage(), e);
            return false; // Assume name is taken if database error occurs
        }
    }

    /**
     * Gets the number of events stored in the database.
     * @return the number of events
     */
    @Override
    public int getEventCount() {
        try {
            var result = dbConn.query("SELECT COUNT(*) FROM Event");
            return !result.isEmpty() && !result.getFirst().isEmpty() ?
                    Integer.parseInt(result.getFirst().getFirst()) : 0;
        } catch (DonaBaseException e) {
            logger.log(Level.WARNING, "Failed to get event count from database", e);
            return 0;
        }
    }

    /**
     * Gets all events stored in the database.
     * @return a list of events
     */
    @Override
    public List<Event> getAllEvents() {
        try {
            var result = dbConn.query("SELECT name, date, time, description FROM Event");
            List<Event> events = new ArrayList<>();

            for (var row : result) {
                if (row.size() >= 4) {
                    Event event = new Event(row.get(0), row.get(1), row.get(2), row.get(3));
                    events.add(event);
                }
            }

            return events;
        } catch (DonaBaseException e) {
            logger.log(Level.WARNING, "Failed to get events from database", e);
            return Collections.emptyList();
        }
    }
}
