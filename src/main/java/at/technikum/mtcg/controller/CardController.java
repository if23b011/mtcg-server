package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.CardRepository;
import at.technikum.mtcg.models.card.Card;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CardController {
    private final CardRepository cardRepository = new CardRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response getAllCards(String token) {
        if (token == null || token.isEmpty()) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\":\"Unauthorized\"}");
        }

        String username = token.split("-")[0];

        try {
            List<Card> cards = cardRepository.getCardsByOwner(username);
            String jsonResponse = objectMapper.writeValueAsString(cards);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\":\"Database error\"}");
        }
    }
}
