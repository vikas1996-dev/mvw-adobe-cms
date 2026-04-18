package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlacesTest {

    private Places places;

    @BeforeEach
    void setUp() {
        places = new Places();
    }

    @Test
    void testGetters() {
        // Places only has getters, no setters - use reflection
        setField(places, "name", "Beach Area");
        setField(places, "nodename", "beach-area");
        setField(places, "description", "A beautiful beach area");

        assertEquals("Beach Area", places.getName());
        assertEquals("beach-area", places.getNodename());
        assertEquals("A beautiful beach area", places.getDescription());
    }

    @Test
    void testNullValues() {
        assertNull(places.getName());
        assertNull(places.getNodename());
        assertNull(places.getDescription());
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
