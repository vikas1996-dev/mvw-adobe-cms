package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PromotionsTest {

    private Promotions promotions;

    @BeforeEach
    void setUp() {
        promotions = new Promotions();
    }

    @Test
    void testGettersAndSetters() {
        promotions.setNodename("summer-promo");
        promotions.setName("Summer Promotion");
        promotions.setEyeBrow("Limited Time");
        promotions.setPriority("1");
        promotions.setPath("/content/promotions/summer");
        promotions.setShortDescriptionAd("Save big this summer");
        promotions.setButtonOfferTextAd("Book Now");
        promotions.setButtonOfferUrlAd("https://example.com/book");
        promotions.setButtonOfferTextAd1("Details");
        promotions.setButtonOfferUrlAd1("https://example.com/details");
        promotions.setDescriptionAd("Full description");

        assertEquals("summer-promo", promotions.getNodename());
        assertEquals("Summer Promotion", promotions.getName());
        assertEquals("Limited Time", promotions.getEyeBrow());
        assertEquals("1", promotions.getPriority());
        assertEquals("/content/promotions/summer", promotions.getPath());
        assertEquals("Save big this summer", promotions.getShortDescriptionAd());
        assertEquals("Book Now", promotions.getButtonOfferTextAd());
        assertEquals("https://example.com/book", promotions.getButtonOfferUrlAd());
        assertEquals("Details", promotions.getButtonOfferTextAd1());
        assertEquals("https://example.com/details", promotions.getButtonOfferUrlAd1());
        assertEquals("Full description", promotions.getDescriptionAd());
    }

    @Test
    void testSetAndGetPersona() {
        List<String> personas = new ArrayList<>();
        personas.add("Family");
        personas.add("Couples");
        
        promotions.setPersona(personas);
        
        assertEquals(personas, promotions.getPersona());
    }

    @Test
    void testSetAndGetPlacementId() {
        List<Tags> placements = new ArrayList<>();
        Tags tag = new Tags();
        tag.setName("Homepage");
        placements.add(tag);
        
        promotions.setPlacementId(placements);
        
        assertEquals(placements, promotions.getPlacementId());
    }

    @Test
    void testSetAndGetDestinations() {
        List<Tags> destinations = new ArrayList<>();
        Tags dest = new Tags();
        dest.setName("Cancun");
        destinations.add(dest);
        
        promotions.setDestinations(destinations);
        
        assertEquals(destinations, promotions.getDestinations());
    }

    @Test
    void testSetAndGetImagesAd() {
        List<ImageAd> imagesAd = new ArrayList<>();
        ImageAd imageAd = new ImageAd();
        imagesAd.add(imageAd);
        
        promotions.setImagesAd(imagesAd);
        
        assertEquals(imagesAd, promotions.getImagesAd());
    }

    @Test
    void testToString() {
        promotions.setNodename("test-promo");
        promotions.setName("Test Promotion");
        
        String toString = promotions.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Promotions"));
        assertTrue(toString.contains("test-promo"));
    }

    @Test
    void testNullValues() {
        assertNull(promotions.getNodename());
        assertNull(promotions.getName());
        assertNull(promotions.getEyeBrow());
        assertNull(promotions.getPriority());
        assertNull(promotions.getPath());
        assertNull(promotions.getPersona());
        assertNull(promotions.getPlacementId());
        assertNull(promotions.getShortDescriptionAd());
        assertNull(promotions.getButtonOfferTextAd());
        assertNull(promotions.getButtonOfferUrlAd());
        assertNull(promotions.getButtonOfferTextAd1());
        assertNull(promotions.getButtonOfferUrlAd1());
        assertNull(promotions.getDescriptionAd());
        assertNull(promotions.getImagesAd());
        assertNull(promotions.getDestinations());
    }
}
