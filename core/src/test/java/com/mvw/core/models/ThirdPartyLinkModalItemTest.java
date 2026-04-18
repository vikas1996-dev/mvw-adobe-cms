package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThirdPartyLinkModalItemTest {

    private ThirdPartyLinkModalItem item;

    @BeforeEach
    void setUp() {
        item = new ThirdPartyLinkModalItem();
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

    @Test
    void testGetCtaText() {
        setField(item, "ctaText", "Click Here");
        assertEquals("Click Here", item.getCtaText());
    }

    @Test
    void testGetCtaTextNull() {
        setField(item, "ctaText", null);
        assertNull(item.getCtaText());
    }

    @Test
    void testGetCtaStyleWithValue() {
        setField(item, "ctaStyle", "secondary");
        assertEquals("secondary", item.getCtaStyle());
    }

    @Test
    void testGetCtaStyleWithNullReturnsDefault() {
        setField(item, "ctaStyle", null);
        assertEquals("Primary Button", item.getCtaStyle());
    }

    @Test
    void testGetCtaPlacementWithValue() {
        setField(item, "ctaPlacement", "left");
        assertEquals("left", item.getCtaPlacement());
    }

    @Test
    void testGetCtaPlacementWithNullReturnsDefault() {
        setField(item, "ctaPlacement", null);
        assertEquals("None", item.getCtaPlacement());
    }

    @Test
    void testGetCtaSizeWithValue() {
        setField(item, "ctaSize", "small");
        assertEquals("small", item.getCtaSize());
    }

    @Test
    void testGetCtaSizeWithNullReturnsDefault() {
        setField(item, "ctaSize", null);
        assertEquals("Large", item.getCtaSize());
    }

    @Test
    void testGetCtaTabWithValue() {
        setField(item, "ctaTab", "newTab");
        assertEquals("newTab", item.getCtaTab());
    }

    @Test
    void testGetCtaTabWithNullReturnsDefault() {
        setField(item, "ctaTab", null);
        assertEquals("Same Tab", item.getCtaTab());
    }

    @Test
    void testAllDefaultValues() {
        assertEquals("Primary Button", item.getCtaStyle());
        assertEquals("None", item.getCtaPlacement());
        assertEquals("Large", item.getCtaSize());
        assertEquals("Same Tab", item.getCtaTab());
        assertNull(item.getCtaText());
    }
}
