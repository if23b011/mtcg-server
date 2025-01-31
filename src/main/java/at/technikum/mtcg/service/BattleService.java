package at.technikum.mtcg.service;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;
import at.technikum.httpserver.server.Request;
import at.technikum.httpserver.server.Response;
import at.technikum.httpserver.server.Service;
import at.technikum.mtcg.controller.BattleController;

public class BattleService implements Service {
    private final BattleController battleController;

    public BattleService() {
        this.battleController = new BattleController();
    }

    @Override
    public Response handleRequest(Request request) {
        String token = request.getHeaderMap().getHeader("Authorization");

        if (request.getPathname().equals("/battles") && request.getMethod().name().equals("POST")) {
            return battleController.startBattle(token);
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{}" );
    }
}

