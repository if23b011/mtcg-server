package at.technikum.mtcg;

import at.technikum.mtcg.handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);
        server.createContext("/users", new UserHandler());
        server.createContext("/sessions", new SessionHandler());
        server.createContext("/packages", new PackageHandler());
        server.createContext("/deck", new DeckHandler());
        server.createContext("/battle", new BattleHandler());
        server.createContext("/scoreboard", new ScoreboardHandler());
        server.createContext("/trades", new TradeHandler());
        server.createContext("/stack", new StatsHandler());
        server.createContext("/profile", new ProfileHandler());
        server.setExecutor(null); // creates a default executorgi


        server.start();
        System.out.println("Server running on port 10001...");
    }
}

