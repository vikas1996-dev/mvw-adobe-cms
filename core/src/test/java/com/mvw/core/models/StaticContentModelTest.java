package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.DefaultModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StaticContentModelTest {

    private StaticContentModel staticContentModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        staticContentModel = new StaticContentModel();
        objectMapper = new ObjectMapper();
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
        JsonNode jsonResponse = createValidJsonResponse();
        
        setField(staticContentModel, "request", request);
        when(request.getAttribute("staticData")).thenReturn(jsonResponse);
        
        List<DefaultModel> result = staticContentModel.getPropertiesList();
        
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPropertiesListWithEmptyDocuments() throws IOException {
        JsonNode jsonResponse = createEmptyDocumentsResponse();
        
        setField(staticContentModel, "request", request);
        when(request.getAttribute("staticData")).thenReturn(jsonResponse);
        
        List<DefaultModel> result = staticContentModel.getPropertiesList();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesListThrowsExceptionOnNull() {
        setField(staticContentModel, "request", request);
        when(request.getAttribute("staticData")).thenReturn(null);
        
        assertThrows(NullPointerException.class, () -> {
            staticContentModel.getPropertiesList();
        });
    }

    private JsonNode createValidJsonResponse() {
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode data = objectMapper.createObjectNode();
        ObjectNode jcr = objectMapper.createObjectNode();
        ObjectNode staticContent = objectMapper.createObjectNode();
        ObjectNode textFields = objectMapper.createObjectNode();
        
        ArrayNode documents = objectMapper.createArrayNode();
        
        // Use only fields that exist in DefaultModel: name, nodename, path, description, properties, images
        ObjectNode doc1 = objectMapper.createObjectNode();
        doc1.put("name", "Document 1");
        doc1.put("nodename", "doc1");
        doc1.put("path", "/content/documents/doc1");
        doc1.put("description", "First document");
        
        ObjectNode doc2 = objectMapper.createObjectNode();
        doc2.put("name", "Document 2");
        doc2.put("nodename", "doc2");
        doc2.put("path", "/content/documents/doc2");
        doc2.put("description", "Second document");
        
        documents.add(doc1);
        documents.add(doc2);
        
        textFields.set("documents", documents);
        staticContent.set("textFields", textFields);
        jcr.set("staticContent", staticContent);
        data.set("jcr", jcr);
        root.set("data", data);
        
        return root;
    }

    private JsonNode createEmptyDocumentsResponse() {
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode data = objectMapper.createObjectNode();
        ObjectNode jcr = objectMapper.createObjectNode();
        ObjectNode staticContent = objectMapper.createObjectNode();
        ObjectNode textFields = objectMapper.createObjectNode();
        
        ArrayNode documents = objectMapper.createArrayNode();
        
        textFields.set("documents", documents);
        staticContent.set("textFields", textFields);
        jcr.set("staticContent", staticContent);
        data.set("jcr", jcr);
        root.set("data", data);
        
        return root;
    }
}
