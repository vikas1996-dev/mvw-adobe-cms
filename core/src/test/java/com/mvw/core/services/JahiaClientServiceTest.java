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
class JahiaClientServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @InjectMocks
    private JahiaClientService service;

    @BeforeEach
    void setUp() {
        when(jahiaApiConfigServiceImpl.getApiEndPoint()).thenReturn("https://api.example.com/graphql");
        when(jahiaApiConfigServiceImpl.getApiAuthToken()).thenReturn("test-token");
    }

    @Test
    void testCallGraphqlService_Success() throws Exception {
        String upcCode = "TEST123";
        String expectedResponse = "{\"data\":{\"test\":\"response\"}}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(expectedResponse);

            HttpResponse<String> result = service.callGraphqlService(upcCode);

            assertNotNull(result);
            assertEquals(expectedResponse, result.body());
        }
    }

    @Test
    void testCallGraphqlService_InterruptedException() throws Exception {
        String upcCode = "TEST123";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new InterruptedException("Test interruption"));

            HttpResponse<String> result = service.callGraphqlService(upcCode);

            assertNull(result);
            assertTrue(Thread.currentThread().isInterrupted());
            // Clear the interrupted status for other tests
            Thread.interrupted();
        }
    }

    @Test
    void testCallGraphqlService_IOException() throws Exception {
        String upcCode = "TEST123";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Test IO error"));

            HttpResponse<String> result = service.callGraphqlService(upcCode);

            assertNull(result);
        }
    }

    @Test
    void testCallGraphqlService_WithDifferentUpcCodes() throws Exception {
        String upcCode = "UPC001";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn("{\"data\":{}}");

            HttpResponse<String> result = service.callGraphqlService(upcCode);

            assertNotNull(result);
        }
    }

    @Test
    void testCallGraphqlService_EmptyUpcCode() throws Exception {
        String upcCode = "";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn("{\"data\":{}}");

            HttpResponse<String> result = service.callGraphqlService(upcCode);

            assertNotNull(result);
        }
    }
}
