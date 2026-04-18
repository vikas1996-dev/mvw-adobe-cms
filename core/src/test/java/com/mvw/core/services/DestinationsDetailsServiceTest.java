package com.mvw.core.services;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DestinationsDetailsServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @InjectMocks
    private TestableDestinationsDetailsService service;

    @BeforeEach
    void setUp() {
        when(jahiaApiConfigServiceImpl.getApiEndPoint()).thenReturn("https://api.example.com/graphql");
        when(jahiaApiConfigServiceImpl.getApiAuthToken()).thenReturn("test-token");
        // @InjectMocks does not inject into parent class fields; set it explicitly
        setField(service, "jahiaApiConfigServiceImpl", jahiaApiConfigServiceImpl);
    }

    private void setField(Object target, String fieldName, Object value) {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            for (java.lang.reflect.Field field : c.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    try {
                        field.setAccessible(true);
                        field.set(target, value);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to set field " + fieldName, e);
                    }
                }
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }

    @Test
    void testCallGraphqlService_Success() throws Exception {
        String name = "caribbean";
        String expectedResponse = "{\"data\":{\"destination\":\"Caribbean\"}}";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(expectedResponse);

        HttpResponse<String> result = service.callGraphqlService(name);

        assertNotNull(result);
        assertEquals(expectedResponse, result.body());
        verify(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testCallGraphqlService_InterruptedException() throws Exception {
        String name = "europe";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Test interruption"));

        HttpResponse<String> result = service.callGraphqlService(name);

        assertNull(result);
        assertTrue(Thread.currentThread().isInterrupted());
        // Clear the interrupted status for other tests
        Thread.interrupted();
    }

    @Test
    void testCallGraphqlService_IOException() throws Exception {
        String name = "asia";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network error"));

        HttpResponse<String> result = service.callGraphqlService(name);

        assertNull(result);
    }

    @Test
    void testCallGraphqlService_EmptyName() throws Exception {
        String name = "";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn("{\"data\":{}}");

        HttpResponse<String> result = service.callGraphqlService(name);

        assertNotNull(result);
    }

    @Test
    void testCallGraphqlService_SpecialCharactersInName() throws Exception {
        String name = "test-destination_123";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn("{\"data\":{}}");

        HttpResponse<String> result = service.callGraphqlService(name);

        assertNotNull(result);
    }

    @Test
    void testCallGraphqlService_MultipleRequests() throws Exception {
        String[] names = {"caribbean", "europe", "asia", "pacific"};

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn("{\"data\":{}}");

        for (String name : names) {
            HttpResponse<String> result = service.callGraphqlService(name);
            assertNotNull(result);
        }

        verify(mockHttpClient, times(names.length)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    /**
     * Testable subclass that overrides getHttpClient() to return a mock
     */
    private static class TestableDestinationsDetailsService extends DestinationsDetailsService {
        private final HttpClient httpClient;

        TestableDestinationsDetailsService(HttpClient httpClient) {
            this.httpClient = httpClient;
        }

        @Override
        protected HttpClient getHttpClient() {
            return httpClient;
        }
    }
}
