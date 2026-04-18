package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentsTest {

    private Documents documents;

    @BeforeEach
    void setUp() {
        documents = new Documents();
    }

    @Test
    void testGetDocument() {
        Document document = new Document();
        setField(documents, "document", document);
        assertEquals(document, documents.getDocument());
    }

    @Test
    void testNullValue() {
        assertNull(documents.getDocument());
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
