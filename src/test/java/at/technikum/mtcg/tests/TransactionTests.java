package at.technikum.mtcg.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.HeaderMap;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.controller.TransactionController;
import at.technikum.mtcg.dal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionTests {

    private TransactionController transactionController;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        transactionController = new TransactionController();
    }

    @Test
    void buyPackage_validAuthorization_packagePurchased() {
        Request request = mock(Request.class);
        HeaderMap headerMap = mock(HeaderMap.class);
        String authHeader = "Bearer testuser-mtcgToken";
        when(request.getHeaderMap()).thenReturn(headerMap);
        when(headerMap.getHeader("Authorization")).thenReturn(authHeader);
        when(userRepository.buyPackage("testuser")).thenReturn(true);

        Response response = transactionController.buyPackage(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"message\": \"Package successfully purchased\"}", response.getBody());
    }

    @Test
    void buyPackage_noAuthorizationHeader_unauthorized() {
        Request request = mock(Request.class);
        HeaderMap headerMap = mock(HeaderMap.class);
        when(request.getHeaderMap()).thenReturn(headerMap);
        when(headerMap.getHeader("Authorization")).thenReturn(null);

        Response response = transactionController.buyPackage(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"Unauthorized\"}", response.getBody());
    }

    @Test
    void buyPackage_notEnoughMoneyOrNoPackages_badRequest() {
        Request request = mock(Request.class);
        HeaderMap headerMap = mock(HeaderMap.class);
        String authHeader = "Bearer testuser-mtcgToken";
        when(request.getHeaderMap()).thenReturn(headerMap);
        when(headerMap.getHeader("Authorization")).thenReturn(authHeader);
        when(userRepository.buyPackage("testuser")).thenReturn(false);

        Response response = transactionController.buyPackage(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"Not enough money or no packages available\"}", response.getBody());
    }
}