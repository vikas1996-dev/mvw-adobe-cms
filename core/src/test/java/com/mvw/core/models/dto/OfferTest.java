package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OfferTest {

    private Offer offer;

    @BeforeEach
    void setUp() {
        offer = new Offer();
    }

    @Test
    void testGettersAndSetters() {
        offer.setTitle("Summer Special");
        offer.setEyebrow("Limited Time");
        offer.setShortDescriptionAd("Save up to 30%");
        offer.setButtonOfferTextAd("Book Now");
        offer.setButtonOfferUrlAd("https://example.com/book");
        offer.setButtonOfferTextAd1("Learn More");
        offer.setButtonOfferUrlAd1("https://example.com/details");
        offer.setDescriptionAd("Full description of the summer special offer");

        assertEquals("Summer Special", offer.getTitle());
        assertEquals("Limited Time", offer.getEyebrow());
        assertEquals("Save up to 30%", offer.getShortDescriptionAd());
        assertEquals("Book Now", offer.getButtonOfferTextAd());
        assertEquals("https://example.com/book", offer.getButtonOfferUrlAd());
        assertEquals("Learn More", offer.getButtonOfferTextAd1());
        assertEquals("https://example.com/details", offer.getButtonOfferUrlAd1());
        assertEquals("Full description of the summer special offer", offer.getDescriptionAd());
    }

    @Test
    void testSetAndGetImages() {
        List<Offer.Image> imagesList = new ArrayList<>();
        Offer.Image image = new Offer.Image("Photo1", "Alt text", "/content/dam/photo.jpg", "16:9");
        imagesList.add(image);
        
        offer.setImages(imagesList);
        
        assertEquals(imagesList, offer.getImages());
        assertEquals(1, offer.getImages().size());
    }

    @Test
    void testImageInnerClass() {
        Offer.Image image = new Offer.Image("Beach Photo", "Beautiful beach view", "/content/dam/beach.jpg", "4:3");
        
        assertEquals("Beach Photo", image.getName());
        assertEquals("Beautiful beach view", image.getAltText());
        assertEquals("/content/dam/beach.jpg", image.getPath());
        assertEquals("4:3", image.getRatio());
    }

    @Test
    void testToString() {
        offer.setTitle("Test Offer");
        offer.setEyebrow("Test");
        
        String toString = offer.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Offer"));
        assertTrue(toString.contains("Test Offer"));
    }

    @Test
    void testNullValues() {
        assertNull(offer.getTitle());
        assertNull(offer.getEyebrow());
        assertNull(offer.getShortDescriptionAd());
        assertNull(offer.getButtonOfferTextAd());
        assertNull(offer.getButtonOfferUrlAd());
        assertNull(offer.getButtonOfferTextAd1());
        assertNull(offer.getButtonOfferUrlAd1());
        assertNull(offer.getDescriptionAd());
        assertNull(offer.getImages());
    }
}
