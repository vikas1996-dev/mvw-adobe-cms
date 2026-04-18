package com.mvw.core.services.impl;

import com.mvw.core.services.CdnPurgeRequest;
import com.mvw.core.services.CdnPurgeResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CdnPurgeServiceImplTest {

    private CdnPurgeServiceImpl service;
    private HttpServer server;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        service = new CdnPurgeServiceImpl();

        // Create config service
        CdnPurgeConfigImpl config = new CdnPurgeConfigImpl();
        config.activate(new TestCdnPurgeConfig(
            "sample-purge-key-1",
            "sample-purge-key-2",
            "www.example.com"
        ));

        // Inject config into the service
        service.setConfigService(config);

        // Activate service AFTER injecting config
        service.activate();

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void buildResourcePathPurgeRequestShouldNormalizeDomainAndPath() {
        CdnPurgeRequest request = service.buildResourcePathPurgeRequest(
                "content/tmvcs/us/en/experiences/destinations",
                "https://dev-publish.marriottvacationclubs.com/",
                "X-AEM-Purge");

        assertEquals("PURGE", request.getMethod());
        assertEquals("https://dev-publish.marriottvacationclubs.com/content/tmvcs/us/en/experiences/destinations.html",
                request.getUrl());
        assertEquals("hard", request.getHeaders().get("X-AEM-Purge"));
        assertEquals("sample-purge-key-1", request.getHeaders().get("X-AEM-Purge-Key"));
    }

    @Test
    void buildSurrogateKeyPurgeRequestShouldSetSurrogateHeader() {
        CdnPurgeRequest request = service.buildSurrogateKeyPurgeRequest(
                "dev-publish.marriottvacationclubs.com",
                "TMVCS_Destinations",
                "X-AEM-Purge");

        assertEquals("PURGE", request.getMethod());
        assertEquals("https://dev-publish.marriottvacationclubs.com/", request.getUrl());
        assertEquals("hard", request.getHeaders().get("X-AEM-Purge"));
        assertEquals("sample-purge-key-1", request.getHeaders().get("X-AEM-Purge-Key"));
        assertEquals("TMVCS_Destinations", request.getHeaders().get("Surrogate-Key"));
    }

    @Test
    void buildResourcePathPurgeRequestShouldRejectBlankPath() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.buildResourcePathPurgeRequest(
                                " ",
                                "dev-publish.marriottvacationclubs.com",
                                "X-AEM-Purge"));
        
        assertEquals("Resource path must not be blank", exception.getMessage());
    }

    @Test
    void buildResourcePathPurgeRequestShouldRejectBlankPurgeTypeHeader() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.buildResourcePathPurgeRequest(
                                "/content/tmvcs/us/en/experiences/destinations.html",
                                "dev-publish.marriottvacationclubs.com",
                                " "));

        assertEquals("Purge type header must not be blank", exception.getMessage());
    }

    @Test
    void buildResourcePathPurgeRequestShouldSkipPurgeKeyHeaderWhenConfigIsBlank() throws IOException {
        CdnPurgeServiceImpl purgeService = new CdnPurgeServiceImpl(HttpClient.newHttpClient());

        CdnPurgeRequest request = purgeService.buildResourcePathPurgeRequest(
                "/content/tmvcs/us/en/experiences/destinations.html",
                "dev-publish.marriottvacationclubs.com",
                "X-AEM-Purge");

        assertEquals(null, request.getHeaders().get("X-AEM-Purge-Key"));
    }

    @Test
    void buildSurrogateKeyPurgeRequestShouldRejectBlankSurrogateKey() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.buildSurrogateKeyPurgeRequest(
                                "dev-publish.marriottvacationclubs.com",
                                " ",
                                "X-AEM-Purge"));
        
        assertEquals("Surrogate key must not be blank", exception.getMessage());
    }

    @Test
    void buildSurrogateKeyPurgeRequestShouldRejectBlankPurgeTypeHeader() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.buildSurrogateKeyPurgeRequest(
                                "dev-publish.marriottvacationclubs.com",
                                "TMVCS_Destinations",
                                " "));

        assertEquals("Purge type header must not be blank", exception.getMessage());
    }

    @Test
    void buildSurrogateKeyPurgeRequestShouldFallbackToSecondConfiguredPurgeKey() {
        // use the initialized service
        service.setConfigService(new CdnPurgeConfigImpl() {{
            activate(new TestCdnPurgeConfig(
                "",                      // key1 blank
                "sample-purge-key-2",    // key2 used
                "www.example.com"
            ));
        }});
        service.activate();

        CdnPurgeRequest request = service.buildSurrogateKeyPurgeRequest(
            "dev-publish.marriottvacationclubs.com",
            "TMVCS_Destinations",
            "X-AEM-Purge"
        );

        assertEquals("sample-purge-key-2", request.getHeaders().get("X-AEM-Purge-Key"));
    }

    @Test
    void executeShouldSendPurgeRequestWithHeaders() throws Exception {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> purgeHeader = new AtomicReference<>();
        AtomicReference<String> surrogateKey = new AtomicReference<>();

        server.createContext("/purge", new JsonResponseHandler(exchange -> {
            method.set(exchange.getRequestMethod());
            purgeHeader.set(exchange.getRequestHeaders().getFirst("X-AEM-Purge"));
            surrogateKey.set(exchange.getRequestHeaders().getFirst("Surrogate-Key"));
        }));

        // Use the service instance that already has config and keys
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-AEM-Purge", "hard");
        headers.put("X-AEM-Purge-Key", "10001");
        headers.put("Surrogate-Key", "TMVCS_Resorts_data");

        CdnPurgeRequest request = new CdnPurgeRequest("PURGE", baseUrl + "/purge", headers);

        // Use the pre-configured service
        CdnPurgeResponse response = service.execute(request);

        assertEquals("PURGE", method.get());
        assertEquals("hard", purgeHeader.get());
        assertEquals("TMVCS_Resorts_data", surrogateKey.get());
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccessful());
        assertEquals("{\"status\":\"ok\"}", response.getResponseBody());
    }

    @Test
    void executeShouldRejectBlankRequestUrl() {
        CdnPurgeRequest request = new CdnPurgeRequest("PURGE", " ", Map.of());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.execute(request));

        assertEquals("Purge request URL must not be blank", exception.getMessage());
    }

    private static final class JsonResponseHandler implements HttpHandler {

        private final ExchangeConsumer exchangeConsumer;

        private JsonResponseHandler(ExchangeConsumer exchangeConsumer) {
            this.exchangeConsumer = exchangeConsumer;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchangeConsumer.accept(exchange);
            byte[] response = "{\"status\":\"ok\"}".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response);
            }
        }
    }

    @FunctionalInterface
    private interface ExchangeConsumer {
        void accept(HttpExchange exchange) throws IOException;
    }

    private static final class TestCdnPurgeConfig implements com.mvw.core.config.CdnPurgeConfig {

        private final String key1;
        private final String key2;
        private final String cdnDomain;

        private TestCdnPurgeConfig(String key1, String key2, String domain) {
            this.key1 = key1;
            this.key2 = key2;
            this.cdnDomain = domain;
        }

        @Override
        public String cdnPurgeKey1() {
            return key1;
        }

        @Override
        public String cdnPurgeKey2() {
            return key2;
        }

        @Override
        public String cdnPurgeDomain() {
            return cdnDomain;
        }

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return com.mvw.core.config.CdnPurgeConfig.class;
        }
    }
}
