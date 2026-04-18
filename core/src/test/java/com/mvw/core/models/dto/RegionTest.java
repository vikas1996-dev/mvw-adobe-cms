package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegionTest {

    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region();
    }

    @Test
    void testGettersAndSetters() {
        region.setName("Caribbean");
        region.setNodename("caribbean");
        region.setOrder("1");

        assertEquals("Caribbean", region.getName());
        assertEquals("caribbean", region.getNodename());
        assertEquals("1", region.getOrder());
    }

    @Test
    void testNullValues() {
        assertNull(region.getName());
        assertNull(region.getNodename());
        assertNull(region.getOrder());
    }
}
