package at.technikum.mtcg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class User {
    private final String Username;
    private final String Password;

    public User(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    public String getUsername() {
        return this.Username;
    }

    public String getPassword() {
        return this.Password;
    }

    public String generateToken() {
        return Username + "-mtcgToken-" + UUID.randomUUID();
    }

    public boolean register() {
    String checkUserSQL = "SELECT * FROM users WHERE username = ?";
    String insertUserSQL = "INSERT INTO users (username, password, coins) VALUES (?, ?, 20)";

    try (Connection conn = DatabaseConnector.connect()) {
        try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSQL)) {
            checkStmt.setString(1, this.Username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Benutzername bereits vergeben: " + this.Username);
                return false;
            }
        }

        // Salt generieren und Passwort hashen
        String salt = generateSalt();
        String hashedPassword = hashPassword(this.Password, salt);

        try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSQL)) {
            insertStmt.setString(1, this.Username);
            insertStmt.setString(2, hashedPassword);
            insertStmt.executeUpdate();
        }


        System.out.println("Benutzer erfolgreich registriert.");
        return true;

    } catch (SQLException e) {
        System.err.println("Fehler bei der Benutzerregistrierung: " + e.getMessage());
        return false;
    }
    }


    public boolean login() {
        String selectUserSQL = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(selectUserSQL)) {

            stmt.setString(1, this.Username);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");

                // Salt und Hash trennen
                String[] parts = storedHash.split(":");
                if (parts.length != 2) {
                    throw new RuntimeException("Fehlerhaftes Passwortformat in der Datenbank");
                }

                String salt = parts[0];
                String expectedHash = parts[1];

                // Passwort mit dem extrahierten Salt erneut hashen
                String hashedPassword = hashPassword(this.Password, salt).split(":")[1];

                // Vergleich des gespeicherten Hashs mit dem neu generierten Hash
                if (hashedPassword.equals(expectedHash)) {
                    System.out.println("Login erfolgreich: " + this.Username);
                    return true; // Login erfolgreich
                }
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Exception: " + e.getMessage());
            System.out.println("Fehler beim Login: " + e.getMessage());
            return false;
        }
    }


    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Methode zum Hashen des Passworts
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());  // Salt zum Hashen hinzufügen
            byte[] hashedPassword = md.digest(password.getBytes());
            return salt + ":" + Base64.getEncoder().encodeToString(hashedPassword); // Salt und Hash speichern
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing-Algorithmus nicht gefunden", e);
        }
    }

    public static void main(String[] args) {
        User newUser = new User("testuser", "testpassword");
        System.out.println(newUser.register() ? "Registrierung erfolgreich!" : "Registrierung fehlgeschlagen!");

        User loginUser = new User("testuser", "testpassword");
        System.out.println(loginUser.login() ? "Login erfolgreich!" : "Login fehlgeschlagen!");
    }
}
