package com.civdevops.helloworld.service;

import com.civdevops.helloworld.Application;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldIntegrationTest {

    private HttpServer server;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        server = Application.createServer(0);
        server.start();

        client = HttpClient.newHttpClient();
        baseUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
        if (client != null) {
            client.close();
        }
    }

    @Test
    void shouldReturnGreetingForValidName() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/hello-world?name=amith")).GET().build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"message\":\"Hello Amith\"}", response.body());
    }

    @Test
    void shouldRejectMissingAndEmptyName() throws Exception {
        assertInvalidInput("");
        assertInvalidInput("?name=");
        assertInvalidInput("?name=%20%20");
    }

    @Test
    void shouldRejectNamesOutsideAllowedAlphabet() throws Exception {
        assertInvalidInput("?name=Naya");
        assertInvalidInput("?name=zara");
        assertInvalidInput("?name=123");
        assertInvalidInput("?name=%21amith");
    }

    @Test
    void shouldRejectDuplicateNameParameters() throws Exception {
        assertInvalidInput("?name=alice&name=banuka");
    }

    @Test
    void shouldRejectMalformedUrlEncoding() throws Exception {
        assertInvalidInput("?name=%FF");
    }

    @Test
    void shouldEscapeJsonSpecialCharacters() throws Exception {
        HttpResponse<String> response = get("?name=a%22b%5Cc");

        assertEquals(200, response.statusCode());
        assertEquals("{\"message\":\"Hello A\\\"b\\\\c\"}", response.body());
    }

    @Test
    void shouldIgnoreUnrelatedQueryParameters() throws Exception {
        HttpResponse<String> response = get("?lang=en&name=amith");

        assertEquals(200, response.statusCode());
        assertEquals("{\"message\":\"Hello Amith\"}", response.body());
    }

    @Test
    void shouldRejectUnsupportedHttpMethods() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/hello-world?name=amith"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertEquals("GET", response.headers().firstValue("Allow").orElse(""));
    }

    private void assertInvalidInput(String query) throws Exception {
        HttpResponse<String> response = get(query);

        assertEquals(400, response.statusCode());
        assertEquals("{\"error\":\"Invalid Input\"}", response.body());
    }

    private HttpResponse<String> get(String query) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/hello-world" + query)).GET().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
