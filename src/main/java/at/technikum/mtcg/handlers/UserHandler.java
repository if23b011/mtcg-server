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
                        String response = "HTTP/1.1 201 Created: User registered successfully.\n";
                        exchange.sendResponseHeaders(201, response.getBytes().length);
                        writeResponse(exchange, response);
                    } else {
                        String response = "HTTP/1.1 409 Conflict: User already exists.\n";
                        exchange.sendResponseHeaders(409, response.getBytes().length);
                        writeResponse(exchange, response);
                    }
                } catch (Exception e) {
                    String response = "HTTP/1.1 500 Internal Server Error: " + e.getMessage() + "\n";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    writeResponse(exchange, response);
                }
            } else {
                String response = "HTTP/1.1 400 Bad Request: Invalid JSON format.\n";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                writeResponse(exchange, response);
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private static void writeResponse(HttpExchange exchange, String response) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private User parseUserFromJson(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, User.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Fehler beim Parsen der JSON-Anfrage: " + e.getMessage() + "\n");
            return null;
        }
    }
}