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

public class DestinationDetailsPromotionsTest {

    private DestinationDetailsPromotions destinationDetailsPromotions;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jahiaApiConfigServiceImpl.getApiImagePath()).thenReturn("");
        destinationDetailsPromotions = new DestinationDetailsPromotions();
        setField(destinationDetailsPromotions, "request", request);
        setField(destinationDetailsPromotions, "jahiaApiConfigServiceImpl", jahiaApiConfigServiceImpl);
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

        List<Promotions> result = destinationDetailsPromotions.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testGetPropertiesListWithDestinationPromoPath() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithDestinationPromo();
        when(request.getAttribute("destinationResponse")).thenReturn(jsonResponse);

        List<Promotions> result = destinationDetailsPromotions.getPropertiesList();

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(p -> 
            p.getPath() != null && p.getPath().toLowerCase().contains("destination-promo")));
    }

    @Test
    void testGetPropertiesListWithDefaultPromotions() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithDefaultPromotions();
        when(request.getAttribute("destinationResponse")).thenReturn(jsonResponse);

        List<Promotions> result = destinationDetailsPromotions.getPropertiesList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetPropertiesListWithEmptyPromoDestinations() throws IOException {
        ObjectNode jsonResponse = createJsonResponseWithEmptyPromoDestinations();
        when(request.getAttribute("destinationResponse")).thenReturn(jsonResponse);

        List<Promotions> result = destinationDetailsPromotions.getPropertiesList();

        assertNotNull(result);
    }

    @Test
    void testFilterEditorialPromotionsWithValidList() {
        List<Promotions> promotions = new ArrayList<>();
        
        Promotions promo1 = new Promotions();
        promo1.setPath("/content/destination-promo/test");
        promo1.setName("Destination Promo 1");
        promotions.add(promo1);

        Promotions promo2 = new Promotions();
        promo2.setPath("/content/editorial-test");
        promo2.setName("Editorial");
        promotions.add(promo2);

        Promotions promo3 = new Promotions();
        promo3.setPath("/content/DESTINATION-PROMO/test2");
        promo3.setName("Destination Promo 2");
        promotions.add(promo3);

        List<Promotions> result = DestinationDetailsPromotions.filterEditorialPromotions(promotions);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> 
            p.getPath().toLowerCase().contains("destination-promo")));
    }

    @Test
    void testFilterEditorialPromotionsWithNullList() {
        List<Promotions> result = DestinationDetailsPromotions.filterEditorialPromotions(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterEditorialPromotionsWithEmptyList() {
        List<Promotions> result = DestinationDetailsPromotions.filterEditorialPromotions(new ArrayList<>());

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
        promo2.setPath("/content/destination-promo/test");
        promo2.setName("Valid");
        promotions.add(promo2);

        List<Promotions> result = DestinationDetailsPromotions.filterEditorialPromotions(promotions);

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

        List<Promotions> result = DestinationDetailsPromotions.filterEditorialPromotions(promotions);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private ObjectNode createValidJsonResponse() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode promotions = jcr.putObject("promotions");
        ArrayNode promoDestinations = promotions.putArray("promoDestinations");
        ObjectNode promo = promoDestinations.addObject();
        promo.put("name", "test-promo");
        promo.put("path", "/content/destination-promo/test");
        
        ObjectNode defaultPromotions = jcr.putObject("defaultPromotions");
        ArrayNode defaultNodes = defaultPromotions.putArray("nodes");
        ObjectNode defaultPromo = defaultNodes.addObject();
        defaultPromo.put("name", "default-promo");
        
        return root;
    }

    private ObjectNode createJsonResponseWithDestinationPromo() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode promotions = jcr.putObject("promotions");
        ArrayNode promoDestinations = promotions.putArray("promoDestinations");
        
        ObjectNode promo1 = promoDestinations.addObject();
        promo1.put("name", "promo-1");
        promo1.put("path", "/content/destination-promo/florida");
        
        ObjectNode promo2 = promoDestinations.addObject();
        promo2.put("name", "promo-2");
        promo2.put("path", "/content/editorial/test");
        
        ObjectNode defaultPromotions = jcr.putObject("defaultPromotions");
        defaultPromotions.putArray("nodes");
        
        return root;
    }

    private ObjectNode createJsonResponseWithDefaultPromotions() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode promotions = jcr.putObject("promotions");
        promotions.putArray("promoDestinations");
        
        ObjectNode defaultPromotions = jcr.putObject("defaultPromotions");
        ArrayNode defaultNodes = defaultPromotions.putArray("nodes");
        
        ObjectNode default1 = defaultNodes.addObject();
        default1.put("name", "default-1");
        default1.put("path", "/content/defaults/test");
        
        ObjectNode default2 = defaultNodes.addObject();
        default2.put("name", "default-2");
        
        return root;
    }

    private ObjectNode createJsonResponseWithEmptyPromoDestinations() {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode data = root.putObject("data");
        ObjectNode jcr = data.putObject("jcr");
        
        ObjectNode promotions = jcr.putObject("promotions");
        promotions.putArray("promoDestinations");
        
        ObjectNode defaultPromotions = jcr.putObject("defaultPromotions");
        defaultPromotions.putArray("nodes");
        
        return root;
    }
}
