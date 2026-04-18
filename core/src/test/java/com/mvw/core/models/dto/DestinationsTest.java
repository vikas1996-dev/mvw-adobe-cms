package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DestinationsTest {

    private Destinations destinations;

    @BeforeEach
    void setUp() {
        destinations = new Destinations();
    }

    @Test
    void testGettersAndSetters() {
        destinations.setName("Caribbean");
        destinations.setNodename("caribbean");
        destinations.setResortCount(15);

        assertEquals("Caribbean", destinations.getName());
        assertEquals("caribbean", destinations.getNodename());
        assertEquals(15, destinations.getResortCount());
    }

    @Test
    void testSetAndGetImages() {
        List<Images> imagesList = new ArrayList<>();
        imagesList.add(new Images());
        
        destinations.setImages(imagesList);
        
        assertEquals(imagesList, destinations.getImages());
    }

    @Test
    void testSetAndGetRegion() {
        Region region = new Region();
        region.setName("North America");
        
        destinations.setRegion(region);
        
        assertEquals(region, destinations.getRegion());
        assertEquals("North America", destinations.getRegion().getName());
    }

    @Test
    void testNullValues() {
        assertNull(destinations.getName());
        assertNull(destinations.getNodename());
        assertNull(destinations.getImages());
        assertNull(destinations.getRegion());
        assertEquals(0, destinations.getResortCount());
    }
}
