package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.TradingRepository;
import at.technikum.mtcg.models.TradingDeal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TradingController {
    private final TradingRepository repository = new TradingRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response getAllTradingDeals() {
        try {
            List<TradingDeal> deals = repository.getAllTradingDeals();
            return new Response(HttpStatus.OK, ContentType.JSON, objectMapper.writeValueAsString(deals));
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "[error retrieving trading deals]");
        }
    }

    public Response createTradingDeal(Request request) {
        try {
            System.out.println("Received JSON: " + request.getBody());  // Debugging

            TradingDeal deal = objectMapper.readValue(request.getBody(), TradingDeal.class);
            repository.addTradingDeal(deal);
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\":\"Trading deal created\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[error creating trading deal]");
        }
    }


    public Response deleteTradingDeal(String id) {
        try {
            repository.deleteTradingDeal(id);
            return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "[error deleting trading deal]");
        }
    }

    public Response tradeCard(String dealId, Request request) {
        try {
            System.out.println("Received trade request: " + request.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;

            if (request.getBody().startsWith("\"") && request.getBody().endsWith("\"")) {
                String offeredCardId = objectMapper.readValue(request.getBody(), String.class);
                jsonNode = objectMapper.createObjectNode().put("offeredCardId", offeredCardId);
            } else {
                jsonNode = objectMapper.readTree(request.getBody());
            }

            if (!jsonNode.has("offeredCardId")) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[error: missing offeredCardId]");
            }

            String offeredCardId = jsonNode.get("offeredCardId").asText();
            System.out.println("Extracted offeredCardId: " + offeredCardId);

            String token = request.getHeaderMap().getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "[error: missing authentication]");
            }

            token = token.replace("Bearer ", "").trim();
            String username = token.split("-")[0];

            String dealOwner = repository.getTradingDealOwner(dealId);
            System.out.println("Deal Owner: " + dealOwner);
            System.out.println("Current User: " + username);

            if (dealOwner == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "[error: trading deal not found]");
            }

            if (dealOwner.equals(username)) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "[error: cannot trade with yourself]");
            }

            boolean success = repository.executeTrade(dealId, offeredCardId, username);

            if (success) {
                return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Trade successful\"}");
            } else {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[error executing trade]");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "[error trading card]");
        }
    }



}
