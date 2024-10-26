package at.technikum.mtcg;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    public static Connection connect() throws SQLException {
        Properties props = new Properties();

        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Konfigurationsdatei 'config.properties' wurde nicht gefunden");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden der Konfigurationsdatei", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}
