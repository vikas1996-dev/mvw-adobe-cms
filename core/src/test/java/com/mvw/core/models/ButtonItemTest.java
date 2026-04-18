package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ButtonItemTest {

    private ButtonItem buttonItem;

    @BeforeEach
    void setUp() {
        buttonItem = new ButtonItem();

        setField(buttonItem, "ctaStyle", "primary");
        setField(buttonItem, "ctaSize", "large");
        setField(buttonItem, "ctaAlignment", "center");
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
    void testGetCtaStyle() {
        assertEquals("primary", buttonItem.getCtaStyle());
    }

    @Test
    void testGetCtaSize() {
        assertEquals("large", buttonItem.getCtaSize());
    }

    @Test
    void testGetCtaAlignment() {
        assertEquals("center", buttonItem.getCtaAlignment());
    }

    @Test
    void testCtaStyleSecondary() {
        setField(buttonItem, "ctaStyle", "secondary");
        assertEquals("secondary", buttonItem.getCtaStyle());
    }

    @Test
    void testCtaStyleTertiary() {
        setField(buttonItem, "ctaStyle", "tertiary");
        assertEquals("tertiary", buttonItem.getCtaStyle());
    }

    @Test
    void testCtaSizeMedium() {
        setField(buttonItem, "ctaSize", "medium");
        assertEquals("medium", buttonItem.getCtaSize());
    }

    @Test
    void testCtaSizeSmall() {
        setField(buttonItem, "ctaSize", "small");
        assertEquals("small", buttonItem.getCtaSize());
    }

    @Test
    void testCtaAlignmentLeft() {
        setField(buttonItem, "ctaAlignment", "left");
        assertEquals("left", buttonItem.getCtaAlignment());
    }

    @Test
    void testCtaAlignmentRight() {
        setField(buttonItem, "ctaAlignment", "right");
        assertEquals("right", buttonItem.getCtaAlignment());
    }

    @Test
    void testNullValues() {
        ButtonItem emptyButtonItem = new ButtonItem();
        assertNull(emptyButtonItem.getCtaStyle());
        assertNull(emptyButtonItem.getCtaSize());
        assertNull(emptyButtonItem.getCtaAlignment());
    }
}
