package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.DeckRepository;
import at.technikum.mtcg.models.card.Card;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeckController {
    private final DeckRepository deckRepository = new DeckRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response getDeck(String token, boolean asPlainText) {
        if (token == null || token.isEmpty()) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\":\"Unauthorized\"}");
        }

        String username = token.split("-")[0];

        try {
            List<Card> cards = deckRepository.getDeckByOwner(username);
            if (asPlainText) {
                StringBuilder plainTextDeck = new StringBuilder();
                for (Card card : cards) {
                    plainTextDeck.append(String.format("ID: %s | Name: %s | Damage: %.2f | Element: %s\n",
                            card.getId(), card.getName(), card.getDamage(), card.getElement()));
                }
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, plainTextDeck.toString());
            } else {
                String jsonResponse = objectMapper.writeValueAsString(cards);
                return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
            }
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\":\"Unexpected error\"}");
        }
    }

    public Response configureDeck(String token, String requestBody) {
        if (token == null || token.isEmpty()) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\":\"Unauthorized\"}");
        }

        String username = token.split("-")[0];

        try {
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            if (!jsonNode.isArray() || jsonNode.size() != 4) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Deck must contain exactly 4 cards\"}");
            }

            List<String> cardIds = new ArrayList<>();
            for (JsonNode node : jsonNode) {
                cardIds.add(node.asText());
            }

            boolean success = deckRepository.configureDeck(username, cardIds);
            if (!success) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"error\":\"One or more cards do not belong to user\"}");
            }

            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\":\"Deck configured successfully\"}");
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON,
                    "{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Invalid JSON format\"}");
        }
    }
}
