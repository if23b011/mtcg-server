package at.technikum.mtcg.server;

import at.technikum.mtcg.database.DatabaseConnector;
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
        server.setExecutor(null); // creates a default executor


        server.start();
        System.out.println("Server running on port 10001...");
        //try Database Connection every 5 seconds
        while (true) {
            try {
                DatabaseConnector.connect();
                break;
            } catch (Exception e) {
                System.out.println("Database not reachable, retrying in 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("Database connected...");
    }
}

