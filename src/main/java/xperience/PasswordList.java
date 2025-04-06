/**
 * File: PasswordList.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 30, 2025,
 * Professor: Dr. Donahoo
 * Description: This file implements a PasswordList class that manages one-time passwords
 * to authenticate post requests to the XPerience server.
 */
package xperience;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Class to manage password list
 *
 * @version 1.0
 */
public class PasswordList {
    private static final Logger logger = Logger.getLogger("xperience"); // logger instance for the password list
    private final Set<String> passwords = new HashSet<>(); // Set to store the passwords
    private final Path passwordFilePath; // Path to the password file
    /**
     * Create password list from file
     *
     * @param passwordFilePath Path to password file
     * @throws IOException if I/O problem
     */
    public PasswordList(String passwordFilePath) throws IOException {
        this.passwordFilePath = Path.of(passwordFilePath); // Set the password file path
        loadPasswords(); // Load passwords from the file
    }

    private void loadPasswords()throws IOException {
        logger.info("Loading passwords from file: " + passwordFilePath); // Log the loading process
        try {
            passwords.clear(); // Clear existing passwords
            for(String line : java.nio.file.Files.readAllLines(passwordFilePath)) { // Read each line from the file
                String trimmedLine = line.trim(); // Trim whitespace
                if (!trimmedLine.isEmpty()) { // Ignore empty lines
                    passwords.add(trimmedLine); // Add password to the set
                }
            }
            logger.info("loaded " + passwords.size() + " passwords successfully."); // Log success
        }
        catch (IOException e) {
            logger.severe( "Failed to load passwords from file: " + e.getMessage());
            throw e; // Rethrow the exception
        }
    }

    /**
     * If password in list, remove password from list and return true;
     * otherwise (not in list), return false
     *
     * @param password password to use
     * @return true if password in list; false otherwise
     */
    public boolean use(String password) {
        if (password == null || password.isEmpty() || password.contains("#")) {
            logger.warning("Invalid password format");
            return false;
        }
        boolean isValid = passwords.remove(password);
        if (isValid) {
            logger.info("Valid password used and removed");
        } else {
            logger.warning("Invalid password attempted: " + password);
        }
        return isValid;
    }

    /**
     * Reloads the password list from the file.
     * @throws IOException if file cannot be read
     */
    public void reload() throws IOException {
        loadPasswords(); // Load passwords from the file
    }

    /**
     * get the number of remaining passwords
     * @return the count of remaining passwords
     */
    public int getRemainingPasswordCount() {
        return passwords.size(); // Return the count of remaining passwords
    }

    /**
     * remove the password from the list
     */
    public void removePassword(String password) {
        passwords.remove(password);
    }
}
