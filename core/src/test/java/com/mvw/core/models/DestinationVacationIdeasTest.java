package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DestinationVacationIdeasTest {

    private DestinationVacationIdeas destinationVacationIdeas;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        destinationVacationIdeas = new DestinationVacationIdeas();
        setField(destinationVacationIdeas, "request", request);
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
    void testGetPropertiesListWithEmptyResponse() throws IOException {
        ObjectNode jsonResponse = createEmptyJsonResponse();
        when(request.getAttribute("destinationResponse")).thenReturn(jsonResponse);
        when(request.getAttribute("nodeName")).thenReturn("hawaii");
        
        List<?> result = destinationVacationIdeas.getPropertiesList();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterByDestinationNodeNameWithNullArticles() {
        List<?> result = DestinationVacationIdeas.filterByDestinationNodeName(null, "hawaii");
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterByDestinationNodeNameWithNullDestinationName() {
        List<?> result = DestinationVacationIdeas.filterByDestinationNodeName(Collections.emptyList(), null);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterByDestinationNodeNameWithEmptyList() {
        List<?> result = DestinationVacationIdeas.filterByDestinationNodeName(Collections.emptyList(), "hawaii");
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testPopulateVacationIdeasWithNullArticles() {
        // Should not throw exception
        DestinationVacationIdeas.populateVacationIdeas(null);
    }

    @Test
    void testPopulateVacationIdeasWithEmptyList() {
        // Should not throw exception
        DestinationVacationIdeas.populateVacationIdeas(Collections.emptyList());
    }

    private ObjectNode createEmptyJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode articles = jcr.putObject("articles");
        articles.putArray("nodes");
        
        return root;
    }
}
