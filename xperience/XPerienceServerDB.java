/**
 * File: XPerienceServerDB.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 17, 2025,
 * Professor: Dr. Donahoo
 * Description: This file extends the XPerienceServer class to add database
 * functionality for storing and retrieving event data with MySQL.
 * It terminates if the database is unavailable.
 */
package xperience;

import java.util.logging.Level;
import java.util.logging.Logger;

/** This class extends XPerienceServer to add database functionality.
 * It can be used to manage event data with mySQL, and terminates if the database is unavailable.
 *
 * @author Kyrellos Ibrahim
 * @version 1.2
 */
public class XPerienceServerDB  {
    private static final String LOGGERNAME = "xperience"; // logger name for the server
    private static final Logger logger = Logger.getLogger(LOGGERNAME); // logger instance for the server
    private static final int DB_PORT = 3306; // MySQL port
    private static final String DB_NAME = "ibrahim"; // name of the database
    private static final String DB_USER = "xperience_user"; // username for the database
    private static final String DB_PASSWORD = "Strong!Password"; // password for the database
    private final XPerienceServer server; // the XPerienceServer instance

    /**
     * Constructor for XPerienceServerDB that uses defined constants to connect to the database.
     * @param eventStore The event store implementation to use.
     */
    public XPerienceServerDB(EventStore eventStore, PasswordList passwordList) {
        this.server = new XPerienceServer(eventStore, passwordList); // Containment
    }

    /**
     * Starts the server and listens for incoming connections.
     * @param port The port number to listen on.
     */
    public void startServer(int port) {
        server.startServer(port); // Delegate to the contained XPerienceServer instance
    }

    /**
     * Main method to start the XPerience server with database functionality.
     * @param args Command-line arguments. Expected: <port> <db server> <password file>
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            logger.severe("Incorrect parameter(s). Expected: <port> <db server> <password file>");
            System.exit(1);
        }

        // Parse the port number with error handling for invalid input
        int servPort;
        try {
            servPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.severe("Invalid port number: " + args[0]);
            System.exit(1);
            return; // This line is unreachable but required for compilation
        }

        String dbServer = args[1]; // get the db server from the command line
        String passwordFile = args[2]; // get the password from the command line
        try {
            logger.info("Starting XPerience server on port " + servPort + " with database server at " + dbServer);
            EventStore eventStore = new EventStoreDB(dbServer, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
            PasswordList passwordList = new PasswordList(passwordFile);
            XPerienceServerDB server = new XPerienceServerDB(eventStore, passwordList);
            server.startServer(servPort);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to the database server: " + e.getMessage(), e);
            System.exit(1);
        }
    }


}