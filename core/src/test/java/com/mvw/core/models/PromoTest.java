package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PromoTest {

    private Promo promo;

    @BeforeEach
    void setUp() {
        promo = new Promo();

        setField(promo, "promo1BgImage", "/content/dam/mvw/promo1-bg.jpg");
        setField(promo, "promo2BgImage", "/content/dam/mvw/promo2-bg.jpg");
        setField(promo, "promo1BgImageAlt", "Promo 1 Background");
        setField(promo, "promo2BgImageAlt", "Promo 2 Background");
        setField(promo, "promo1ShortDescription", "Save 20% on your next stay");
        setField(promo, "promo2ShortDescription", "Earn double points");
        setField(promo, "promo1Url", "/content/mvw/promotions/save-20");
        setField(promo, "promo2Url", "/content/mvw/promotions/double-points");
        setField(promo, "promo1Tab", "_self");
        setField(promo, "promo2Tab", "_blank");
        setField(promo, "ctaStyle1", "primary");
        setField(promo, "ctaStyle2", "secondary");
        setField(promo, "ctaAlignment1", "left");
        setField(promo, "ctaAlignment2", "center");
        setField(promo, "ctaSize1", "large");
        setField(promo, "ctaSize2", "medium");
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
    void testGetPromo1BgImage() {
        assertEquals("/content/dam/mvw/promo1-bg.jpg", promo.getPromo1BgImage());
    }

    @Test
    void testGetPromo2BgImage() {
        assertEquals("/content/dam/mvw/promo2-bg.jpg", promo.getPromo2BgImage());
    }

    @Test
    void testGetPromo1BgImageAlt() {
        assertEquals("Promo 1 Background", promo.getPromo1BgImageAlt());
    }

    @Test
    void testGetPromo2BgImageAlt() {
        assertEquals("Promo 2 Background", promo.getPromo2BgImageAlt());
    }

    @Test
    void testGetPromo1ShortDescription() {
        assertEquals("Save 20% on your next stay", promo.getPromo1ShortDescription());
    }

    @Test
    void testGetPromo2ShortDescription() {
        assertEquals("Earn double points", promo.getPromo2ShortDescription());
    }

    @Test
    void testGetPromo1Url() {
        assertEquals("/content/mvw/promotions/save-20", promo.getPromo1Url());
    }

    @Test
    void testGetPromo2Url() {
        assertEquals("/content/mvw/promotions/double-points", promo.getPromo2Url());
    }

    @Test
    void testGetPromo1Tab() {
        assertEquals("_self", promo.getPromo1Tab());
    }

    @Test
    void testGetPromo2Tab() {
        assertEquals("_blank", promo.getPromo2Tab());
    }

    @Test
    void testGetCtaStyle1() {
        assertEquals("primary", promo.getCtaStyle1());
    }

    @Test
    void testGetCtaStyle2() {
        assertEquals("secondary", promo.getCtaStyle2());
    }

    @Test
    void testGetCtaAlignment1() {
        assertEquals("left", promo.getCtaAlignment1());
    }

    @Test
    void testGetCtaAlignment2() {
        assertEquals("center", promo.getCtaAlignment2());
    }

    @Test
    void testGetCtaSize1() {
        assertEquals("large", promo.getCtaSize1());
    }

    @Test
    void testGetCtaSize2() {
        assertEquals("medium", promo.getCtaSize2());
    }

    @Test
    void testNullValues() {
        Promo emptyPromo = new Promo();
        assertNull(emptyPromo.getPromo1BgImage());
        assertNull(emptyPromo.getPromo2BgImage());
        assertNull(emptyPromo.getPromo1BgImageAlt());
        assertNull(emptyPromo.getPromo2BgImageAlt());
        assertNull(emptyPromo.getPromo1ShortDescription());
        assertNull(emptyPromo.getPromo2ShortDescription());
        assertNull(emptyPromo.getPromo1Url());
        assertNull(emptyPromo.getPromo2Url());
        assertNull(emptyPromo.getPromo1Tab());
        assertNull(emptyPromo.getPromo2Tab());
        assertNull(emptyPromo.getCtaStyle1());
        assertNull(emptyPromo.getCtaStyle2());
        assertNull(emptyPromo.getCtaAlignment1());
        assertNull(emptyPromo.getCtaAlignment2());
        assertNull(emptyPromo.getCtaSize1());
        assertNull(emptyPromo.getCtaSize2());
    }
}
