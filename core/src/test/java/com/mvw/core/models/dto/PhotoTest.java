package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PhotoTest {

    private Photo photo;

    @BeforeEach
    void setUp() {
        photo = new Photo();
    }

    @Test
    void testGettersAndSetters() {
        photo.setName("Beach Photo");
        photo.setPath("/content/dam/beach.jpg");
        photo.setWidth("1920");
        photo.setHeight("1080");
        photo.setRatio("16:9");
        photo.setReferenceId("ref-001");
        photo.setAltText("Beautiful beach view");

        assertEquals("Beach Photo", photo.getName());
        assertEquals("/content/dam/beach.jpg", photo.getPath());
        assertEquals("1920", photo.getWidth());
        assertEquals("1080", photo.getHeight());
        assertEquals("16:9", photo.getRatio());
        assertEquals("ref-001", photo.getReferenceId());
        assertEquals("Beautiful beach view", photo.getAltText());
    }

    @Test
    void testToString() {
        photo.setName("Test Photo");
        photo.setPath("/test/path.jpg");
        photo.setWidth("800");
        photo.setHeight("600");
        
        String toString = photo.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Photo"));
        assertTrue(toString.contains("Test Photo"));
        assertTrue(toString.contains("/test/path.jpg"));
    }

    @Test
    void testNullValues() {
        assertNull(photo.getName());
        assertNull(photo.getPath());
        assertNull(photo.getWidth());
        assertNull(photo.getHeight());
        assertNull(photo.getRatio());
        assertNull(photo.getReferenceId());
        assertNull(photo.getAltText());
    }
}
