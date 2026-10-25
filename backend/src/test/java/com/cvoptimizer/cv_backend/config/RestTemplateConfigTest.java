package com.cvoptimizer.cv_backend.config;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    private HttpServer server;
    private final AtomicReference<String> acceptEncoding = new AtomicReference<>();

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            acceptEncoding.set(exchange.getRequestHeaders().getFirst("Accept-Encoding"));
            byte[] body = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void sharedClient_doesNotAdvertiseCompression() {
        RestTemplate restTemplate = new RestTemplateConfig().restTemplate(new RestTemplateBuilder());

        String body = restTemplate.getForObject("http://127.0.0.1:" + server.getAddress().getPort() + "/", String.class);

        assertThat(body).isEqualTo("{\"ok\":true}");
        // Advertising "br" made OpenAI send Brotli, which the bundled decoder truncated to "{"
        assertThat(acceptEncoding.get()).isNull();
    }
}
