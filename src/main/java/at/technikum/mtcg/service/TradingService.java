package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.http.Method;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.TradingController;

public class TradingService implements Service {
    private final TradingController tradingController;

    public TradingService() {
        this.tradingController = new TradingController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET && "/tradings".equals(request.getServiceRoute())) {
            return this.tradingController.getAllTradingDeals();
        } else if (request.getMethod() == Method.POST && "/tradings".equals(request.getServiceRoute())) {
            return this.tradingController.createTradingDeal(request);
        } else if (request.getMethod() == Method.DELETE && request.getPathParts().size() > 1) {
            return this.tradingController.deleteTradingDeal(request.getPathParts().get(1));
        } else if (request.getMethod() == Method.POST && request.getPathParts().size() > 1) {
            return this.tradingController.tradeCard(request.getPathParts().get(1), request);
        }

        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[trading]");
    }

}
