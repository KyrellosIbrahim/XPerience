/**
 * File: PasswordListTest.java
 * Course: CSC 4610
 * Student: Kyrellos Ibrahim
 * Date: March 31, 2025,
 * Professor: Dr. Donahoo
 * Description: JUnit test for PasswordList class.
 */
package xperience;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * JUnit test class for PasswordList.
 *
 * @author Kyrellos Ibrahim
 * @version 1.0
 */
public class PasswordListTest {
    @TempDir
    private Path tempDir; // temporary directory for password file
    private Path passwordFile; // temporary password file
    private List<String> testPasswords; // list of test passwords

    @BeforeEach
    public void setup() throws IOException {
        passwordFile = tempDir.resolve("passwords.txt"); // create temporary password file
        testPasswords = Arrays.asList("password123", "Strong!Password", "foofoo", "RonaldMcDonald"); // list of test passwords
        Files.write(passwordFile, testPasswords); // write test passwords to the file
    }

    @Test
    public void passwordListCreation() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());
        assertEquals(testPasswords.size(), passwordList.getRemainingPasswordCount());
    }

    @Test
    public void testValidPassword() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());
        assertTrue(passwordList.use("password123")); // check if valid password
        assertEquals(testPasswords.size() - 1, passwordList.getRemainingPasswordCount()); // check remaining count

        // password must be removed after testing
        assertFalse(passwordList.use("password123")); // check if password is no longer valid
    }

    @Test
    public void testInvalidPassword() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());
        assertFalse(passwordList.use("notAValidPassword")); // check if invalid password
        assertEquals(testPasswords.size(), passwordList.getRemainingPasswordCount()); // check remaining count
    }

    @Test
    public void testHashPassword() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());
        assertFalse(passwordList.use("invalid#Password")); // check if invalid password
        assertEquals(testPasswords.size(), passwordList.getRemainingPasswordCount()); // check remaining count
    }

    @Test
    public void testReload() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());

        // Use a password
        assertTrue(passwordList.use("password123"));
        assertEquals(testPasswords.size() - 1, passwordList.getRemainingPasswordCount());

        // Reload and check if all passwords are available again
        passwordList.reload();
        assertEquals(testPasswords.size(), passwordList.getRemainingPasswordCount());
    }

    @Test
    public void testEmptyPassword() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());
        assertFalse(passwordList.use("")); // check empty password
        assertEquals(testPasswords.size(), passwordList.getRemainingPasswordCount()); // check remaining count
    }

    @Test
    public void testNullPassword() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());
        assertFalse(passwordList.use(null)); // check null password
        assertEquals(testPasswords.size(), passwordList.getRemainingPasswordCount()); // check remaining count
    }

    @Test
    public void testWhitespacePassword() throws IOException {
        PasswordList passwordList = new PasswordList(passwordFile.toString());
        assertFalse(passwordList.use("   ")); // check whitespace password
        assertEquals(testPasswords.size(), passwordList.getRemainingPasswordCount()); // check remaining count
    }
}
