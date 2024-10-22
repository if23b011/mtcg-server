package at.technikum.mtcg;

import at.technikum.mtcg.handlers.SessionHandler;
import at.technikum.mtcg.handlers.UserHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);
        server.createContext("/users", new UserHandler());
        server.createContext("/sessions", new SessionHandler());
        server.setExecutor(null); // creates a default executor


        server.start();
        System.out.println("Server running on port 10001...");
    }
}

