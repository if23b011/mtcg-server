package at.technikum.mtcg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean register() {
        String insertUserSQL = "INSERT INTO users (username, password, coins) VALUES (?, ?, 20)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(insertUserSQL)) {

            stmt.setString(1, this.username);
            stmt.setString(2, this.password); //TODO: Passwort verschlüsseln!

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }
    public boolean login() {
        String selectUserSQL = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(selectUserSQL)) {

            stmt.setString(1, this.username);
            stmt.setString(2, this.password); // Passwort verschlüsseln!

            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        User newUser = new User("testuser", "testpassword");
        System.out.println(newUser.register() ? "Registrierung erfolgreich!" : "Registrierung fehlgeschlagen!");

        User loginUser = new User("testuser", "testpassword");
        System.out.println(loginUser.login() ? "Login erfolgreich!" : "Login fehlgeschlagen!");
    }
}
