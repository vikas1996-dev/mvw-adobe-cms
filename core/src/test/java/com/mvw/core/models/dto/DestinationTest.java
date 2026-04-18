package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DestinationTest {

    private Destination destination;

    @BeforeEach
    void setUp() {
        destination = new Destination();
    }

    @Test
    void testGettersAndSetters() {
        destination.setNodename("cancun");
        destination.setTitle("Cancun");
        
        assertEquals("cancun", destination.getNodename());
        assertEquals("Cancun", destination.getTitle());
    }

    @Test
    void testSetAndGetStructuredContent() {
        List<String> content = new ArrayList<>();
        content.add("Content item 1");
        content.add("Content item 2");
        
        destination.setStructuredContent(content);
        
        assertEquals(content, destination.getStructuredContent());
        assertEquals(2, destination.getStructuredContent().size());
    }

    @Test
    void testStructuredContentEmpty() {
        destination.setStructuredContent(new ArrayList<>());
        assertTrue(destination.getStructuredContent().isEmpty());
    }

    @Test
    void testNullValues() {
        assertNull(destination.getNodename());
        assertNull(destination.getTitle());
        assertNull(destination.getStructuredContent());
    }
}
