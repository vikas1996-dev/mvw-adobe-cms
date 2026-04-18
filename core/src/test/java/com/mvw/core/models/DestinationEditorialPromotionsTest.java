package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.Promotions;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
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

public class DestinationEditorialPromotionsTest {

    private DestinationEditorialPromotions destinationEditorialPromotions;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jahiaApiConfigServiceImpl.getApiImagePath()).thenReturn("");
        destinationEditorialPromotions = new DestinationEditorialPromotions();
        setField(destinationEditorialPromotions, "request", request);
        setField(destinationEditorialPromotions, "jahiaApiConfigServiceImpl", jahiaApiConfigServiceImpl);
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
        when(request.getAttribute("destinationResponse")).thenReturn(jsonResponse);

        List<Promotions> result = destinationEditorialPromotions.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListWithEditorialPath() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithEditorialPath();
        when(request.getAttribute("destinationResponse")).thenReturn(jsonResponse);

        List<Promotions> result = destinationEditorialPromotions.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(p -> 
            p.getPath() != null && p.getPath().toLowerCase().contains("editorial-")));
    }

    @Test
    void testGetPropertiesListWithEmptyPromoDestinations() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithEmptyPromoDestinations();
        when(request.getAttribute("destinationResponse")).thenReturn(jsonResponse);

        List<Promotions> result = destinationEditorialPromotions.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testFilterEditorialPromotionsWithValidList() {
        List<Promotions> promotions = new ArrayList<>();
        
        Promotions promo1 = new Promotions();
        promo1.setPath("/content/editorial-test/promotion");
        promo1.setName("Editorial 1");
        promotions.add(promo1);

        Promotions promo2 = new Promotions();
        promo2.setPath("/content/destination-promo/test");
        promo2.setName("Destination");
        promotions.add(promo2);

        Promotions promo3 = new Promotions();
        promo3.setPath("/content/EDITORIAL-CONTENT/test2");
        promo3.setName("Editorial 2");
        promotions.add(promo3);

        List<Promotions> result = DestinationEditorialPromotions.filterEditorialPromotions(promotions);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> 
            p.getPath().toLowerCase().contains("editorial-")));
    }

    @Test
    void testFilterEditorialPromotionsWithNullList() {
        List<Promotions> result = DestinationEditorialPromotions.filterEditorialPromotions(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterEditorialPromotionsWithEmptyList() {
        List<Promotions> result = DestinationEditorialPromotions.filterEditorialPromotions(new ArrayList<>());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterEditorialPromotionsWithNullPaths() {
        List<Promotions> promotions = new ArrayList<>();
        
        Promotions promo1 = new Promotions();
        promo1.setPath(null);
        promo1.setName("No Path");
        promotions.add(promo1);

        Promotions promo2 = new Promotions();
        promo2.setPath("/content/editorial-test");
        promo2.setName("Valid");
        promotions.add(promo2);

        List<Promotions> result = DestinationEditorialPromotions.filterEditorialPromotions(promotions);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Valid", result.get(0).getName());
    }

    @Test
    void testFilterEditorialPromotionsWithNoMatches() {
        List<Promotions> promotions = new ArrayList<>();
        
        Promotions promo1 = new Promotions();
        promo1.setPath("/content/other-path");
        promotions.add(promo1);

        Promotions promo2 = new Promotions();
        promo2.setPath("/content/destination-promo/test");
        promotions.add(promo2);

        List<Promotions> result = DestinationEditorialPromotions.filterEditorialPromotions(promotions);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterEditorialPromotionsCaseInsensitive() {
        List<Promotions> promotions = new ArrayList<>();
        
        Promotions promo1 = new Promotions();
        promo1.setPath("/content/EDITORIAL-TEST");
        promotions.add(promo1);

        Promotions promo2 = new Promotions();
        promo2.setPath("/content/Editorial-Content");
        promotions.add(promo2);

        Promotions promo3 = new Promotions();
        promo3.setPath("/content/ediTORIAL-mixed");
        promotions.add(promo3);

        List<Promotions> result = DestinationEditorialPromotions.filterEditorialPromotions(promotions);

        assertEquals(3, result.size());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode promotions = jcr.putObject("promotions");
        ArrayNode promoDestinations = promotions.putArray("promoDestinations");
        ObjectNode promo = promoDestinations.addObject();
        promo.put("name", "test-promo");
        promo.put("path", "/content/editorial-test");
        
        return root;
    }

    private ObjectNode createJsonResponseWithEditorialPath() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode promotions = jcr.putObject("promotions");
        ArrayNode promoDestinations = promotions.putArray("promoDestinations");
        
        ObjectNode promo1 = promoDestinations.addObject();
        promo1.put("name", "editorial-1");
        promo1.put("path", "/content/editorial-florida");
        
        ObjectNode promo2 = promoDestinations.addObject();
        promo2.put("name", "destination-promo");
        promo2.put("path", "/content/destination-promo/test");
        
        return root;
    }

    private ObjectNode createJsonResponseWithEmptyPromoDestinations() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode promotions = jcr.putObject("promotions");
        promotions.putArray("promoDestinations");
        
        return root;
    }
}
