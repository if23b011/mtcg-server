package at.technikum.mtcg.handlers;

import at.technikum.mtcg.models.User;
import at.technikum.mtcg.utils.ResponseUtil;
import at.technikum.mtcg.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class SessionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Read JSON Request Body
            String requestBody = new String(exchange.getRequestBody().readAllBytes());

            // deserialize JSON
            User user = JsonUtil.parseUserFromJson(requestBody);

            String token = user != null ? user.login() : null;
            String response;
            if (token != null) {
                // prepare an answer
                response = "HTTP/1.1 200 OK: User logged in successfully. Token: " + token + "\n";
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
                response = "HTTP/1.1 401 Unauthorized: Invalid credentials.\n";
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }
            ResponseUtil.writeResponse(exchange, response);

        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}