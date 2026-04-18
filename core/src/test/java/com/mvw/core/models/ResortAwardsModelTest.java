package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Awards;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResortAwardsModelTest {

    private ResortAwardsModel resortAwardsModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortAwardsModel = new ResortAwardsModel();
        setField(resortAwardsModel, "request", request);
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

        List<Awards> result = resortAwardsModel.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListWithTripAdvisorAward() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithTripAdvisorAward();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Awards> result = resortAwardsModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(a -> 
            a.getNodename() != null && 
            a.getNodename().toLowerCase().contains("tripadvisor-certificate-of-excellence")));
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Awards> result = resortAwardsModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithMultipleAwards() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleAwards();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Awards> result = resortAwardsModel.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListFiltersNonTripAdvisorAwards() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMixedAwards();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Awards> result = resortAwardsModel.getPropertiesList();

        assertNotNull(result);
        // Should only contain tripadvisor-certificate-of-excellence awards
        assertTrue(result.stream().allMatch(a -> 
            a.getNodename().toLowerCase().contains("tripadvisor-certificate-of-excellence")));
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode awards = jcr.putObject("awards");
        ArrayNode nodes = awards.putArray("nodes");
        
        ObjectNode award = nodes.addObject();
        award.put("nodename", "tripadvisor-certificate-of-excellence-2023");
        award.put("name", "TripAdvisor Certificate of Excellence 2023");
        award.put("description", "Award for excellence");
        award.put("priority", "1");
        
        return root;
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode awards = jcr.putObject("awards");
        awards.putArray("nodes");
        
        return root;
    }

    private ObjectNode createJsonResponseWithTripAdvisorAward() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode awards = jcr.putObject("awards");
        ArrayNode nodes = awards.putArray("nodes");
        
        ObjectNode award = nodes.addObject();
        award.put("nodename", "tripadvisor-certificate-of-excellence-2024");
        award.put("name", "TripAdvisor Certificate of Excellence 2024");
        award.put("awardLink", "https://tripadvisor.com/award");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMultipleAwards() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode awards = jcr.putObject("awards");
        ArrayNode nodes = awards.putArray("nodes");
        
        ObjectNode award1 = nodes.addObject();
        award1.put("nodename", "tripadvisor-certificate-of-excellence-2023");
        award1.put("name", "TripAdvisor 2023");
        
        ObjectNode award2 = nodes.addObject();
        award2.put("nodename", "tripadvisor-certificate-of-excellence-2024");
        award2.put("name", "TripAdvisor 2024");
        
        return root;
    }

    private ObjectNode createJsonResponseWithMixedAwards() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode awards = jcr.putObject("awards");
        ArrayNode nodes = awards.putArray("nodes");
        
        ObjectNode award1 = nodes.addObject();
        award1.put("nodename", "tripadvisor-certificate-of-excellence-2024");
        award1.put("name", "TripAdvisor Certificate");
        
        ObjectNode award2 = nodes.addObject();
        award2.put("nodename", "best-resort-award");
        award2.put("name", "Best Resort Award");
        
        ObjectNode award3 = nodes.addObject();
        award3.put("nodename", "traveler-choice");
        award3.put("name", "Traveler's Choice");
        
        return root;
    }
}
