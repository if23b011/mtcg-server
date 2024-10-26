package at.technikum.mtcg.handlers;

import at.technikum.mtcg.models.User;
import at.technikum.mtcg.util.ResponseUtil;
import at.technikum.mtcg.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class UserHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // JSON Request Body lesen
            String requestBody = new String(exchange.getRequestBody().readAllBytes());

            // JSON deserialisieren über die parseUserFromJson-Methode
            User user = JsonUtil.parseUserFromJson(requestBody);

            if (user != null && user.getUsername() != null && user.getPassword() != null) {
                try {
                    if (user.register()) {
                        String response = "HTTP/1.1 201 Created: User registered successfully.\n";
                        exchange.sendResponseHeaders(201, response.getBytes().length);
                        ResponseUtil.writeResponse(exchange, response);
                    } else {
                        String response = "HTTP/1.1 409 Conflict: User already exists.\n";
                        exchange.sendResponseHeaders(409, response.getBytes().length);
                        ResponseUtil.writeResponse(exchange, response);
                    }
                } catch (Exception e) {
                    String response = "HTTP/1.1 500 Internal Server Error: " + e.getMessage() + "\n";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    ResponseUtil.writeResponse(exchange, response);
                }
            } else {
                String response = "HTTP/1.1 400 Bad Request: Invalid JSON format.\n";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                ResponseUtil.writeResponse(exchange, response);
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}