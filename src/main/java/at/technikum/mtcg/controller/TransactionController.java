package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.UserRepository;

public class TransactionController {
    private final UserRepository userRepository = new UserRepository();

    public Response buyPackage(Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Unauthorized\"}");
        }

        String username = authHeader.replace("Bearer ", "").split("-")[0];

        boolean success = userRepository.buyPackage(username);
        if (success) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Package successfully purchased\"}");
        } else {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Not enough money or no packages available\"}");
        }
    }
}
