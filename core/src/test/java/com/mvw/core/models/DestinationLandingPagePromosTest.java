package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Promotions;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DestinationLandingPagePromosTest {

    private final AemContext context = AppAemContext.newAemContext();
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private DestinationLandingPagePromos model;

    @BeforeEach
    void setUp() {
        when(jahiaApiConfigServiceImpl.getApiImagePath()).thenReturn("");
        model = new DestinationLandingPagePromos();
        setField(model, "request", context.request());
        setField(model, "jahiaApiConfigServiceImpl", jahiaApiConfigServiceImpl);
        setField(model, "baseImagePath", "");
    }

    private void setField(Object target, String fieldName, Object value) {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    try {
                        field.setAccessible(true);
                        field.set(target, value);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to set " + fieldName, e);
                    }
                }
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }

    @Test
    void testGetPropertiesList_success() throws Exception {
        JsonNode jsonResponse = createValidResponse();
        context.request().setAttribute("destinationLandingPageResponse", jsonResponse);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
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

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingJcrPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingPromotionsPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_missingPromoDestinationsPath() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("promotions");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_promoDestinationsNotArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("promotions")
                .put("promoDestinations", "not an array");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_emptyPromoDestinationsArray() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("promotions")
                .putArray("promoDestinations");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_invalidJsonCausesIOException() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        ObjectNode data = response.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        ObjectNode promotions = jcr.putObject("promotions");
        ArrayNode promoDestinations = promotions.putArray("promoDestinations");
        
        // Add an item with incorrect type that may cause parsing issues
        ObjectNode invalidItem = promoDestinations.addObject();
        invalidItem.putArray("name"); // name expects String but we give array
        
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_withAllFields() throws Exception {
        JsonNode jsonResponse = createResponseWithAllFields();
        context.request().setAttribute("destinationLandingPageResponse", jsonResponse);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_withPartialFields() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"promotions\": {\n" +
                "        \"promoDestinations\": [\n" +
                "          {\"name\": \"Partial Promo\"}\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JsonNode jsonResponse = mapper.readTree(json);
        context.request().setAttribute("destinationLandingPageResponse", jsonResponse);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesList_promoDestinationsIsObject() throws Exception {
        ObjectNode response = mapper.createObjectNode();
        response.putObject("data").putObject("jcr").putObject("promotions")
                .putObject("promoDestinations");
        context.request().setAttribute("destinationLandingPageResponse", response);

        List<Promotions> result = model.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private JsonNode createValidResponse() throws Exception {
        String json = "{\n" +
                "  \"data\": {\n" +
                "    \"jcr\": {\n" +
                "      \"promotions\": {\n" +
                "        \"promoDestinations\": [\n" +
                "          {\"name\": \"Promo 1\", \"nodename\": \"promo1\", \"path\": \"/promos/1\"},\n" +
                "          {\"name\": \"Promo 2\", \"nodename\": \"promo2\", \"path\": \"/promos/2\"}\n" +
                "        ]\n" +
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
                "      \"promotions\": {\n" +
                "        \"promoDestinations\": [\n" +
                "          {\n" +
                "            \"name\": \"Full Promo\",\n" +
                "            \"nodename\": \"promo-node\",\n" +
                "            \"eyeBrow\": \"Eyebrow Text\",\n" +
                "            \"priority\": \"high\",\n" +
                "            \"path\": \"/content/promos/1\",\n" +
                "            \"shortDescriptionAd\": \"Short Description\",\n" +
                "            \"descriptionAd\": \"Full Description\",\n" +
                "            \"buttonOfferTextAd\": \"Click Here\",\n" +
                "            \"buttonOfferUrlAd\": \"/promo-link\",\n" +
                "            \"buttonOfferTextAd1\": \"Learn More\",\n" +
                "            \"buttonOfferUrlAd1\": \"/promo-link-2\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return mapper.readTree(json);
    }
}
