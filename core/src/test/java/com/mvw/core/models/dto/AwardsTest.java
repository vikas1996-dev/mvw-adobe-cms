package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AwardsTest {

    private Awards awards;

    @BeforeEach
    void setUp() {
        awards = new Awards();
    }

    @Test
    void testGettersAndSetters() {
        awards.setName("TripAdvisor Excellence Award");
        awards.setNodename("tripadvisor-excellence");
        awards.setAwardLink("https://tripadvisor.com/award");
        awards.setDescription("Certificate of Excellence");
        awards.setPriority("1");

        assertEquals("TripAdvisor Excellence Award", awards.getName());
        assertEquals("tripadvisor-excellence", awards.getNodename());
        assertEquals("https://tripadvisor.com/award", awards.getAwardLink());
        assertEquals("Certificate of Excellence", awards.getDescription());
        assertEquals("1", awards.getPriority());
    }

    @Test
    void testSetAndGetImages() {
        List<Images> imagesList = new ArrayList<>();
        Images image = new Images();
        imagesList.add(image);
        
        awards.setImages(imagesList);
        assertEquals(imagesList, awards.getImages());
    }

    @Test
    void testToString() {
        awards.setName("Test Award");
        String toString = awards.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Awards"));
    }

    @Test
    void testNullValues() {
        assertNull(awards.getName());
        assertNull(awards.getNodename());
        assertNull(awards.getAwardLink());
        assertNull(awards.getDescription());
        assertNull(awards.getPriority());
        assertNull(awards.getImages());
    }
}
