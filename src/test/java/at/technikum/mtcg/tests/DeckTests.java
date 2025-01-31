package at.technikum.mtcg.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.controller.DeckController;
import at.technikum.mtcg.dal.repository.DeckRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckTests {

    private DeckController deckController;
    private DeckRepository deckRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deckRepository = mock(DeckRepository.class);
        objectMapper = mock(ObjectMapper.class);
        deckController = new DeckController();
    }

    @Test
    void configureDeck_invalidToken_unauthorized() {
        String token = "";
        String requestBody = "[\"card1\", \"card2\", \"card3\", \"card4\"]";

        Response response = deckController.configureDeck(token, requestBody);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\":\"Unauthorized\"}", response.getBody());
    }

    @Test
    void configureDeck_invalidCardCount_badRequest() throws Exception {
        String token = "testuser-mtcgToken";
        String requestBody = "[\"card1\", \"card2\"]";
        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(requestBody)).thenReturn(jsonNode);
        when(jsonNode.isArray()).thenReturn(true);
        when(jsonNode.size()).thenReturn(2);

        Response response = deckController.configureDeck(token, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\":\"Deck must contain exactly 4 cards\"}", response.getBody());
    }

}