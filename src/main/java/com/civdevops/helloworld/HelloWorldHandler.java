package com.civdevops.helloworld;

import com.civdevops.helloworld.service.GreetingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class HelloWorldHandler implements HttpHandler {
    private final GreetingService service;

    public HelloWorldHandler(GreetingService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");

                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            try {
                String name = getNameParameter(exchange.getRequestURI().getRawQuery());

                String greeting = service.greet(name);

                sendResponse(exchange, 200, "{\"message\":\"" + escapeJson(greeting) + "\"}");
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, 400, "{\"error\":\"" + Constants.INVALID_INPUT + "\"}");
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String json) throws IOException {

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
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

    private static String escapeJson(String value) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);

            switch (character) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> {
                    if (character < 0x20) {
                        result.append(String.format(
                                "\\u%04x", (int) character));
                    } else {
                        result.append(character);
                    }
                }
            }
        }

        return result.toString();
    }
}
