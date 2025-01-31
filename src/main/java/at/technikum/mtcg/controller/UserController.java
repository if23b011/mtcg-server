package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.UserRepository;
import at.technikum.mtcg.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class UserController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository = new UserRepository();

    public Response getUser(String username, String token) {
        if (!userRepository.isValidToken(username, token)) {
            System.out.println("Validating token for user: " + username);
            System.out.println("Received token: " + token);

            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"error\":\"Access denied\"}");
        }

        User user = userRepository.getUser(username);
        if (user == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\":\"User not found\"}");
        }

        return new Response(HttpStatus.OK, ContentType.JSON, String.format("{\"Name\":\"%s\", \"Bio\":\"%s\", \"Image\":\"%s\"}", user.getUsername(), user.getBio(), user.getImage()));
    }

    public Response updateUser(String username, String token, String body) {
        if (!userRepository.isValidToken(username, token)) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"error\":\"Access denied\"}");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> updateData = objectMapper.readValue(body, Map.class);

            String name = updateData.get("Name");
            String bio = updateData.get("Bio");
            String image = updateData.get("Image");

            System.out.println("Parsed values - Name: " + name + ", Bio: " + bio + ", Image: " + image);

            if (name == null || bio == null || image == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Missing fields\"}");
            }

            return userRepository.updateUser(username, name, bio, image)
                    ? new Response(HttpStatus.OK, ContentType.JSON, "{\"message\":\"User updated successfully\"}")
                    : new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\":\"User not found\"}");
        } catch (IOException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Invalid JSON format\"}");
        }
    }

    public Response registerUser(Request request) {
        try {
            User user = objectMapper.readValue(request.getBody(), User.class);
            if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid input\"}");
            }

            if (userRepository.addUser(user)) {
                return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User created\"}");
            } else {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"error\": \"User already exists\"}");
            }
        } catch (IOException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid JSON format\"}");
        }
    }

    public Response loginUser(Request request) {
        try {
            Map<String, String> credentials = objectMapper.readValue(request.getBody(), Map.class);
            String username = credentials.get("Username");
            String password = credentials.get("Password");

            if (username == null || password == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid credentials\"}");
            }

            String token = userRepository.authenticateUser(username, password);
            if (token != null) {
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"token\": \"" + token + "\"}");
            } else {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Invalid username or password\"}");
            }
        } catch (IOException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid JSON format\"}");
        }
    }
}
