package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.DefaultModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ResortNearByDiningModelTest {

    private final AemContext context = new AemContext();
    private final ObjectMapper mapper = new ObjectMapper();

    private ResortNearByDiningModel model;

    @BeforeEach
    void setUp() {
        model = new ResortNearByDiningModel();
        setField(model, "request", context.request());
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetPropertiesList_success() throws Exception {
        JsonNode jsonResponse = createValidJahiaResponse();
        context.request().setAttribute("jahiaResponse", jsonResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Restaurant 1", result.get(0).getName());
        assertEquals("Restaurant 2", result.get(1).getName());
    }

    @Test
    void testGetPropertiesList_nullJahiaResponse() {
        context.request().setAttribute("jahiaResponse", null);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_jahiaResponseNotJsonNode() {
        context.request().setAttribute("jahiaResponse", "invalid string");

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingDataPath() throws Exception {
        ObjectNode emptyResponse = mapper.createObjectNode();
        context.request().setAttribute("jahiaResponse", emptyResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingJcrPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data");
        context.request().setAttribute("jahiaResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingNearbyDiningPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr");
        context.request().setAttribute("jahiaResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingCopyPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("nearbyDining");
        context.request().setAttribute("jahiaResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingTextFieldsPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("nearbyDining").putObject("copy");
        context.request().setAttribute("jahiaResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_textFieldsNotArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("nearbyDining")
                .putObject("copy").put("textFields", "not an array");
        context.request().setAttribute("jahiaResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_emptyTextFieldsArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("nearbyDining")
                .putObject("copy").putArray("textFields");
        context.request().setAttribute("jahiaResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_invalidJsonCausesException() throws Exception {
        // Create a response that will cause JSON parsing to fail
        ObjectNode response = mapper.createObjectNode();
        ObjectNode data = response.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode nearbyDining = jcr.putObject("nearbyDining");
        ObjectNode copy = nearbyDining.putObject("copy");
        
        // Add a malformed structure that could cause issues during mapping
        ArrayNode textFields = copy.putArray("textFields");
        ObjectNode invalidItem = textFields.addObject();
        // Create a field with mismatched type for DefaultModel
        invalidItem.putArray("name"); // name expects String but we give array
        
        context.request().setAttribute("jahiaResponse", response);

        // This should handle exception gracefully
        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_attributeNotSet() {
        // Don't set any attribute
        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_withDescription() throws Exception {
        JsonNode jsonResponse = createJahiaResponseWithDescription();
        context.request().setAttribute("jahiaResponse", jsonResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Fine Dining", result.get(0).getName());
        assertEquals("A great restaurant", result.get(0).getDescription());
    }

    private JsonNode createValidJahiaResponse() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"nearbyDining\": {\n" +
                "        \"copy\": {\n" +
                "          \"textFields\": [\n" +
                "            {\"name\": \"Restaurant 1\", \"description\": \"Description 1\", \"path\": \"/dining/1\"},\n" +
                "            {\"name\": \"Restaurant 2\", \"description\": \"Description 2\", \"path\": \"/dining/2\"}\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return mapper.readTree(json);
    }

    private JsonNode createJahiaResponseWithDescription() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"nearbyDining\": {\n" +
                "        \"copy\": {\n" +
                "          \"textFields\": [\n" +
                "            {\"name\": \"Fine Dining\", \"description\": \"A great restaurant\"}\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return mapper.readTree(json);
    }
}
