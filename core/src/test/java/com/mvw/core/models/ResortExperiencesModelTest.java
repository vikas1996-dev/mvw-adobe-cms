package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Activities;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResortExperiencesModelTest {

    private ResortExperiencesModel resortExperiencesModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortExperiencesModel = new ResortExperiencesModel();
        setField(resortExperiencesModel, "request", request);
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

        List<Activities> result = resortExperiencesModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Activities> result = resortExperiencesModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithMultipleActivities() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleActivities();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Activities> result = resortExperiencesModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetPropertiesListWithDetailedActivity() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithDetailedActivity();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Activities> result = resortExperiencesModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Beach Volleyball", result.get(0).getName());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode activities = jcr.putObject("activities");
        ArrayNode nodes = activities.putArray("nodes");
        
        ObjectNode activity = nodes.addObject();
        activity.put("name", "Swimming");
        activity.put("nodename", "swimming");
        activity.put("description", "Pool activities");
        activity.put("priority", "1");
        
        return root;
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode activities = jcr.putObject("activities");
        activities.putArray("nodes");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMultipleActivities() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode activities = jcr.putObject("activities");
        ArrayNode nodes = activities.putArray("nodes");
        
        ObjectNode a1 = nodes.addObject();
        a1.put("name", "Swimming");
        a1.put("priority", "1");
        
        ObjectNode a2 = nodes.addObject();
        a2.put("name", "Tennis");
        a2.put("priority", "2");
        
        ObjectNode a3 = nodes.addObject();
        a3.put("name", "Golf");
        a3.put("priority", "3");
        
        return root;
    }

    private ObjectNode createJsonResponseWithDetailedActivity() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode activities = jcr.putObject("activities");
        ArrayNode nodes = activities.putArray("nodes");
        
        ObjectNode activity = nodes.addObject();
        activity.put("name", "Beach Volleyball");
        activity.put("nodename", "beach-volleyball");
        activity.put("description", "Play volleyball on the beach");
        activity.put("longDescription", "Enjoy beach volleyball with friends and family");
        activity.put("activityType", "outdoor");
        activity.put("phone", "555-1234");
        activity.put("priority", "1");
        
        return root;
    }
}
