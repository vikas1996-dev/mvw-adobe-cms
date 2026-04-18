package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Properties;
import com.mvw.core.models.dto.ResortDto;
import com.mvw.core.services.TripAdvisorEnrichmentService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class ResortDetailsModelTest {

    private ResortDetailsModel resortDetailsModel;
    private SlingHttpServletRequest request;
    private ResourceResolver resourceResolver;
    private TripAdvisorEnrichmentService tripAdvisorEnrichmentService;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        resortDetailsModel = new ResortDetailsModel();
        request = mock(SlingHttpServletRequest.class);
        resourceResolver = mock(ResourceResolver.class);
        tripAdvisorEnrichmentService = mock(TripAdvisorEnrichmentService.class);
        
        setField(resortDetailsModel, "request", request);
        setField(resortDetailsModel, "resourceResolver", resourceResolver);
        setField(resortDetailsModel, "tripAdvisorEnrichmentService", tripAdvisorEnrichmentService);
        
        // Default mock behavior: return enriched resorts with TripAdvisor data
        when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList()))
                .thenAnswer(invocation -> {
                    List<ResortDto> resorts = invocation.getArgument(1);
                    // Enrich each resort with mock TripAdvisor data
                    for (ResortDto resort : resorts) {
                        if (resort.getTripadvisorId() != null) {
                            resort.setRating("4.5");
                            resort.setReviews("500");
                            resort.setRatingImage("http://rating.png");
                            resort.setWebUrl("http://tripadvisor.com");
                        }
                    }
                    return resorts;
                });
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

        List<Properties> result = resortDetailsModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(tripAdvisorEnrichmentService).enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList());
    }

    @Test
    void testGetPropertiesListWithEmptyNodes() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Properties> result = resortDetailsModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithTripAdvisorData() throws IOException {
        ObjectNode jsonResponse = createValidJsonResponse();
        
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Properties> result = resortDetailsModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Verify enrichment was called
        verify(tripAdvisorEnrichmentService).enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList());
        // Verify TripAdvisor data was set on properties
        assertEquals("4.5", result.get(0).getRating());
        assertEquals("500", result.get(0).getReviews());
    }

    @Test
    void testGetPropertiesListWithMultipleProperties() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithMultipleProperties();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<Properties> result = resortDetailsModel.getPropertiesList();

        assertNotNull(result);
        assertEquals(3, result.size());
        // Verify all properties were enriched
        for (Properties p : result) {
            assertEquals("4.5", p.getRating());
        }
    }

    @Test
    void testGetPropertiesListWithNoTripAdvisorEnrichment() throws IOException {
        ObjectNode jsonResponse = createValidJsonResponse();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);
        // Return resorts without enrichment
        when(tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(any(ResourceResolver.class), anyList()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        List<Properties> result = resortDetailsModel.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Properties should not have TripAdvisor data
        assertNull(result.get(0).getRating());
    }

    @Test
    void testModelInstantiation() {
        ResortDetailsModel model = new ResortDetailsModel();
        assertNotNull(model);
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode properties = jcr.putObject("properties");
        ArrayNode nodes = properties.putArray("nodes");
        
        ObjectNode property = nodes.addObject();
        property.put("nodename", "test-resort");
        property.put("name", "Test Resort");
        property.put("description", "A beautiful resort");
        property.put("tripAdvisorId", "12345");
        property.put("address1", "123 Test Street");
        property.put("city", "Test City");
        property.put("region", "Test Region");
        property.put("country", "USA");
        property.put("phoneMain", "1-800-555-1234");
        property.put("coordinateLatitude", "25.7617");
        property.put("coordinateLongitude", "-80.1918");
        
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
        
        for (int i = 1; i <= 3; i++) {
            ObjectNode property = nodes.addObject();
            property.put("nodename", "resort-" + i);
            property.put("name", "Resort " + i);
            property.put("description", "Resort description " + i);
            property.put("tripAdvisorId", "1234" + i);
        }
        
        return root;
    }

}
