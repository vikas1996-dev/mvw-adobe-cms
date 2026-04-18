package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomButtonTest {

    private CustomButton customButton;

    @BeforeEach
    void setUp() {
        customButton = new CustomButton();

        setField(customButton, "ctaStyle", "primary");
        setField(customButton, "ctaSize", "large");
        setField(customButton, "ctaAlignment", "center");
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
        assertEquals("primary", customButton.getCtaStyle());
    }

    @Test
    void testGetCtaSize() {
        assertEquals("large", customButton.getCtaSize());
    }

    @Test
    void testGetCtaAlignment() {
        assertEquals("center", customButton.getCtaAlignment());
    }

    @Test
    void testCtaStyleSecondary() {
        setField(customButton, "ctaStyle", "secondary");
        assertEquals("secondary", customButton.getCtaStyle());
    }

    @Test
    void testCtaStyleTertiary() {
        setField(customButton, "ctaStyle", "tertiary");
        assertEquals("tertiary", customButton.getCtaStyle());
    }

    @Test
    void testCtaSizeMedium() {
        setField(customButton, "ctaSize", "medium");
        assertEquals("medium", customButton.getCtaSize());
    }

    @Test
    void testCtaSizeSmall() {
        setField(customButton, "ctaSize", "small");
        assertEquals("small", customButton.getCtaSize());
    }

    @Test
    void testCtaAlignmentLeft() {
        setField(customButton, "ctaAlignment", "left");
        assertEquals("left", customButton.getCtaAlignment());
    }

    @Test
    void testCtaAlignmentRight() {
        setField(customButton, "ctaAlignment", "right");
        assertEquals("right", customButton.getCtaAlignment());
    }

    @Test
    void testNullValues() {
        CustomButton emptyButton = new CustomButton();
        assertNull(emptyButton.getCtaStyle());
        assertNull(emptyButton.getCtaSize());
        assertNull(emptyButton.getCtaAlignment());
    }
}
