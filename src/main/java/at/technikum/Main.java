package at.technikum;

import at.technikum.httpserver.server.Server;
import at.technikum.httpserver.utils.Router;
import at.technikum.mtcg.service.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/sessions", new UserService());
        router.addService("/cards", new CardService());
        router.addService("/packages", new PackageService());
        router.addService("/transactions/packages", new TransactionService());
        router.addService("/cards", new CardService());
        router.addService("/deck", new DeckService());
        router.addService("/stats", new StatsService());
        router.addService("/scoreboard", new StatsService());
        router.addService("/battles", new BattleService());
        router.addService("/tradings", new TradingService());

        return router;
    }
}
