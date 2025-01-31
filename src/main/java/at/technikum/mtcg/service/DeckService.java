package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.http.Method;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.DeckController;

public class DeckService implements Service {
    private final DeckController deckController;

    public DeckService() {
        this.deckController = new DeckController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET && "/deck".equals(request.getServiceRoute())) {
            String token = request.getHeaderMap().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean asPlainText = "plain".equals(request.getParams());
            return deckController.getDeck(token, asPlainText);
        }

        if (request.getMethod() == Method.PUT && "/deck".equals(request.getServiceRoute())) {
            String token = request.getHeaderMap().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return deckController.configureDeck(token, request.getBody());
        }

        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Bad Request\"}");
    }
}
