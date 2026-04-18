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
class StaticContentServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @InjectMocks
    private StaticContentService service;

    @BeforeEach
    void setUp() {
        when(jahiaApiConfigServiceImpl.getApiEndPoint()).thenReturn("https://api.example.com/graphql");
        when(jahiaApiConfigServiceImpl.getApiAuthToken()).thenReturn("test-token");
    }

    @Test
    void testCallGraphqlService_Success() throws Exception {
        String expectedResponse = "{\"data\":{\"staticContent\":{\"test\":\"value\"}}}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(expectedResponse);

            HttpResponse<String> result = service.callGraphqlService();

            assertNotNull(result);
            assertEquals(expectedResponse, result.body());
        }
    }

    @Test
    void testCallGraphqlService_InterruptedException() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new InterruptedException("Test interruption"));

            HttpResponse<String> result = service.callGraphqlService();

            assertNull(result);
            assertTrue(Thread.currentThread().isInterrupted());
            // Clear the interrupted status for other tests
            Thread.interrupted();
        }
    }

    @Test
    void testCallGraphqlService_IOException() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Network error"));

            HttpResponse<String> result = service.callGraphqlService();

            assertNull(result);
        }
    }

    @Test
    void testCallGraphqlService_EmptyResponse() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn("{}");

            HttpResponse<String> result = service.callGraphqlService();

            assertNotNull(result);
            assertEquals("{}", result.body());
        }
    }

    @Test
    void testCallGraphqlService_NullBody() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(null);

            HttpResponse<String> result = service.callGraphqlService();

            assertNotNull(result);
            assertNull(result.body());
        }
    }

    @Test
    void testCallGraphqlService_ComplexJsonResponse() throws Exception {
        String complexResponse = "{\"data\":{\"jcr\":{\"static\":{\"header\":{\"logo\":\"url\",\"nav\":[\"home\",\"about\"]},\"footer\":{\"links\":[]}}}}}";

        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(complexResponse);

            HttpResponse<String> result = service.callGraphqlService();

            assertNotNull(result);
            assertTrue(result.body().contains("header"));
            assertTrue(result.body().contains("footer"));
        }
    }

    @Test
    void testCallGraphqlService_MultipleInvocations() throws Exception {
        try (MockedStatic<HttpClient> mockedStatic = mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn("{\"data\":{}}");

            // Call multiple times
            HttpResponse<String> result1 = service.callGraphqlService();
            HttpResponse<String> result2 = service.callGraphqlService();
            HttpResponse<String> result3 = service.callGraphqlService();

            assertNotNull(result1);
            assertNotNull(result2);
            assertNotNull(result3);
        }
    }
}
