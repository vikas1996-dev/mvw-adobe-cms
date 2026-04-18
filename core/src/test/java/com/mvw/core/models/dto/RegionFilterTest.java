package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegionFilterTest {

    private RegionFilter regionFilter;

    @BeforeEach
    void setUp() {
        regionFilter = new RegionFilter();
    }

    @Test
    void testGettersAndSetters() {
        regionFilter.setHeader("Caribbean");
        
        List<String> subHeaders = new ArrayList<>();
        subHeaders.add("Jamaica");
        subHeaders.add("Aruba");
        regionFilter.setSubHeader(subHeaders);

        assertEquals("Caribbean", regionFilter.getHeader());
        assertEquals(subHeaders, regionFilter.getSubHeader());
        assertEquals(2, regionFilter.getSubHeader().size());
    }

    @Test
    void testToString() {
        regionFilter.setHeader("Test Region");
        
        String toString = regionFilter.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("RegionFilter"));
        assertTrue(toString.contains("Test Region"));
    }

    @Test
    void testNullValues() {
        assertNull(regionFilter.getHeader());
        assertNull(regionFilter.getSubHeader());
    }
}
