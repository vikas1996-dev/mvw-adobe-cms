package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TagsTest {

    private Tags tags;

    @BeforeEach
    void setUp() {
        tags = new Tags();
    }

    @Test
    void testGettersAndSetters() {
        tags.setName("Beach");
        tags.setNodename("beach");

        assertEquals("Beach", tags.getName());
        assertEquals("beach", tags.getNodename());
    }

    @Test
    void testGetParent() {
        Parent parent = new Parent();
        setField(tags, "parent", parent);
        assertEquals(parent, tags.getParent());
    }

    @Test
    void testToString() {
        tags.setName("Test Tag");
        tags.setNodename("test-tag");
        String toString = tags.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Tags"));
        assertTrue(toString.contains("Test Tag"));
        assertTrue(toString.contains("test-tag"));
    }

    @Test
    void testNullValues() {
        assertNull(tags.getName());
        assertNull(tags.getNodename());
        assertNull(tags.getParent());
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
