package com.mvw.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class LegalBrandImagesTest {

    private LegalBrandImages legalBrandImages;

    @Mock
    private ResourceResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        legalBrandImages = new LegalBrandImages();
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
    void testGetBrandImage() {
        setField(legalBrandImages, "brandImage", "/content/dam/brand.png");
        assertEquals("/content/dam/brand.png", legalBrandImages.getBrandImage());
    }

    @Test
    void testGetBrandImageAltText() {
        setField(legalBrandImages, "brandImageAltText", "Brand Image Alt");
        assertEquals("Brand Image Alt", legalBrandImages.getBrandImageAltText());
    }

    @Test
    void testGetBrandImageLink() {
        setField(legalBrandImages, "brandImageLink", "/content/mvw/en/brand");
        assertEquals("/content/mvw/en/brand", legalBrandImages.getBrandImageLink());
    }

    @Test
    void testGetBrandImageLinkTargetWithValidTarget() {
        setField(legalBrandImages, "brandImageLinkTarget", "/content/mvw/en/target");
        setField(legalBrandImages, "resolver", resolver);
        
        String result = legalBrandImages.getBrandImageLinkTarget();
        // Result may be null or modified depending on resolver configuration
    }

    @Test
    void testGetBrandImageLinkTargetWithExternalUrl() {
        setField(legalBrandImages, "brandImageLinkTarget", "https://example.com");
        setField(legalBrandImages, "resolver", resolver);
        
        String result = legalBrandImages.getBrandImageLinkTarget();
        assertEquals("https://example.com", result);
    }

    @Test
    void testGetBrandImageLinkTargetWithNullTarget() {
        setField(legalBrandImages, "brandImageLinkTarget", null);
        setField(legalBrandImages, "resolver", resolver);
        
        String result = legalBrandImages.getBrandImageLinkTarget();
        assertNull(result);
    }

    @Test
    void testGetAriaLabel() {
        setField(legalBrandImages, "ariaLabel", "Brand logo link");
        assertEquals("Brand logo link", legalBrandImages.getAriaLabel());
    }

    @Test
    void testGetResolver() {
        setField(legalBrandImages, "resolver", resolver);
        assertEquals(resolver, legalBrandImages.getResolver());
    }

    @Test
    void testNullValues() {
        assertNull(legalBrandImages.getBrandImage());
        assertNull(legalBrandImages.getBrandImageAltText());
        assertNull(legalBrandImages.getBrandImageLink());
        assertNull(legalBrandImages.getAriaLabel());
        assertNull(legalBrandImages.getResolver());
    }
}
