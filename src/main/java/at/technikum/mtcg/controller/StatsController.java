package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.StatsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class StatsController {
    private final StatsRepository statsRepository = new StatsRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response getUserStats(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\":\"Unauthorized\"}");
        }
        token = token.substring(7);

        try {
            Map<String, Object> userStats = statsRepository.getUserStats(token);
            if (userStats.isEmpty()) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"error\":\"User not found\"}");
            }
            String jsonResponse = objectMapper.writeValueAsString(userStats);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\":\"Database error\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\":\"Unexpected error\"}");
        }
    }

    public Response getScoreboard() {
        try {
            List<Map<String, Object>> scoreboard = statsRepository.getScoreboard();
            String jsonResponse = objectMapper.writeValueAsString(scoreboard);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\":\"Database error\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\":\"Unexpected error\"}");
        }
    }
}
