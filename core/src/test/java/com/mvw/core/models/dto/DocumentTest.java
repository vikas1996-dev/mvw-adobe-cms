package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTest {

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document();
    }

    @Test
    void testGetters() {
        // Document only has getters, no setters - use reflection
        setField(document, "name", "Terms of Service");
        setField(document, "nodename", "terms-of-service");
        setField(document, "path", "/content/documents/tos.pdf");

        assertEquals("Terms of Service", document.getName());
        assertEquals("terms-of-service", document.getNodename());
        assertEquals("/content/documents/tos.pdf", document.getPath());
    }

    @Test
    void testNullValues() {
        assertNull(document.getName());
        assertNull(document.getNodename());
        assertNull(document.getPath());
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
