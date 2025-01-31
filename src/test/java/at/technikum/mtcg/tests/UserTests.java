package at.technikum.mtcg.tests;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.mtcg.controller.UserController;
import at.technikum.mtcg.models.User;
import at.technikum.mtcg.dal.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserTests {
    private UserController userController;
    private UserRepository userRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        userRepository = mock(UserRepository.class);
        userController = new UserController();
    }

    @Test
    void registerUser_validInput_userCreated() throws IOException {
        Request request = mock(Request.class);
        User user = new User(1, "testuser", "password", 20);
        when(request.getBody()).thenReturn("{\"username\":\"testuser\",\"password\":\"password\"}");
        when(objectMapper.readValue(request.getBody(), User.class)).thenReturn(user);
        when(userRepository.addUser(user)).thenReturn(true);

        Response response = userController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"message\": \"User created\"}", response.getBody());
    }

    @Test
    void registerUser_userAlreadyExists_conflict() throws IOException {
        Request request = mock(Request.class);
        User user = new User(1, "testuser", "password", 20);
        when(request.getBody()).thenReturn("{\"username\":\"testuser\",\"password\":\"password\"}");
        when(objectMapper.readValue(request.getBody(), User.class)).thenReturn(user);
        when(userRepository.addUser(user)).thenReturn(false);

        Response response = userController.registerUser(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"User already exists\"}", response.getBody());
    }

    @Test
    void registerUser_invalidInput_badRequest() throws IOException {
        Request request = mock(Request.class);
        when(request.getBody()).thenReturn("{\"username\":\"\",\"password\":\"\"}");
        User user = new User(1, "", "", 20);
        when(objectMapper.readValue(request.getBody(), User.class)).thenReturn(user);

        Response response = userController.registerUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"Invalid input\"}", response.getBody());
    }
    @Test
    void loginUser_validCredentials_returnsToken() throws IOException {
        Request request = mock(Request.class);
        when(request.getBody()).thenReturn("{\"Username\":\"testuser\",\"Password\":\"password\"}");
        when(objectMapper.readValue(request.getBody(), Map.class)).thenReturn(Map.of("Username", "testuser", "Password", "password"));
        when(userRepository.authenticateUser("testuser", "password")).thenReturn("validToken");

        Response response = userController.loginUser(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"token\": \"testuser-mtcgToken\"}", response.getBody());
    }

    @Test
    void loginUser_invalidCredentials_returnsUnauthorized() throws IOException {
        Request request = mock(Request.class);
        when(request.getBody()).thenReturn("{\"Username\":\"testuser\",\"Password\":\"wrongpassword\"}");
        when(objectMapper.readValue(request.getBody(), Map.class)).thenReturn(Map.of("Username", "testuser", "Password", "wrongpassword"));
        when(userRepository.authenticateUser("testuser", "wrongpassword")).thenReturn(null);

        Response response = userController.loginUser(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"Invalid username or password\"}", response.getBody());
    }

    @Test
    void loginUser_missingCredentials_returnsBadRequest() throws IOException {
        Request request = mock(Request.class);
        when(request.getBody()).thenReturn("{\"Username\":\"testuser\"}");
        when(objectMapper.readValue(request.getBody(), Map.class)).thenReturn(Map.of("Username", "testuser"));

        Response response = userController.loginUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\": \"Invalid credentials\"}", response.getBody());
    }
    @Test
    void updateUser_validTokenAndData_userUpdated() throws IOException {
        String username = "testuser";
        String token = "Bearer testuser-mtcgToken";
        String body = "{\"Name\":\"New Name\",\"Bio\":\"New Bio\",\"Image\":\"New Image\"}";
        when(userRepository.isValidToken(username, token)).thenReturn(true);
        when(objectMapper.readValue(body, Map.class)).thenReturn(Map.of("Name", "New Name", "Bio", "New Bio", "Image", "New Image"));
        when(userRepository.updateUser(username, "New Name", "New Bio", "New Image")).thenReturn(true);

        Response response = userController.updateUser(username, token, body);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"message\":\"User updated successfully\"}", response.getBody());
    }

    @Test
    void updateUser_invalidToken_accessDenied() {
        String username = "testuser";
        String token = "Bearer invalidToken";
        String body = "{\"Name\":\"New Name\",\"Bio\":\"New Bio\",\"Image\":\"New Image\"}";
        when(userRepository.isValidToken(username, token)).thenReturn(false);

        Response response = userController.updateUser(username, token, body);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\":\"Access denied\"}", response.getBody());
    }

    @Test
    void updateUser_missingFields_badRequest() throws IOException {
        String username = "testuser";
        String token = "Bearer testuser-mtcgToken";
        String body = "{\"Name\":\"New Name\"}";
        when(userRepository.isValidToken(username, token)).thenReturn(true);
        when(objectMapper.readValue(body, Map.class)).thenReturn(Map.of("Name", "New Name"));

        Response response = userController.updateUser(username, token, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals(ContentType.JSON, response.getContentType());
        assertEquals("{\"error\":\"Missing fields\"}", response.getBody());
    }
}
