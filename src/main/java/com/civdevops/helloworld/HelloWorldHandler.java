package com.civdevops.helloworld;

import com.civdevops.helloworld.dto.ErrorResponse;
import com.civdevops.helloworld.dto.MessageResponse;
import com.civdevops.helloworld.service.GreetingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class HelloWorldHandler implements HttpHandler {
    private final GreetingService service;

    public HelloWorldHandler(GreetingService service) {
        this.service = service;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");

                sendResponse(exchange, 405, new ErrorResponse(Constants.INVALID_INPUT));
                return;
            }

            try {
                String name = getNameParameter(exchange.getRequestURI().getRawQuery());

                String greeting = service.greet(name);

                sendResponse(exchange, 200,  new MessageResponse(greeting));
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, 400,  new ErrorResponse(Constants.INVALID_INPUT));
            }
        }
    }

    private void sendResponse( HttpExchange exchange, int status, Object response) throws IOException {

        byte[] body  =  OBJECT_MAPPER.writeValueAsBytes(response);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, body .length);

        try (var output = exchange.getResponseBody()) {
            output.write(body );
        }
    }


    private String getNameParameter(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        String name = null;
        boolean found = false;

        for (String parameter : query.split("&", -1)) {
            String[] pair = parameter.split("=", 2);

            String key = URLDecoder.decode(
                    pair[0], StandardCharsets.UTF_8);

            if ("name".equals(key)) {
                if (found) {
                    throw new IllegalArgumentException(Constants.INVALID_INPUT);
                }

                found = true;

                name = URLDecoder.decode(
                        pair.length == 2 ? pair[1] : "",
                        StandardCharsets.UTF_8);
            }
        }

        return name;
    }
}
