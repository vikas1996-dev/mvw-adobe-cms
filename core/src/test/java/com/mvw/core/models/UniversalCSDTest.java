package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniversalCSDTest {

    private UniversalCSD teaser;

    @BeforeEach
    void setUp() {
        teaser = new UniversalCSD();

        // Set private fields via reflection
        setField(teaser, "headlineText", "Curious About Ownership?");
        setField(teaser, "headlineType", "heading3");
        setField(teaser, "shortDescription", "Owners enjoy access to a distinguished family experience.");
        setField(teaser, "bgImage", "/content/dam/mvw/bg.jpg");
        setField(teaser, "copySectionAlignment", "center");
        setField(teaser, "copySectionBgColor", "indigo");
        setField(teaser, "brandIconImageBgColor", "none");
        setField(teaser, "copyAlignment", "center");
    }

    // Reflection utility
    private void setField(Object target, String name, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHeadlineText() {
        assertEquals("Curious About Ownership?", teaser.getHeadlineText());
    }

    @Test
    void testHeadlineType() {
        assertEquals("heading3", teaser.getHeadlineType());
    }

    @Test
    void testShortDescription() {
        assertEquals(
                "Owners enjoy access to a distinguished family experience.",
                teaser.getShortDescription()
        );
        assertTrue(teaser.getShortDescription().contains("distinguished family"));
    }

    @Test
    void testBgImage() {
        assertEquals("/content/dam/mvw/bg.jpg", teaser.getBgImage());
    }

    @Test
    void testCopySectionAlignment() {
        assertEquals("center", teaser.getCopySectionAlignment());
    }

    @Test
    void testCopySectionBgColor() {
        assertEquals("indigo", teaser.getCopySectionBgColor());
    }

    @Test
    void testBrandIconImageBgColor() {
        assertEquals("none", teaser.getBrandIconImageBgColor());
    }

    @Test
    void testCopyAlignment() {
        assertEquals("center", teaser.getCopyAlignment());
    }

    // --------- NEW TESTS FOR 100% COVERAGE ---------

    @Test
    void testHeadlineTagWhenValidHeadingType() {
        setField(teaser, "headlineType", "heading4");
        assertEquals("h4", teaser.getHeadlineTag());
    }

    @Test
    void testHeadlineTagWhenInvalidHeadingType() {
        setField(teaser, "headlineType", "h3");
        assertEquals("h2", teaser.getHeadlineTag());
    }

    @Test
    void testHeadlineTagWhenHeadlineTypeIsNull() {
        setField(teaser, "headlineType", null);
        assertEquals("h2", teaser.getHeadlineTag());
    }
}
