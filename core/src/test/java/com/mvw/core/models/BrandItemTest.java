package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class BrandItemTest {

    private BrandItem brandItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        brandItem = new BrandItem();
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
    void testGetBrandLogoImage() {
        setField(brandItem, "brandLogoImage", "/content/dam/brands/marriott.png");
        assertEquals("/content/dam/brands/marriott.png", brandItem.getBrandLogoImage());
    }

    @Test
    void testGetBrandLogoImageWithNull() {
        assertNull(brandItem.getBrandLogoImage());
    }

    @Test
    void testGetBrandLogoImageWithEmptyString() {
        setField(brandItem, "brandLogoImage", "");
        assertEquals("", brandItem.getBrandLogoImage());
    }

    @Test
    void testGetBrandLogoAltText() {
        setField(brandItem, "brandLogoAltText", "Marriott Logo");
        assertEquals("Marriott Logo", brandItem.getBrandLogoAltText());
    }

    @Test
    void testGetBrandLogoAltTextWithNull() {
        assertNull(brandItem.getBrandLogoAltText());
    }

    @Test
    void testGetBrandLogoAltTextWithEmptyString() {
        setField(brandItem, "brandLogoAltText", "");
        assertEquals("", brandItem.getBrandLogoAltText());
    }

    @Test
    void testAllFieldsPopulated() {
        setField(brandItem, "brandLogoImage", "/content/dam/brands/westin.png");
        setField(brandItem, "brandLogoAltText", "Westin Logo");
        
        assertEquals("/content/dam/brands/westin.png", brandItem.getBrandLogoImage());
        assertEquals("Westin Logo", brandItem.getBrandLogoAltText());
    }

    @Test
    void testNullValues() {
        assertNull(brandItem.getBrandLogoImage());
        assertNull(brandItem.getBrandLogoAltText());
    }

    @Test
    void testBrandLogoImageWithSpecialCharacters() {
        setField(brandItem, "brandLogoImage", "/content/dam/brands/w-hotels_logo@2x.png");
        assertEquals("/content/dam/brands/w-hotels_logo@2x.png", brandItem.getBrandLogoImage());
    }

    @Test
    void testBrandLogoAltTextWithSpecialCharacters() {
        setField(brandItem, "brandLogoAltText", "W Hotels & Resorts Logo");
        assertEquals("W Hotels & Resorts Logo", brandItem.getBrandLogoAltText());
    }

    @Test
    void testBrandLogoImageWithLongPath() {
        String longPath = "/content/dam/mvw/brands/luxury/collection/images/logos/brand-logo-large-resolution.png";
        setField(brandItem, "brandLogoImage", longPath);
        assertEquals(longPath, brandItem.getBrandLogoImage());
    }

    @Test
    void testBrandLogoAltTextWithLongText() {
        String longAltText = "This is a very long alt text that describes the brand logo in great detail for accessibility purposes";
        setField(brandItem, "brandLogoAltText", longAltText);
        assertEquals(longAltText, brandItem.getBrandLogoAltText());
    }
}
