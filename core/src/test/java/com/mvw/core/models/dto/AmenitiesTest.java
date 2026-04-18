package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmenitiesTest {

    private Amenities amenities;

    @BeforeEach
    void setUp() {
        amenities = new Amenities();
    }

    @Test
    void testGetters() {
        // Amenities only has getters, no setters - use reflection
        setField(amenities, "name", "Swimming Pool");
        setField(amenities, "nodename", "swimming-pool");
        setField(amenities, "shortTitle", "Pool");
        setField(amenities, "description", "Olympic sized swimming pool");
        setField(amenities, "priority", "1");
        setField(amenities, "icon", "pool-icon");
        setField(amenities, "mvcsIcon", "mvcs-pool");
        setField(amenities, "featured", "true");

        assertEquals("Swimming Pool", amenities.getName());
        assertEquals("swimming-pool", amenities.getNodename());
        assertEquals("Pool", amenities.getShortTitle());
        assertEquals("Olympic sized swimming pool", amenities.getDescription());
        assertEquals("1", amenities.getPriority());
        assertEquals("pool-icon", amenities.getIcon());
        assertEquals("mvcs-pool", amenities.getMvcsIcon());
        assertEquals("true", amenities.getFeatured());
    }

    @Test
    void testGetImages() {
        List<Images> imagesList = new ArrayList<>();
        imagesList.add(new Images());
        setField(amenities, "images", imagesList);
        
        assertEquals(imagesList, amenities.getImages());
    }

    @Test
    void testToString() {
        setField(amenities, "name", "Test Amenity");
        setField(amenities, "nodename", "test-amenity");
        
        String toString = amenities.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Amenities"));
        assertTrue(toString.contains("Test Amenity"));
    }

    @Test
    void testNullValues() {
        assertNull(amenities.getName());
        assertNull(amenities.getNodename());
        assertNull(amenities.getShortTitle());
        assertNull(amenities.getDescription());
        assertNull(amenities.getPriority());
        assertNull(amenities.getIcon());
        assertNull(amenities.getMvcsIcon());
        assertNull(amenities.getFeatured());
        assertNull(amenities.getImages());
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
}
