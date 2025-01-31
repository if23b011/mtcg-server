package at.technikum.mtcg.controller;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.dal.repository.PackageRepository;
import at.technikum.mtcg.models.card.Card;
import at.technikum.mtcg.models.Package;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PackageController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PackageRepository packageRepository = new PackageRepository();

    public Response createPackage(Request request) {
        String authHeader = request.getHeaderMap().getHeader("Authorization");
        if (authHeader == null || !authHeader.equals("Bearer admin-mtcgToken")) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"error\": \"Only admin can create packages\"}");
        }

        try {
            Card[] cardsArray = objectMapper.readValue(request.getBody(), Card[].class);
            List<Card> cards = Arrays.asList(cardsArray);

            if (cards.size() != 5) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"A package must contain exactly 5 cards\"}");
            }

            packageRepository.addPackage(new Package(cards));
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Package created\"}");
        } catch (IOException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid JSON format\"}");
        }
    }
}
