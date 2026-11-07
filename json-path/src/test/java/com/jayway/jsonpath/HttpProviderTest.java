package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.HttpProviderFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class HttpProviderTest {

    private static final String EXPECTED =
            """
            {
               "results" : [],
               "status" : "ZERO_RESULTS"
            }\
            """;

    @Test
    public void http_get() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        byte[] response = EXPECTED.getBytes(StandardCharsets.UTF_8);
        server.createContext(
                "/maps/api/geocode/json",
                exchange -> {
                    exchange.sendResponseHeaders(200, response.length);
                    try (OutputStream body = exchange.getResponseBody()) {
                        body.write(response);
                    }
                });
        server.start();

        InputStream inputStream = null;
        try {
            URL url =
                    new URL(
                            "http://127.0.0.1:"
                                    + server.getAddress().getPort()
                                    + "/maps/api/geocode/json?sensor=false");
            inputStream = HttpProviderFactory.getProvider().get(url);

            byte[] bytes = inputStream.readAllBytes();

            String json = new String(bytes).trim();

            assertEquals(EXPECTED, json);

        } catch (IOException e) {
            Utils.closeQuietly(inputStream);
        } finally {
            Utils.closeQuietly(inputStream);
            server.stop(0);
        }
    }
}
