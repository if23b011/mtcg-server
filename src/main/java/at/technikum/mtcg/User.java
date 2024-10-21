package at.technikum.mtcg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public boolean register() {
    String checkUserSQL = "SELECT * FROM users WHERE username = ?";
    String insertUserSQL = "INSERT INTO users (username, password, coins) VALUES (?, ?, 20)";

    try (Connection conn = DatabaseConnector.connect()) {
        System.out.println("Datenbankverbindung hergestellt.");

        try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSQL)) {
            checkStmt.setString(1, this.Username);
            ResultSet rs = checkStmt.executeQuery();
            System.out.println("Überprüfe, ob der Benutzername bereits vergeben ist.");

            if (rs.next()) {
                System.out.println("Benutzername bereits vergeben: " + this.Username);
                return false;
            }
        }

        try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSQL)) {
            insertStmt.setString(1, this.Username);
            insertStmt.setString(2, this.Password); // TODO: Passwort verschlüsseln!
            System.out.println("Füge neuen Benutzer in die Datenbank ein.");
            insertStmt.executeUpdate();
        }

        System.out.println("Benutzer erfolgreich registriert.");
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Fehler bei der Benutzerregistrierung: " + e.getMessage());
        return false;
    }
}


    public boolean login() {
        String selectUserSQL = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(selectUserSQL)) {

            stmt.setString(1, this.Username);
            stmt.setString(2, this.Password); // Passwort verschlüsseln!

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
