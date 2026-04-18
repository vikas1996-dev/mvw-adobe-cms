package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlagsTest {

    private Flags flags;

    @BeforeEach
    void setUp() {
        flags = new Flags();
    }

    @Test
    void testGetters() {
        // Flags only has getters, no setters - use reflection
        setField(flags, "name", "City Collection");
        setField(flags, "nodename", "city-collection");

        assertEquals("City Collection", flags.getName());
        assertEquals("city-collection", flags.getNodename());
    }

    @Test
    void testNullValues() {
        assertNull(flags.getName());
        assertNull(flags.getNodename());
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
