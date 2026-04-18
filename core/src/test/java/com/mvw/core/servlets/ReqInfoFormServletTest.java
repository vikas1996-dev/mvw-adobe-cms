package com.mvw.core.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.mvw.core.services.CountryRegionService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReqInfoFormServletTest {

    @InjectMocks
    private ReqInfoFormServlet servlet;

    @Mock
    private CountryRegionService countryRegionService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource mockResource;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoPost_USACountry_DefaultThankYou() throws Exception {
        String message = "{\"Country\":\"United States of America\"}";
        when(request.getParameter("message")).thenReturn(message);

        servlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
        assertTrue(output.contains("request-information-thank-you.html"));
    }

    @Test
    void testDoPost_USACountry_OwnerRequestType() throws Exception {
        String message = "{\"Country\":\"United States of America\",\"formId\":\"Owner\"}";
        when(request.getParameter("message")).thenReturn(message);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("request-information-thank-you-owner.html"));
    }

    @Test
    void testDoPost_USACountry_OwnerServicesRequestType() throws Exception {
        String message = "{\"Country\":\"United States of America\",\"formId\":\"Owner Services\"}";
        when(request.getParameter("message")).thenReturn(message);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("request-information-thank-you-owner-services.html"));
    }

    @Test
    void testDoPost_USACountry_SpecialOffers() throws Exception {
        String message = "{\"Country\":\"United States of America\",\"action\":\"web_to_lead\"}";
        when(request.getParameter("message")).thenReturn(message);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("special-offers-thank-you.html"));
    }

    @Test
    void testDoPost_NonUSACountry_WithRegion() throws Exception {
        String message = "{\"Country\":\"Canada\"}";
        when(request.getParameter("message")).thenReturn(message);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(countryRegionService.getRegionByCountry("Canada")).thenReturn("NA");
        
        // Mock resource resolution - resource not found
        when(resourceResolver.resolve(eq(request), anyString())).thenReturn(mockResource);
        when(mockResource.getResourceType()).thenReturn(Resource.RESOURCE_TYPE_NON_EXISTING);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
    }

    @Test
    void testDoPost_NonUSACountry_ResourceExists() throws Exception {
        String message = "{\"Country\":\"Mexico\"}";
        when(request.getParameter("message")).thenReturn(message);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(countryRegionService.getRegionByCountry("Mexico")).thenReturn("LATAM");
        
        // Mock resource exists
        when(resourceResolver.resolve(eq(request), anyString())).thenReturn(mockResource);
        when(mockResource.getResourceType()).thenReturn("cq:Page");

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
        assertTrue(output.contains("country=Mexico"));
    }

    @Test
    void testDoPost_NonUSACountry_NullRegion() throws Exception {
        String message = "{\"Country\":\"Unknown Country\"}";
        when(request.getParameter("message")).thenReturn(message);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(countryRegionService.getRegionByCountry("Unknown Country")).thenReturn(null);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
        assertTrue(output.contains("no-offer-redirect.html"));
    }

    @Test
    void testDoPost_NullMessage() throws Exception {
        when(request.getParameter("message")).thenReturn(null);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
        assertTrue(output.contains("redirectUrl"));
    }

    @Test
    void testDoPost_EmptyMessage() throws Exception {
        when(request.getParameter("message")).thenReturn("");

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
    }

    @Test
    void testDoPost_InvalidJson() throws Exception {
        when(request.getParameter("message")).thenReturn("invalid json");

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("success"));
    }

    @Test
    void testDoPost_NullFormId() throws Exception {
        String message = "{\"Country\":\"United States of America\",\"formId\":null}";
        when(request.getParameter("message")).thenReturn(message);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("request-information-thank-you.html"));
    }

    @Test
    void testDoPost_NullAction() throws Exception {
        String message = "{\"Country\":\"United States of America\",\"action\":null}";
        when(request.getParameter("message")).thenReturn(message);

        servlet.doPost(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("request-information-thank-you.html"));
    }
}
