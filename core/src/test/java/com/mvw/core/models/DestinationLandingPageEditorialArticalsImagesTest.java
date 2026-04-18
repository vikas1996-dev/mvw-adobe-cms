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
class DestinationLandingPageEditorialArticalsImagesTest {

    private final AemContext context = new AemContext();
    private final ObjectMapper mapper = new ObjectMapper();

    private DestinationLandingPageEditorialArticalsImages model;

    @BeforeEach
    void setUp() {
        model = new DestinationLandingPageEditorialArticalsImages();
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
        assertEquals("Image 1", result.get(0).getName());
        assertEquals("Image 2", result.get(1).getName());
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
    void testGetPropertiesList_missingEditorialImagesPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingImagesPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorialImages");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingNodesPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorialImages")
                .putObject("images");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_nodesNotArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorialImages")
                .putObject("images").put("nodes", "not an array");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_emptyNodesArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorialImages")
                .putObject("images").putArray("nodes");
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
        ObjectNode editorialImages = jcr.putObject("editorialImages");
        ObjectNode images = editorialImages.putObject("images");
        ArrayNode nodes = images.putArray("nodes");
        
        // Add an item with incorrect type to cause parsing issues
        ObjectNode invalidItem = nodes.addObject();
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
        DefaultModel image = result.get(0);
        assertEquals("Full Image", image.getName());
        assertEquals("image-node", image.getNodename());
        assertEquals("/content/dam/images/1.jpg", image.getPath());
        assertEquals("Image description", image.getDescription());
    }

    @Test
    void testGetPropertiesList_withPartialFields() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorialImages\": {\n" +
                "        \"images\": {\n" +
                "          \"nodes\": [\n" +
                "            {\"name\": \"Partial Image\"}\n" +
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
        assertEquals("Partial Image", result.get(0).getName());
        assertNull(result.get(0).getNodename());
        assertNull(result.get(0).getPath());
    }

    @Test
    void testGetPropertiesList_nodesIsObject() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("editorialImages")
                .putObject("images").putObject("nodes");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<DefaultModel> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_multipleImages() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorialImages\": {\n" +
                "        \"images\": {\n" +
                "          \"nodes\": [\n" +
                "            {\"name\": \"Image 1\", \"path\": \"/dam/img1.jpg\"},\n" +
                "            {\"name\": \"Image 2\", \"path\": \"/dam/img2.jpg\"},\n" +
                "            {\"name\": \"Image 3\", \"path\": \"/dam/img3.jpg\"},\n" +
                "            {\"name\": \"Image 4\", \"path\": \"/dam/img4.jpg\"}\n" +
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
        assertEquals(4, result.size());
    }

    @Test
    void testGetPropertiesList_attributeNotSet() {
        // Don't set any attribute - should throw NullPointerException
        assertThrows(NullPointerException.class, () -> model.getPropertiesList());
    }

    @Test
    void testGetPropertiesList_withDescriptionAndPath() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorialImages\": {\n" +
                "        \"images\": {\n" +
                "          \"nodes\": [\n" +
                "            {\"name\": \"Beach Image\", \"description\": \"A beautiful beach\", \"path\": \"/dam/beach.jpg\"}\n" +
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
        assertEquals("Beach Image", result.get(0).getName());
        assertEquals("A beautiful beach", result.get(0).getDescription());
        assertEquals("/dam/beach.jpg", result.get(0).getPath());
    }

    private JsonNode createValidResponse() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"editorialImages\": {\n" +
                "        \"images\": {\n" +
                "          \"nodes\": [\n" +
                "            {\"name\": \"Image 1\", \"path\": \"/dam/images/1.jpg\"},\n" +
                "            {\"name\": \"Image 2\", \"path\": \"/dam/images/2.jpg\"}\n" +
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
                "      \"editorialImages\": {\n" +
                "        \"images\": {\n" +
                "          \"nodes\": [\n" +
                "            {\n" +
                "              \"name\": \"Full Image\",\n" +
                "              \"nodename\": \"image-node\",\n" +
                "              \"path\": \"/content/dam/images/1.jpg\",\n" +
                "              \"description\": \"Image description\"\n" +
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
