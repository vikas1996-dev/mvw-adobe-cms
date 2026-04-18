package com.mvw.core.servlets;

import com.mvw.core.models.DestinationLandingPageResponse;
import com.mvw.core.services.DestinationLandingService;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestinationLandingPageServletTest {

    @InjectMocks
    private DestinationLandingPageServlet servlet;

    @Mock
    private DestinationLandingService destinationLandingService;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @Test
    void testDoGetSuccess() throws Exception {
        DestinationLandingPageResponse destinationResponse = new DestinationLandingPageResponse();

        when(destinationLandingService.getDestinationResponse()).thenReturn(destinationResponse);
        when(jahiaApiConfigServiceImpl.getApiImagePath()).thenReturn("https://content-stg.vistana.com/files/live");

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGetException() throws Exception {
        when(destinationLandingService.getDestinationResponse()).thenThrow(new RuntimeException("Test exception"));

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
