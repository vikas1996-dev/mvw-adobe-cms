package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiningTest {

    private Dining dining;

    @BeforeEach
    void setUp() {
        dining = new Dining();
    }

    @Test
    void testGetters() {
        // Set fields using reflection since there are no setters
        setField(dining, "name", "Main Restaurant");
        setField(dining, "nodename", "main-restaurant");
        setField(dining, "description", "Fine dining experience");
        setField(dining, "typeOnOffSite", "on-site");
        setField(dining, "typeCuisine", "American");
        setField(dining, "typeAtmosphere", "casual");
        setField(dining, "mobileOrdering", true);
        setField(dining, "hours", "6:00 AM - 10:00 PM");
        setField(dining, "phone", "555-1234");
        setField(dining, "priority", "1");
        setField(dining, "urlMenu", "https://menu.example.com");

        assertEquals("Main Restaurant", dining.getName());
        assertEquals("main-restaurant", dining.getNodename());
        assertEquals("Fine dining experience", dining.getDescription());
        assertEquals("on-site", dining.getTypeOnOffSite());
        assertEquals("American", dining.getTypeCuisine());
        assertEquals("casual", dining.getTypeAtmosphere());
        assertEquals(true, dining.getMobileOrdering());
        assertEquals("6:00 AM - 10:00 PM", dining.getHours());
        assertEquals("555-1234", dining.getPhone());
        assertEquals("1", dining.getPriority());
        assertEquals("https://menu.example.com", dining.getUrlMenu());
    }

    @Test
    void testGetImages() {
        List<Images> imagesList = new ArrayList<>();
        Images image = new Images();
        imagesList.add(image);
        
        setField(dining, "images", imagesList);
        assertEquals(imagesList, dining.getImages());
    }

    @Test
    void testGetImagesJsonWithEmptyList() {
        setField(dining, "images", new ArrayList<>());
        String json = dining.getImagesJson();
        assertEquals("[]", json);
    }

    @Test
    void testGetImagesJsonWithNull() {
        String json = dining.getImagesJson();
        assertEquals("null", json);
    }

    @Test
    void testToString() {
        setField(dining, "name", "Test Restaurant");
        String toString = dining.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Dining"));
    }

    @Test
    void testNullValues() {
        assertNull(dining.getName());
        assertNull(dining.getNodename());
        assertNull(dining.getImages());
        assertNull(dining.getDescription());
        assertNull(dining.getTypeOnOffSite());
        assertNull(dining.getTypeCuisine());
        assertNull(dining.getTypeAtmosphere());
        assertNull(dining.getMobileOrdering());
        assertNull(dining.getHours());
        assertNull(dining.getPhone());
        assertNull(dining.getPriority());
        assertNull(dining.getUrlMenu());
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
