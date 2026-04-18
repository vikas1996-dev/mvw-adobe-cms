package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Dining;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResortDiningModelTest {

    private ResortDiningModel resortDiningModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortDiningModel = new ResortDiningModel();
        setField(resortDiningModel, "request", request);
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

        List<Dining> result = resortDiningModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size()); // only "true" should be included
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Dining> result = resortDiningModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListSortsByPriority() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithPriorities();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Dining> result = resortDiningModel.getPropertiesList();

        assertNotNull(result);

        for (int i = 0; i < result.size() - 1; i++) {
            int current = parsePriority(result.get(i).getPriority());
            int next = parsePriority(result.get(i + 1).getPriority());
            assertTrue(current <= next);
        }
    }

    @Test
    void testSortByPriorityWithValidPriorities() {
        List<Dining> diningList = new ArrayList<>();

        diningList.add(createDining("Restaurant C", "3"));
        diningList.add(createDining("Restaurant A", "1"));
        diningList.add(createDining("Restaurant B", "2"));

        ResortDiningModel.sortByPriority(diningList);

        assertEquals("Restaurant A", diningList.get(0).getName());
        assertEquals("Restaurant B", diningList.get(1).getName());
        assertEquals("Restaurant C", diningList.get(2).getName());
    }

    @Test
    void testSortByPriorityWithInvalidPriorities() {
        List<Dining> diningList = new ArrayList<>();

        diningList.add(createDining("Restaurant A", "invalid"));
        diningList.add(createDining("Restaurant B", "1"));
        diningList.add(createDining("Restaurant C", null));

        ResortDiningModel.sortByPriority(diningList);

        assertEquals("Restaurant B", diningList.get(0).getName());
    }

    @Test
    void testSortByPriorityWithEmptyList() {
        List<Dining> diningList = new ArrayList<>();

        ResortDiningModel.sortByPriority(diningList);

        assertTrue(diningList.isEmpty());
    }

    @Test
    void testSortByPriorityWithNullPriorities() {
        List<Dining> diningList = new ArrayList<>();

        diningList.add(createDining("Restaurant A", null));
        diningList.add(createDining("Restaurant B", null));

        ResortDiningModel.sortByPriority(diningList);

        assertEquals(2, diningList.size());
    }

    @Test
    void testGetPropertiesListWithAllDiningTypes() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithAllDiningTypes();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Dining> result = resortDiningModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(2, result.size()); // only "true" values
    }

    // ------------------ JSON BUILDERS ------------------

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode dining = jcr.putObject("dining");
        ArrayNode nodes = dining.putArray("nodes");

        ObjectNode restaurant = nodes.addObject();
        restaurant.put("name", "Main Restaurant");
        restaurant.put("priority", "1");
        restaurant.put("typeOnOffSite", "true");

        return root;
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode dining = jcr.putObject("dining");
        dining.putArray("nodes");

        return root;
    }

    private ObjectNode createJsonResponseWithPriorities() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode dining = jcr.putObject("dining");
        ArrayNode nodes = dining.putArray("nodes");

        ObjectNode r1 = nodes.addObject();
        r1.put("name", "Restaurant C");
        r1.put("priority", "3");
        r1.put("typeOnOffSite", "true");

        ObjectNode r2 = nodes.addObject();
        r2.put("name", "Restaurant A");
        r2.put("priority", "1");
        r2.put("typeOnOffSite", "true");

        ObjectNode r3 = nodes.addObject();
        r3.put("name", "Restaurant B");
        r3.put("priority", "2");
        r3.put("typeOnOffSite", "true");

        return root;
    }

    private ObjectNode createJsonResponseWithAllDiningTypes() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode dining = jcr.putObject("dining");
        ArrayNode nodes = dining.putArray("nodes");

        ObjectNode r1 = nodes.addObject();
        r1.put("name", "Pool Bar");
        r1.put("typeOnOffSite", "true");
        r1.put("priority", "1");

        ObjectNode r2 = nodes.addObject();
        r2.put("name", "Fine Dining");
        r2.put("typeOnOffSite", "true");
        r2.put("priority", "2");

        ObjectNode r3 = nodes.addObject();
        r3.put("name", "Nearby Restaurant");
        r3.put("typeOnOffSite", "false");
        r3.put("priority", "3");

        return root;
    }

    // ------------------ HELPERS ------------------

    private Dining createDining(String name, String priority) {
        Dining dining = new Dining();
        setFieldOnObject(dining, "name", name);
        setFieldOnObject(dining, "priority", priority);
        setFieldOnObject(dining, "typeOnOffSite", "true"); // ensure not filtered
        return dining;
    }

    private void setFieldOnObject(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int parsePriority(String priority) {
        try {
            return priority != null ? Integer.parseInt(priority) : Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}