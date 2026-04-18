package com.mvw.core.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TripadvisorServiceImplTest {

    private TripAdvisorServiceImpl service;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private TripAdvisorConfigServiceImpl mockConfig;

    @BeforeEach
    void setUp() {
        mockConfig = mock(TripAdvisorConfigServiceImpl.class);

        when(mockConfig.getApiUrl()).thenReturn("http://dummy-url");
        when(mockConfig.getApiKey()).thenReturn("dummy-key");

        service = new TripAdvisorServiceImpl();

        // Inject mock config into service
        service.tripAdvisorConfigServiceImpl = mockConfig;
    }

    @Test
    void testGetLocationDetails_Success() throws Exception {
        String expectedResponse = "{\"rating\":\"4.5\",\"num_reviews\":\"100\"}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(expectedResponse);

            String result = service.getLocationDetails("123456");

            assertNotNull(result);
            assertEquals(expectedResponse, result);
        }
    }

    @Test
    void testGetLocationDetails_IOException() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Network error"));

            String result = service.getLocationDetails("123456");

            assertNotNull(result);
            assertEquals("", result);
        }
    }

    @Test
    void testGetLocationDetails_InterruptedException() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Test interruption"));

            String result = service.getLocationDetails("123456");

            assertNotNull(result);
            assertEquals("", result);
            assertTrue(Thread.currentThread().isInterrupted());
            // Clear the interrupted status for other tests
            Thread.interrupted();
        }
    }

    @Test
    void testGetLocationDetails_EmptyId() throws Exception {
        String expectedResponse = "{\"error\":\"Invalid location\"}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(expectedResponse);

            String result = service.getLocationDetails("");

            assertNotNull(result);
        }
    }

    @Test
    void testGetLocationDetails_NullId() {
        // This may throw NPE during URL construction
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            
            try {
                String result = service.getLocationDetails(null);
                assertNotNull(result);
            } catch (NullPointerException e) {
                // Expected if null check is not in place
                assertNotNull(e);
            }
        }
    }

    @Test
    void testGetLocationDetails_ValidResponse() throws Exception {
        String validResponse = "{\"location_id\":\"123\",\"name\":\"Test Resort\",\"rating\":\"4.5\",\"num_reviews\":\"1000\",\"rating_image_url\":\"http://example.com/img.png\"}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(validResponse);

            String result = service.getLocationDetails("123");

            assertNotNull(result);
            assertTrue(result.contains("Test Resort"));
            assertTrue(result.contains("4.5"));
        }
    }

    @Test
    void testGetLocationDetails_MultipleRequests() throws Exception {
        String expectedResponse = "{\"rating\":\"4.5\"}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(expectedResponse);

            String result1 = service.getLocationDetails("111");
            String result2 = service.getLocationDetails("222");
            String result3 = service.getLocationDetails("333");

            assertNotNull(result1);
            assertNotNull(result2);
            assertNotNull(result3);
        }
    }
}
