package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LegalErrorPageImagesTest {

    private LegalErrorPageImages legalErrorPageImages;

    @BeforeEach
    void setUp() {
        legalErrorPageImages = new LegalErrorPageImages();
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
    void testGetMainImageFileReference() {
        setField(legalErrorPageImages, "mainImageFileReference", "/content/dam/mvw/error/main-image.jpg");
        assertEquals("/content/dam/mvw/error/main-image.jpg", legalErrorPageImages.getMainImageFileReference());
    }

    @Test
    void testGetMainImageAltText() {
        setField(legalErrorPageImages, "mainImageAltText", "Error page main image");
        assertEquals("Error page main image", legalErrorPageImages.getMainImageAltText());
    }

    @Test
    void testGetOverlayImagePath() {
        setField(legalErrorPageImages, "overlayImagePath", "/content/dam/mvw/error/overlay.png");
        assertEquals("/content/dam/mvw/error/overlay.png", legalErrorPageImages.getOverlayImagePath());
    }

    @Test
    void testGetOverlayImageAltText() {
        setField(legalErrorPageImages, "overlayImageAltText", "/content/dam/mvw/error/mobile-overlay.png");
        assertEquals("/content/dam/mvw/error/mobile-overlay.png", legalErrorPageImages.getOverlayImageAltText());
    }

    @Test
    void testAllFieldsWithValidValues() {
        setField(legalErrorPageImages, "mainImageFileReference", "/content/dam/error-main.jpg");
        setField(legalErrorPageImages, "mainImageAltText", "Main error image");
        setField(legalErrorPageImages, "overlayImagePath", "/content/dam/error-overlay.png");
        setField(legalErrorPageImages, "overlayImageAltText", "/content/dam/error-mb-overlay.png");

        assertEquals("/content/dam/error-main.jpg", legalErrorPageImages.getMainImageFileReference());
        assertEquals("Main error image", legalErrorPageImages.getMainImageAltText());
        assertEquals("/content/dam/error-overlay.png", legalErrorPageImages.getOverlayImagePath());
        assertEquals("/content/dam/error-mb-overlay.png", legalErrorPageImages.getOverlayImageAltText());
    }

    @Test
    void testNullValues() {
        LegalErrorPageImages emptyModel = new LegalErrorPageImages();
        assertNull(emptyModel.getMainImageFileReference());
        assertNull(emptyModel.getMainImageAltText());
        assertNull(emptyModel.getOverlayImagePath());
        assertNull(emptyModel.getOverlayImageAltText());
    }

    @Test
    void testMainImageWithDamPath() {
        setField(legalErrorPageImages, "mainImageFileReference", "/content/dam/tmvcs/legal/404-background.jpg");
        assertTrue(legalErrorPageImages.getMainImageFileReference().startsWith("/content/dam/"));
    }

    @Test
    void testOverlayWithPngFormat() {
        setField(legalErrorPageImages, "overlayImagePath", "/content/dam/tmvcs/legal/overlay.png");
        assertTrue(legalErrorPageImages.getOverlayImagePath().endsWith(".png"));
    }
}
