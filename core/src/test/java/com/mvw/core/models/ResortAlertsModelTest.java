package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Alerts;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResortAlertsModelTest {

    private ResortAlertsModel resortAlertsModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortAlertsModel = new ResortAlertsModel();
        setField(resortAlertsModel, "request", request);
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

        List<Alerts> result = resortAlertsModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Alerts> result = resortAlertsModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithMultipleAlerts() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleAlerts();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Alerts> result = resortAlertsModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetPropertiesListWithDetailedAlert() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithDetailedAlert();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Alerts> result = resortAlertsModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Weather Alert", result.get(0).getName());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode alerts = jcr.putObject("alerts");
        ArrayNode nodes = alerts.putArray("nodes");
        
        ObjectNode alert = nodes.addObject();
        alert.put("name", "Test Alert");
        alert.put("nodename", "test-alert");
        
        return root;
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode alerts = jcr.putObject("alerts");
        alerts.putArray("nodes");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMultipleAlerts() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode alerts = jcr.putObject("alerts");
        ArrayNode nodes = alerts.putArray("nodes");
        
        ObjectNode a1 = nodes.addObject();
        a1.put("name", "Weather Alert");
        
        ObjectNode a2 = nodes.addObject();
        a2.put("name", "Maintenance Notice");
        
        ObjectNode a3 = nodes.addObject();
        a3.put("name", "Special Event");
        
        return root;
    }

    private ObjectNode createJsonResponseWithDetailedAlert() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode alerts = jcr.putObject("alerts");
        ArrayNode nodes = alerts.putArray("nodes");
        
        ObjectNode alert = nodes.addObject();
        alert.put("name", "Weather Alert");
        alert.put("nodename", "weather-alert");
        alert.put("description", "Tropical storm warning");
        
        return root;
    }
}
