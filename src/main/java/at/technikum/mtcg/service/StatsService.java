package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.StatsController;

public class StatsService implements Service {
    private final StatsController statsController;

    public StatsService() {
        this.statsController = new StatsController();
    }

    @Override
    public Response handleRequest(Request request) {
        String token = request.getHeaderMap().getHeader("Authorization");

        if (request.getPathname().equals("/stats")) {
            return statsController.getUserStats(token);
        } else if (request.getPathname().equals("/scoreboard")) {
            return statsController.getScoreboard();
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{}" );
    }
}