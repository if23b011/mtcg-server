package at.technikum.httpserver.server;

public interface Service {
    Response handleRequest(Request request);
}
