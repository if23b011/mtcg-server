package at.technikum.mtcg.handlers;

import at.technikum.mtcg.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class SessionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // JSON Request Body lesen
            String requestBody = new String(exchange.getRequestBody().readAllBytes());

            // JSON deserialisieren
            User user = parseUserFromJson(requestBody);

            if (user != null && user.login()) {
                String response = "User logged in successfully.";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                String response = "Login failed: Invalid username or password.";
                exchange.sendResponseHeaders(401, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private User parseUserFromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, User.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Fehler beim Parsen der JSON-Anfrage: " + e.getMessage());
            return null;
        }
    }
}