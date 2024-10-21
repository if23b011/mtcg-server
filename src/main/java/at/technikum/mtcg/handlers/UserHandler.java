package at.technikum.mtcg.handlers;

import at.technikum.mtcg.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class UserHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // JSON Request Body lesen
            String requestBody = new String(exchange.getRequestBody().readAllBytes());

            // JSON deserialisieren über die parseUserFromJson-Methode
            User user = parseUserFromJson(requestBody);

            if (user != null && user.getUsername() != null && user.getPassword() != null) {
                try {
                    if (user.register()) {
                        String response = "User registered successfully.";
                        exchange.sendResponseHeaders(201, response.getBytes().length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        String response = "User registration failed: Username already exists.";
                        exchange.sendResponseHeaders(409, response.getBytes().length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                } catch (Exception e) {
                    String response = "User registration failed due to an internal error: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                String response = "Invalid request body.";
                exchange.sendResponseHeaders(400, response.getBytes().length);
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