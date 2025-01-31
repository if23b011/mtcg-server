package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.http.Method;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.CardController;

public class CardService implements Service {
    private final CardController cardController;

    public CardService() {
        this.cardController = new CardController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET && "/cards".equals(request.getServiceRoute())) {
            String token = request.getHeaderMap().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return cardController.getAllCards(token);
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Bad Request\"}");
    }
}
