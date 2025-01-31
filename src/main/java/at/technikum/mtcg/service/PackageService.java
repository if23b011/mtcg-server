package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.http.Method;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.PackageController;

public class PackageService implements Service {
    private final PackageController packageController;

    public PackageService() {
        this.packageController = new PackageController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST && "/packages".equals(request.getServiceRoute())) {
            return this.packageController.createPackage(request);
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request\"}");
    }
}
