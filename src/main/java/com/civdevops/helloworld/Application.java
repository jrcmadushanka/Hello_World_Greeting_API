package com.civdevops.helloworld;

import com.civdevops.helloworld.service.GreetingService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.civdevops.helloworld.Constants.PORT;

public final class Application {

    private Application() {}

    public static void main(String[] args) throws IOException {
        HttpServer server = createServer(PORT);
        server.start();

        System.out.println("Server running on http://localhost:" + PORT);
    }

    public static HttpServer createServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        GreetingService service = new GreetingService();
        HelloWorldHandler handler = new HelloWorldHandler(service);

        server.createContext("/hello-world", handler);

        return server;
    }
}
