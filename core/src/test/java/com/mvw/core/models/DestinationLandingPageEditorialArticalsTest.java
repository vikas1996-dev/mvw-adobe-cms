package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.DefaultModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DestinationLandingPageEditorialArticalsTest {

    private final AemContext context = new AemContext();
    private final ObjectMapper mapper = new ObjectMapper();

    private DestinationLandingPageEditorialArticals model;

    @BeforeEach
    void setUp() {
        model = new DestinationLandingPageEditorialArticals();
        setField(model, "request", context.request());
        setField(model, "mapper", mapper);
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
        JsonNode jsonResponse = createValidResponse();
        context.request().setAttribute("destinationLandingPageResponse", jsonResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article 1", result.get(0).getName());
        assertEquals("Article 2", result.get(1).getName());
    }

    @Test
    void testGetPropertiesList_nullResponse() {
        context.request().setAttribute("destinationLandingPageResponse", null);

        assertThrows(NullPointerException.class, () -> model.getPropertiesList());
    }

    @Test
    void testGetPropertiesList_missingDataPath() throws Exception {
        ObjectNode emptyResponse = mapper.createObjectNode();
        context.request().setAttribute("destinationLandingPageResponse", emptyResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingJcrPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingEditorialPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingCopyPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorial");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingTextFieldsPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorial")
                .putObject("copy");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_textFieldsNotArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorial")
                .putObject("copy").put("textFields", "not an array");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_emptyTextFieldsArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorial")
                .putObject("copy").putArray("textFields");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_invalidJsonCausesIOException() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        ObjectNode data = response.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode editorial = jcr.putObject("editorial");
        ObjectNode copy = editorial.putObject("copy");
        ArrayNode textFields = copy.putArray("textFields");
        
        // Add an item with incorrect type to cause parsing issues
        ObjectNode invalidItem = textFields.addObject();
        invalidItem.putArray("name"); // name expects String but we give array
        
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_withAllFields() throws Exception {
        JsonNode jsonResponse = createResponseWithAllFields();
        context.request().setAttribute("destinationLandingPageResponse", jsonResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        DefaultModel article = result.get(0);
        assertEquals("Full Article", article.getName());
        assertEquals("article-node", article.getNodename());
        assertEquals("/content/articles/1", article.getPath());
        assertEquals("This is the full description", article.getDescription());
    }

    @Test
    void testGetPropertiesList_withPartialFields() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorial\": {\n" +
                "        \"copy\": {\n" +
                "          \"textFields\": [\n" +
                "            {\"name\": \"Partial Article\"}\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JsonNode jsonResponse = mapper.readTree(json);
        context.request().setAttribute("destinationLandingPageResponse", jsonResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Partial Article", result.get(0).getName());
        assertNull(result.get(0).getNodename());
        assertNull(result.get(0).getPath());
    }

    @Test
    void testGetPropertiesList_textFieldsIsObject() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorial")
                .putObject("copy").putObject("textFields");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_multipleArticles() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorial\": {\n" +
                "        \"copy\": {\n" +
                "          \"textFields\": [\n" +
                "            {\"name\": \"Article 1\", \"description\": \"Desc 1\"},\n" +
                "            {\"name\": \"Article 2\", \"description\": \"Desc 2\"},\n" +
                "            {\"name\": \"Article 3\", \"description\": \"Desc 3\"}\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JsonNode jsonResponse = mapper.readTree(json);
        context.request().setAttribute("destinationLandingPageResponse", jsonResponse);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    private JsonNode createValidResponse() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorial\": {\n" +
                "        \"copy\": {\n" +
                "          \"textFields\": [\n" +
                "            {\"name\": \"Article 1\", \"description\": \"Description 1\", \"path\": \"/articles/1\"},\n" +
                "            {\"name\": \"Article 2\", \"description\": \"Description 2\", \"path\": \"/articles/2\"}\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return mapper.readTree(json);
    }

    private JsonNode createResponseWithAllFields() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorial\": {\n" +
                "        \"copy\": {\n" +
                "          \"textFields\": [\n" +
                "            {\n" +
                "              \"name\": \"Full Article\",\n" +
                "              \"nodename\": \"article-node\",\n" +
                "              \"path\": \"/content/articles/1\",\n" +
                "              \"description\": \"This is the full description\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return mapper.readTree(json);
    }
}
