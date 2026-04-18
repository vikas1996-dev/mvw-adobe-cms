package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReferenceTest {

    private Reference reference;

    @BeforeEach
    void setUp() {
        reference = new Reference();
    }

    @Test
    void testGetName() {
        // Reference only has getter, no setter - use reflection
        setField(reference, "name", "Reference Name");
        assertEquals("Reference Name", reference.getName());
    }

    @Test
    void testNullValue() {
        assertNull(reference.getName());
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
