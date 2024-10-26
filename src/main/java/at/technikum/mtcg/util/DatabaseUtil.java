package at.technikum.mtcg.util;

import at.technikum.mtcg.database.DatabaseConnector;
import at.technikum.mtcg.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    public static boolean registerUser(User user) {
        String checkUserSQL = "SELECT * FROM users WHERE username = ?";
        String insertUserSQL = "INSERT INTO users (username, password, coins) VALUES (?, ?, 20)";

        try (Connection conn = DatabaseConnector.connect()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSQL)) {
                checkStmt.setString(1, user.getUsername());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Benutzername bereits vergeben: " + user.getUsername());
                    return false;
                }
            }

            // Salt generieren und Passwort hashen
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSQL)) {
                insertStmt.setString(1, user.getUsername());
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

    public static boolean loginUser(User user) {
        String selectUserSQL = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(selectUserSQL)) {

            stmt.setString(1, user.getUsername());
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
                String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt).split(":")[1];

                // Vergleich des gespeicherten Hashs mit dem neu generierten Hash
                if (hashedPassword.equals(expectedHash)) {
                    System.out.println("Login erfolgreich: " + user.getUsername());
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
}