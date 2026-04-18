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

public class ResortActivitiesModelTest {

    private ResortActivitiesModel resortActivitiesModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortActivitiesModel = new ResortActivitiesModel();
        setField(resortActivitiesModel, "request", request);
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

        List<Activities> result = resortActivitiesModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Activities> result = resortActivitiesModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithMultipleActivities() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleActivities();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Activities> result = resortActivitiesModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetPropertiesListWithDetailedActivity() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithDetailedActivity();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Activities> result = resortActivitiesModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Spa Treatment", result.get(0).getName());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode activities = jcr.putObject("activities");
        ArrayNode nodes = activities.putArray("nodes");
        
        ObjectNode activity = nodes.addObject();
        activity.put("name", "Pool Activity");
        activity.put("nodename", "pool-activity");
        activity.put("description", "Enjoy the pool");
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
        a1.put("name", "Pool");
        a1.put("priority", "1");
        
        ObjectNode a2 = nodes.addObject();
        a2.put("name", "Spa");
        a2.put("priority", "2");
        
        ObjectNode a3 = nodes.addObject();
        a3.put("name", "Fitness Center");
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
        activity.put("name", "Spa Treatment");
        activity.put("nodename", "spa-treatment");
        activity.put("description", "Relaxing spa experience");
        activity.put("longDescription", "Full spa treatment with massage and facial");
        activity.put("activityType", "wellness");
        activity.put("phone", "555-SPA1");
        activity.put("priority", "1");
        
        return root;
    }
}
