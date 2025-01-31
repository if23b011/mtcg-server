package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.http.Method;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.UserController;

public class UserService implements Service {
    private final UserController userController;

    public UserService() {
        this.userController = new UserController();
    }

    @Override
    public Response handleRequest(Request request) {

        String token = request.getHeaderMap().getHeader("Authorization");


        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userController.getUser(request.getPathParts().get(1), token);
        } else if (request.getMethod() == Method.PUT && request.getPathParts().size() > 1) {
            System.out.println("Received PUT request body: " + request.getBody());
            return this.userController.updateUser(request.getPathParts().get(1), token, request.getBody());
        } else if (request.getMethod() == Method.POST && "/users".equals(request.getServiceRoute())) {
            return this.userController.registerUser(request);
        } else if (request.getMethod() == Method.POST && "/sessions".equals(request.getServiceRoute())) {
            return this.userController.loginUser(request);
        }


        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[user]");
    }
}
