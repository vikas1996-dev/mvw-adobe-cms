package com.mvw.core.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mvw.core.services.LocDataService;

@ExtendWith(MockitoExtension.class)
class PhoneNumberLookupServletTest {

    @InjectMocks
    private PhoneNumberLookupServlet servlet;

    @Mock
    private LocDataService locDataService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGet_Success() throws Exception {
        Map<String, String> mockData = new HashMap<>();
        mockData.put("loc", "US");
        mockData.put("phoneNumber", "1-800-555-1234");

        when(locDataService.processLocData(request, response)).thenReturn(mockData);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
        assertTrue(output.contains("US"));
        assertTrue(output.contains("1-800-555-1234"));
    }

    @Test
    void testDoGet_NullValues() throws Exception {
        Map<String, String> mockData = new HashMap<>();
        mockData.put("loc", null);
        mockData.put("phoneNumber", null);

        when(locDataService.processLocData(request, response)).thenReturn(mockData);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
    }
}
