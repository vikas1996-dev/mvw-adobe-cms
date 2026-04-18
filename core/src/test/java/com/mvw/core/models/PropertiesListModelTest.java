package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Properties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PropertiesListModelTest {

    private PropertiesListModel propertiesListModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        propertiesListModel = new PropertiesListModel();
        setField(propertiesListModel, "request", request);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetPropertiesListWithValidResponse() throws IOException {
        ObjectNode jsonResponse = createValidJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Properties> result = propertiesListModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Properties> result = propertiesListModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithMultipleProperties() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleProperties();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Properties> result = propertiesListModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetPropertiesListWithDetailedProperty() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithDetailedProperty();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Properties> result = propertiesListModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Beach Resort", result.get(0).getName());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode properties = jcr.putObject("properties");
        ArrayNode nodes = properties.putArray("nodes");
        
        ObjectNode property = nodes.addObject();
        property.put("name", "Test Resort");
        property.put("nodename", "test-resort");
        property.put("description", "A great resort");
        property.put("city", "Miami");
        property.put("country", "USA");
        
        return root;
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode properties = jcr.putObject("properties");
        properties.putArray("nodes");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMultipleProperties() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode properties = jcr.putObject("properties");
        ArrayNode nodes = properties.putArray("nodes");
        
        ObjectNode p1 = nodes.addObject();
        p1.put("name", "Beach Resort");
        p1.put("city", "Miami");
        
        ObjectNode p2 = nodes.addObject();
        p2.put("name", "Mountain Lodge");
        p2.put("city", "Aspen");
        
        ObjectNode p3 = nodes.addObject();
        p3.put("name", "City Hotel");
        p3.put("city", "New York");
        
        return root;
    }

    private ObjectNode createJsonResponseWithDetailedProperty() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode properties = jcr.putObject("properties");
        ArrayNode nodes = properties.putArray("nodes");
        
        ObjectNode property = nodes.addObject();
        property.put("name", "Beach Resort");
        property.put("nodename", "beach-resort");
        property.put("description", "Beautiful beachfront property");
        property.put("address1", "123 Beach Road");
        property.put("city", "Miami");
        property.put("region", "Florida");
        property.put("country", "USA");
        property.put("phoneMain", "555-1234");
        property.put("coordinateLatitude", "25.7617");
        property.put("coordinateLongitude", "-80.1918");
        property.put("checkIn", "4:00 PM");
        property.put("checkOut", "10:00 AM");
        
        return root;
    }
}
