/*package com.mvw.core.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DestinationsDetailsJahiaServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<Object> httpResponse;

    @Test
    void testCallGraphqlService() throws Exception {

        DestinationsDetailsService service =
                Mockito.spy(new DestinationsDetailsService());

        doReturn(httpClient).when(service).getHttpClient();

        when(httpResponse.body()).thenReturn("{\"data\":{}}");
        when(httpClient.send(any(), any()))
                .thenReturn(httpResponse);

        HttpResponse<String> response =
                service.callGraphqlService("DEST123");

        assertNotNull(response);
        verify(httpClient, times(1))
                .send(any(), any());
    }
}
*/