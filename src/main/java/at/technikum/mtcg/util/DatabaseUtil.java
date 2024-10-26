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
                    System.out.println("Username already taken: " + user.getUsername());
                    return false;
                }
            }

            // Generate salt and hash password
            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSQL)) {
                insertStmt.setString(1, user.getUsername());
                insertStmt.setString(2, hashedPassword);
                insertStmt.executeUpdate();
            }

            System.out.println("User successfully registered");
            return true;

        } catch (SQLException e) {
            System.err.println("Error during user registration: " + e.getMessage());
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

                // Separate salt and hash
                String[] parts = storedHash.split(":");
                if (parts.length != 2) {
                    throw new RuntimeException("Incorrect password format in the database");
                }

                String salt = parts[0];
                String expectedHash = parts[1];

                // Hash the password again with the extracted salt
                String hashedPassword = PasswordUtil.hashPassword(user.getPassword(), salt).split(":")[1];

                // Comparison of the stored hash with the newly generated hash
                if (hashedPassword.equals(expectedHash)) {
                    System.out.println("Login successful: " + user.getUsername());
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Exception: " + e.getMessage());
            System.out.println("Error logging in: " + e.getMessage());
            return false;
        }
    }
}