package at.technikum.mtcg.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.controller.StatsController;
import at.technikum.mtcg.dal.repository.StatsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsTests {

    private StatsController statsController;
    private StatsRepository statsRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        statsRepository = mock(StatsRepository.class);
        objectMapper = mock(ObjectMapper.class);
        statsController = new StatsController();
    }

    @Test
    void getUserStats_validToken_userStatsReturned() throws Exception {
        String token = "Bearer testuser-mtcgToken";
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("elo", 100);
        userStats.put("games_played", 0);

        when(statsRepository.getUserStats("testuser-mtcgToken")).thenReturn(userStats);
        when(objectMapper.writeValueAsString(userStats)).thenReturn("{\"elo\":100,\"games_played\":0}");

        System.out.println("Mocked userStats: " + statsRepository.getUserStats("testuser-mtcgToken"));

        Response response = statsController.getUserStats(token);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"elo\":100,\"games_played\":0,\"username\":\"testuser\"}", response.getBody());
    }


    @Test
    void getScoreboard_validRequest_scoreboardReturned() throws Exception {
        List<Map<String, Object>> scoreboard = List.of(
                Map.of("username", "testuser", "elo", 100, "games_played", 0)
        );

        when(statsRepository.getScoreboard()).thenReturn(scoreboard);
        when(objectMapper.writeValueAsString(scoreboard)).thenReturn(
                "[{\"elo\":100,\"username\":\"testuser\"}]"
        );

        Response response = statsController.getScoreboard();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals(
                "[{\"elo\":100,\"username\":\"testuser\"}]",
                response.getBody()
        );
    }



    @Test
    void getUserStats_invalidToken_unauthorized() {
        String token = "invalidToken";

        Response response = statsController.getUserStats(token);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\":\"Unauthorized\"}", response.getBody());
    }

    @Test
    void getUserStats_userNotFound_notFound() throws Exception {
        String token = "Bearer validToken";
        when(statsRepository.getUserStats("validToken")).thenReturn(new HashMap<>());

        Response response = statsController.getUserStats(token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\":\"User not found\"}", response.getBody());
    }
}