/**
 * File: XPerienceServer.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 17, 2025,
 * Professor: Dr. Donahoo
 * Description: This file is a server that uses in-memory storage to store events.
 */
package xperience;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * XPerienceServer - A server application for handling event submissions.
 * This server listens for client connections on a specified port, accepts event data,
 * and checks for duplicates before adding them to the event list. It logs all actions
 * and errors for monitoring and debugging purposes.
 *
 * @author Kyrellos Ibrahim
 * @version 1.3
 */
public class XPerienceServer {
    private static final String LOGGERNAME = "xperience"; // logger name for the server
    private static final Logger logger = Logger.getLogger(LOGGERNAME); // logger instance for the server
    private static final String DELIM = "#"; // delimiter the client uses to separate the inputted data
    protected static final Charset ENCODING = StandardCharsets.US_ASCII; // encoding for the server
    protected final EventStore EVENT_STORE; // event store to store events
    protected final PasswordList PASSWORD_LIST; // password list for authentication
    private static final int MAX_NAME_LENGTH = 300; // maximum length for event name
    private static final int MAX_DESCRIPTION_LENGTH = 65535; // maximum length for event description
    private static final String DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}"; // date format regex
    private static final String TIME_FORMAT = "([01]\\d|2[0-3]):([0-5]\\d)"; // time format regex

    /**
     * Constructor for XPerienceServer.
     * Initializes the event store with the in-memory implementation.
     * @param eventStore The event store implementation to use.
     */
    public XPerienceServer(EventStore eventStore, PasswordList PASSWORD_LIST) {
        this.EVENT_STORE = eventStore;
        this.PASSWORD_LIST = PASSWORD_LIST;
    }

    /**
     * Main method to start the XPerience server.
     * @param args Command-line arguments. Expected: <Port> <Password>
     */
    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 2) {
            logger.severe("Incorrect parameter(s). Expected: <Port> <Password>");
            System.exit(1);
        }

        // Parse the port number with error handling for invalid input
        int servPort;
        try {
            servPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid port number: " + args[0], e);
            System.exit(1);
            return; // This line is unreachable but required for compilation
        }
        String passwordFile = args[1]; // get password file from command line
        try {
            EventStore eventStore = new EventStoreMemory();
            PasswordList passwordList = new PasswordList(passwordFile);
            XPerienceServer server = new XPerienceServer(eventStore, passwordList);
            server.startServer(servPort);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load password file: " + e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Starts the server and listens for incoming client connections.
     * @param port The port number on which the server will listen for connections.
     */
    protected void startServer(int port) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
             ServerSocket servSock = new ServerSocket(port)) {
            logger.info("XPerience Server started on port " + port);
            while (true) {
                Socket clientSock = servSock.accept();
                logger.info("Connection from: " + clientSock.getRemoteSocketAddress());
                executor.submit(() -> handleClient(clientSock));
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Server setup failed", ex);
        }
    }

    /**
     * Handles communication with a connected client.
     * @param clientSock The socket connected to the client.
     */
    protected void handleClient(Socket clientSock) {
        logger.info("Handling client at " + clientSock.getRemoteSocketAddress());
        try (Scanner in = new Scanner(clientSock.getInputStream(), ENCODING);
             PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true, ENCODING)) {

            in.useDelimiter(DELIM);
            processClientRequest(in, out);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Client communication failed", ex);
        } finally {
            try {
                clientSock.close();
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Failed to close client socket", ex);
            }
        }
    }

    /**
     * Processes the client request and sends a response.
     * @param in The input scanner for the client.
     * @param out The output writer for the client.
     */
    protected void processClientRequest(Scanner in, PrintWriter out) {
        if (!in.hasNext()) {
            out.print("Invalid request");
            return;
        }
        // check if name is valid
        String name = in.next();
        if(name.length() > MAX_NAME_LENGTH || name.isEmpty()) {
            rejectRequest(out, "Name must be between 1 and "+ MAX_NAME_LENGTH +" characters");
            return;
        }
        // validate name availability
        if (!EVENT_STORE.isNameAvailable(name)) {
            String reason = !in.hasNext() ? "Missing data" : "Duplicate name";
            rejectRequest(out, reason);
            return;
        }
        // check if date is present
        if(!in.hasNext()) {
            rejectRequest(out, "Missing date");
            return;
        }
        // check if date matches the format YYYY-MM-DD
        String date = in.next();
        if(!date.matches(DATE_FORMAT)) {
            rejectRequest(out, "Invalid date format. Expected YYYY-MM-DD");
            return;
        }
        // check if time is present
        if(!in.hasNext()) {
            rejectRequest(out, "Missing time");
            return;
        }
        // check if time matches the format HH:MM in 24-hour format
        String time = in.next();
        if(!time.matches(TIME_FORMAT)) {
            rejectRequest(out, "Invalid time format. Expected HH:MM in 24-hour format");
            return;
        }
        // check if description is present
        if(!in.hasNext()) {
            rejectRequest(out, "Missing description");
            return;
        }
        // check if description is valid
        String description = in.next();
        if(description.length() > MAX_DESCRIPTION_LENGTH || description.isEmpty()) {
            rejectRequest(out, "Description must be between 1 and "+ MAX_DESCRIPTION_LENGTH +" characters");
            return;
        }
        // check if password is present
        if(!in.hasNext()) {
            rejectRequest(out, "Missing password");
            return;
        }
        // validate password
        String password = in.next();
        if(!PASSWORD_LIST.use(password)) {
            rejectRequest(out, "Invalid password");
            return;
        }
        else { // password is removed after it is validated
            PASSWORD_LIST.removePassword(password);
        }
        // add the event to the storage
        boolean success = EVENT_STORE.addEvent(name, date, time, description);
        if(success) {
            int eventCount = EVENT_STORE.getEventCount();
            out.print("Accept#" + eventCount + "#");
            logger.info("Event added successfully: " + name);
        }
        else {
            rejectRequest(out, "Failed to add event");
        }
    }

    /**
     * Send a rejection response to the client
     * @param out PrintWriter to send the response
     * @param reason Reason for rejection (for logging only)
     */
    protected void rejectRequest(PrintWriter out, String reason) {
        out.print("Reject#");
        logger.warning("Rejected event: " + reason);
    }

}