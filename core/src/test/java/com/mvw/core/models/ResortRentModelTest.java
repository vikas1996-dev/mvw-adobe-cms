package com.mvw.core.models;

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

public class ResortRentModelTest {

    private ResortRentModel resortRentModel;

    @Mock
    private SlingHttpServletRequest request;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resortRentModel = new ResortRentModel();
        setField(resortRentModel, "request", request);
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

        List<DefaultModel> result = resortRentModel.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListWithBothSources() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithBothSources();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<DefaultModel> result = resortRentModel.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.size() >= 2); // Should contain items from both sources
    }

    @Test
    void testGetPropertiesListWithOnlyResortDetailPage() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithOnlyResortDetailPage();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<DefaultModel> result = resortRentModel.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListWithOnlyLabels() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithOnlyLabels();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<DefaultModel> result = resortRentModel.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListWithEmptyTextFields() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithEmptyTextFields();
        when(request.getAttribute("jahiaResponse")).thenReturn(jsonResponse);

        List<DefaultModel> result = resortRentModel.getPropertiesList();

        assertNotNull(result);
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode mvcsResortDetailPage = jcr.putObject("mvcsResortDetailPage");
        ObjectNode copy = mvcsResortDetailPage.putObject("copy");
        ArrayNode textFields = copy.putArray("textFields");
        ObjectNode field1 = textFields.addObject();
        field1.put("name", "field1");
        field1.put("nodename", "field1-node");
        field1.put("description", "Description 1");
        
        ObjectNode mvcsLabels = jcr.putObject("mvcsLabels");
        ObjectNode labelsCopy = mvcsLabels.putObject("copy");
        ArrayNode labelsTextFields = labelsCopy.putArray("textFields");
        ObjectNode label1 = labelsTextFields.addObject();
        label1.put("name", "label1");
        label1.put("nodename", "label1-node");
        label1.put("description", "Label Description 1");
        
        return root;
    }

    private ObjectNode createJsonResponseWithBothSources() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode mvcsResortDetailPage = jcr.putObject("mvcsResortDetailPage");
        ObjectNode copy = mvcsResortDetailPage.putObject("copy");
        ArrayNode textFields = copy.putArray("textFields");
        ObjectNode field1 = textFields.addObject();
        field1.put("name", "resortField");
        field1.put("nodename", "resort-field-node");
        field1.put("description", "Resort Field Description");
        ObjectNode field2 = textFields.addObject();
        field2.put("name", "resortField2");
        field2.put("nodename", "resort-field2-node");
        field2.put("description", "Resort Field 2 Description");
        
        ObjectNode mvcsLabels = jcr.putObject("mvcsLabels");
        ObjectNode labelsCopy = mvcsLabels.putObject("copy");
        ArrayNode labelsTextFields = labelsCopy.putArray("textFields");
        ObjectNode label1 = labelsTextFields.addObject();
        label1.put("name", "label1");
        label1.put("nodename", "label1-node");
        label1.put("description", "Label Description");
        
        return root;
    }

    private ObjectNode createJsonResponseWithOnlyResortDetailPage() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode mvcsResortDetailPage = jcr.putObject("mvcsResortDetailPage");
        ObjectNode copy = mvcsResortDetailPage.putObject("copy");
        ArrayNode textFields = copy.putArray("textFields");
        ObjectNode field1 = textFields.addObject();
        field1.put("name", "resortField");
        field1.put("nodename", "resort-field-node");
        field1.put("description", "Resort Field Description");
        
        ObjectNode mvcsLabels = jcr.putObject("mvcsLabels");
        ObjectNode labelsCopy = mvcsLabels.putObject("copy");
        labelsCopy.putArray("textFields");
        
        return root;
    }

    private ObjectNode createJsonResponseWithOnlyLabels() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode mvcsResortDetailPage = jcr.putObject("mvcsResortDetailPage");
        ObjectNode copy = mvcsResortDetailPage.putObject("copy");
        copy.putArray("textFields");
        
        ObjectNode mvcsLabels = jcr.putObject("mvcsLabels");
        ObjectNode labelsCopy = mvcsLabels.putObject("copy");
        ArrayNode labelsTextFields = labelsCopy.putArray("textFields");
        ObjectNode label1 = labelsTextFields.addObject();
        label1.put("name", "label1");
        label1.put("nodename", "label1-node");
        label1.put("description", "Label Description");
        
        return root;
    }

    private ObjectNode createJsonResponseWithEmptyTextFields() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode mvcsResortDetailPage = jcr.putObject("mvcsResortDetailPage");
        ObjectNode copy = mvcsResortDetailPage.putObject("copy");
        copy.putArray("textFields");
        
        ObjectNode mvcsLabels = jcr.putObject("mvcsLabels");
        ObjectNode labelsCopy = mvcsLabels.putObject("copy");
        labelsCopy.putArray("textFields");
        
        return root;
    }
}
