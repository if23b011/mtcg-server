package at.technikum.mtcg.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.HeaderMap;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.controller.PackageController;
import at.technikum.mtcg.dal.repository.PackageRepository;
import at.technikum.mtcg.models.card.Card;
import at.technikum.mtcg.models.Package;
import at.technikum.mtcg.models.card.ElementType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

public class PackageTests {

    private PackageController packageController;
    private PackageRepository packageRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        packageRepository = mock(PackageRepository.class);
        objectMapper = mock(ObjectMapper.class);
        packageController = new PackageController();
    }

    @Test
    void createPackage_validRequest_packageCreated() throws IOException {
        Request request = mock(Request.class);
        HeaderMap headerMapMock = mock(HeaderMap.class);
        String authHeader = "Bearer admin-mtcgToken";

        when(request.getHeaderMap()).thenReturn(headerMapMock);
        when(headerMapMock.getHeader("Authorization")).thenReturn(authHeader);

        String body = "[{\"Id\":\"" + UUID.randomUUID() + "\",\"Name\":\"Card1\",\"Damage\":10.0},{\"Id\":\"" + UUID.randomUUID() + "\",\"Name\":\"Card2\",\"Damage\":10.0},{\"Id\":\"" + UUID.randomUUID() + "\",\"Name\":\"Card3\",\"Damage\":10.0},{\"Id\":\"" + UUID.randomUUID() + "\",\"Name\":\"Card4\",\"Damage\":10.0},{\"Id\":\"" + UUID.randomUUID() + "\",\"Name\":\"Card5\",\"Damage\":10.0}]";
        when(request.getBody()).thenReturn(body);

        Card[] cardsArray = new Card[]{
                new Card(UUID.randomUUID(), "Card1", 10.0, ElementType.FIRE),
                new Card(UUID.randomUUID(), "Card2", 10.0, ElementType.WATER),
                new Card(UUID.randomUUID(), "Card3", 10.0, ElementType.NORMAL),
                new Card(UUID.randomUUID(), "Card4", 10.0, ElementType.NORMAL),
                new Card(UUID.randomUUID(), "Card5", 10.0, ElementType.FIRE)
        };
        when(objectMapper.readValue(body, Card[].class)).thenReturn(cardsArray);

        Response response = packageController.createPackage(request);
        System.out.println("Response Status: " + response.getStatus());
        System.out.println("Response Body: " + response.getBody());


        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"message\": \"Package created\"}", response.getBody());
    }


    @Test
    void createPackage_invalidAuthorization_forbidden() {
        Request request = mock(Request.class);
        HeaderMap headerMapMock = mock(HeaderMap.class);

        String authHeader = "Bearer invalidToken";
        when(request.getHeaderMap()).thenReturn(headerMapMock);
        when(headerMapMock.getHeader("Authorization")).thenReturn(authHeader);
        String body = "[{\"id\":\"1\",\"name\":\"Card1\"},{\"id\":\"2\",\"name\":\"Card2\"},{\"id\":\"3\",\"name\":\"Card3\"},{\"id\":\"4\",\"name\":\"Card4\"},{\"id\":\"5\",\"name\":\"Card5\"}]";

        when(request.getHeaderMap().getHeader("Authorization")).thenReturn(authHeader);
        when(request.getBody()).thenReturn(body);

        Response response = packageController.createPackage(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"Only admin can create packages\"}", response.getBody());
        verify(packageRepository, never()).addPackage(any(Package.class));
    }

    @Test
    void createPackage_invalidCardCount_badRequest() throws IOException {
        Request request = mock(Request.class);
        HeaderMap headerMapMock = mock(HeaderMap.class);

        String authHeader = "Bearer admin-mtcgToken";
        when(request.getHeaderMap()).thenReturn(headerMapMock);
        when(headerMapMock.getHeader("Authorization")).thenReturn(authHeader);
        String body = "[{\"id\":\"1\",\"name\":\"Card1\"},{\"id\":\"2\",\"name\":\"Card2\"}]";
        Card[] cardsArray = new Card[]{
                new Card(UUID.randomUUID(), "Card1", 10.0, ElementType.FIRE),
                new Card(UUID.randomUUID(), "Card2", 10.0, ElementType.WATER)
        };

        when(request.getHeaderMap().getHeader("Authorization")).thenReturn(authHeader);
        when(request.getBody()).thenReturn(body);
        when(objectMapper.readValue(body, Card[].class)).thenReturn(cardsArray);

        Response response = packageController.createPackage(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"Invalid JSON format\"}", response.getBody());
        verify(packageRepository, never()).addPackage(any(Package.class));
    }
}