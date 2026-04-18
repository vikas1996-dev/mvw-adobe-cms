package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FeaturedResortTest {

    private FeaturedResort featuredResort;

    @BeforeEach
    void setUp() {
        featuredResort = new FeaturedResort();
    }

    @Test
    void testSetAndGetRegion() {
        Region region = new Region();
        region.setName("Caribbean");
        
        featuredResort.setRegion(region);
        
        assertEquals(region, featuredResort.getRegion());
        assertEquals("Caribbean", featuredResort.getRegion().getName());
    }

    @Test
    void testSetAndGetReferenceProperty() {
        List<ReferenceProperty> properties = new ArrayList<>();
        ReferenceProperty prop = new ReferenceProperty();
        prop.setName("Featured Resort 1");
        properties.add(prop);
        
        featuredResort.setReferenceProperty(properties);
        
        assertEquals(properties, featuredResort.getReferenceProperty());
        assertEquals(1, featuredResort.getReferenceProperty().size());
    }

    @Test
    void testNullValues() {
        assertNull(featuredResort.getRegion());
        assertNull(featuredResort.getReferenceProperty());
    }
}
