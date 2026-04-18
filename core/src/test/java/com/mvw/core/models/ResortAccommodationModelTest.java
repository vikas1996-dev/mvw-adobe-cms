package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Villas;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResortAccommodationModelTest {

    private ResortAccommodationModel resortAccommodationModel;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jahiaApiConfigServiceImpl.getApiImagePath()).thenReturn("");
        resortAccommodationModel = new ResortAccommodationModel();
        setField(resortAccommodationModel, "request", request);
        setField(resortAccommodationModel, "jahiaApiConfigServiceImpl", jahiaApiConfigServiceImpl);
        setField(resortAccommodationModel, "baseImagePath", "");
    }

    private void setField(Object target, String fieldName, Object value) {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    try {
                        field.setAccessible(true);
                        field.set(target, value);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to set " + fieldName, e);
                    }
                }
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }

    @Test
    void testGetPropertiesListWithValidResponse() throws IOException {
        ObjectNode jsonResponse = createValidJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Villas> result = resortAccommodationModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Villas> result = resortAccommodationModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithMultipleVillas() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleVillas();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Villas> result = resortAccommodationModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetPropertiesListWithDetailedVilla() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithDetailedVilla();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Villas> result = resortAccommodationModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Two-Bedroom Villa", result.get(0).getName());
        assertEquals("4", result.get(0).getSleeps());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode villas = jcr.putObject("villas");
        ArrayNode nodes = villas.putArray("nodes");
        
        ObjectNode villa = nodes.addObject();
        villa.put("name", "One-Bedroom Villa");
        villa.put("nodename", "one-bedroom");
        villa.put("sleeps", "2");
        villa.put("squareFootage", "800");
        villa.put("description", "Cozy villa");
        
        return root;
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode villas = jcr.putObject("villas");
        villas.putArray("nodes");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMultipleVillas() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode villas = jcr.putObject("villas");
        ArrayNode nodes = villas.putArray("nodes");
        
        ObjectNode v1 = nodes.addObject();
        v1.put("name", "One-Bedroom Villa");
        v1.put("sleeps", "2");
        
        ObjectNode v2 = nodes.addObject();
        v2.put("name", "Two-Bedroom Villa");
        v2.put("sleeps", "4");
        
        ObjectNode v3 = nodes.addObject();
        v3.put("name", "Three-Bedroom Villa");
        v3.put("sleeps", "8");
        
        return root;
    }

    private ObjectNode createJsonResponseWithDetailedVilla() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode villas = jcr.putObject("villas");
        ArrayNode nodes = villas.putArray("nodes");
        
        ObjectNode villa = nodes.addObject();
        villa.put("name", "Two-Bedroom Villa");
        villa.put("nodename", "two-bedroom-villa");
        villa.put("sleeps", "4");
        villa.put("squareFootage", "1200");
        villa.put("description", "Spacious two-bedroom villa with king bed");
        villa.put("checkAvailabilityUrl", "https://book.test.com/villa");
        villa.put("disclaimer", "Prices may vary");
        
        return root;
    }
}
