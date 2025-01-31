package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.TransactionController;

public class TransactionService implements Service {
    private final TransactionController transactionController;

    public TransactionService() {
        this.transactionController = new TransactionController();
    }

    @Override
    public Response handleRequest(Request request) {

        if ("POST".equalsIgnoreCase(request.getMethod().toString()) && request.getPathname().trim().equalsIgnoreCase("/transactions/packages")) {
            return this.transactionController.buyPackage(request);
        }

        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\": \"Invalid request\"}");
    }
}
