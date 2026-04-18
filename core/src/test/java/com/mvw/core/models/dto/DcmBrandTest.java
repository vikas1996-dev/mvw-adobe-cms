package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DcmBrandTest {

    private DcmBrand dcmBrand;

    @BeforeEach
    void setUp() {
        dcmBrand = new DcmBrand();
    }

    @Test
    void testGetters() {
        // DcmBrand only has getters, no setters - use reflection
        setField(dcmBrand, "name", "Marriott");
        setField(dcmBrand, "nodename", "marriott");
        
        List<Object> images = new ArrayList<>();
        images.add("image1.jpg");
        setField(dcmBrand, "images", images);

        assertEquals("Marriott", dcmBrand.getName());
        assertEquals("marriott", dcmBrand.getNodename());
        assertEquals(images, dcmBrand.getImages());
    }

    @Test
    void testNullValues() {
        assertNull(dcmBrand.getName());
        assertNull(dcmBrand.getNodename());
        assertNull(dcmBrand.getImages());
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
