package at.technikum.mtcg.handlers;

import at.technikum.mtcg.User;
import at.technikum.mtcg.util.JsonUtil;
import at.technikum.mtcg.util.ResponseUtil;
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
            User user = JsonUtil.parseUserFromJson(requestBody);

            if (user != null && user.login()) {
                // Token generieren
                String token = user.generateAuthToken();

                // Antwort vorbereiten
                String response = "User logged in successfully. Token: " + token + "\n";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                ResponseUtil.writeResponse(exchange, response);
            } else {
                String response = "HTTP/1.1 401 Unauthorized: Invalid credentials.\n";
                exchange.sendResponseHeaders(401, response.getBytes().length);
                ResponseUtil.writeResponse(exchange, response);
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}