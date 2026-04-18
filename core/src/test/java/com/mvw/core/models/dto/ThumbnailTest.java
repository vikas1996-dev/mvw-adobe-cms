package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThumbnailTest {

    private Thumbnail thumbnail;

    @BeforeEach
    void setUp() {
        thumbnail = new Thumbnail();
    }

    @Test
    void testGettersAndSetters() {
        thumbnail.setPath("/content/dam/thumbnail.jpg");
        thumbnail.setWidth("800");
        thumbnail.setHeight("600");
        thumbnail.setRatio("4:3");

        assertEquals("/content/dam/thumbnail.jpg", thumbnail.getPath());
        assertEquals("800", thumbnail.getWidth());
        assertEquals("600", thumbnail.getHeight());
        assertEquals("4:3", thumbnail.getRatio());
    }

    @Test
    void testWidthAndHeightWithZero() {
        thumbnail.setWidth("0");
        thumbnail.setHeight("0");
        
        assertEquals("0", thumbnail.getWidth());
        assertEquals("0", thumbnail.getHeight());
    }

    @Test
    void testNullValues() {
        assertNull(thumbnail.getPath());
        assertNull(thumbnail.getWidth());
        assertNull(thumbnail.getHeight());
        assertNull(thumbnail.getRatio());
    }
}
