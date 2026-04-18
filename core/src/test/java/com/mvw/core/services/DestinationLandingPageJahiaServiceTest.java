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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DestinationLandingPageJahiaServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @InjectMocks
    private DestinationLandingPageJahiaService service;

    @BeforeEach
    void setUp() {
        when(jahiaApiConfigServiceImpl.getApiEndPoint()).thenReturn("https://api.example.com/graphql");
        when(jahiaApiConfigServiceImpl.getApiAuthToken()).thenReturn("test-token");
    }

    @Test
    void testExecuteGraphQlRequest_Success() throws Exception {
        String expectedResponse = "{\"data\":{\"jcr\":{\"promos\":[]}}}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(expectedResponse);

            HttpResponse<String> result = service.executeGraphQlRequest();

            assertNotNull(result);
            assertEquals(expectedResponse, result.body());
        }
    }

    @Test
    void testExecuteGraphQlRequest_InterruptedException() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new InterruptedException("Test interruption"));

            assertThrows(InterruptedException.class, () -> service.executeGraphQlRequest());
        }
    }

    @Test
    void testExecuteGraphQlRequest_IOException() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Network error"));

            assertThrows(IOException.class, () -> service.executeGraphQlRequest());
        }
    }

    @Test
    void testExecuteGraphQlRequest_EmptyResponse() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn("{}");

            HttpResponse<String> result = service.executeGraphQlRequest();

            assertNotNull(result);
            assertEquals("{}", result.body());
        }
    }

    @Test
    void testExecuteGraphQlRequest_JsonResponse() throws Exception {
        String jsonResponse = "{\"data\":{\"jcr\":{\"editorialPath\":{\"children\":{\"nodes\":[{\"name\":\"promo1\"}]}}}}}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(jsonResponse);

            HttpResponse<String> result = service.executeGraphQlRequest();

            assertNotNull(result);
            assertTrue(result.body().contains("promo1"));
        }
    }

    @Test
    void testExecuteGraphQlRequest_LargeResponse() throws Exception {
        StringBuilder largeResponse = new StringBuilder("{\"data\":{\"items\":[");
        for (int i = 0; i < 100; i++) {
            if (i > 0) largeResponse.append(",");
            largeResponse.append("{\"id\":").append(i).append("}");
        }
        largeResponse.append("]}}");

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(largeResponse.toString());

            HttpResponse<String> result = service.executeGraphQlRequest();

            assertNotNull(result);
            assertTrue(result.body().length() > 1000);
        }
    }
}
