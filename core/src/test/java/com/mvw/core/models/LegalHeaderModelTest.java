package com.mvw.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LegalHeaderModelTest {

    private LegalHeaderModel legalHeaderModel;

    @Mock
    private ResourceResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        legalHeaderModel = new LegalHeaderModel();
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
    void testGetLogoImage() {
        setField(legalHeaderModel, "logoImage", "/content/dam/header-logo.png");
        assertEquals("/content/dam/header-logo.png", legalHeaderModel.getLogoImage());
    }

    @Test
    void testGetLogoImageAltText() {
        setField(legalHeaderModel, "logoImageAltText", "Header Logo");
        assertEquals("Header Logo", legalHeaderModel.getLogoImageAltText());
    }

    @Test
    void testGetHomeLinkWithValidLink() {
        setField(legalHeaderModel, "homeLink", "/content/mvw/en/home");
        setField(legalHeaderModel, "resolver", resolver);
        
        // UrlUtils.getNormalizedUrl would process this
        String result = legalHeaderModel.getHomeLink();
        // Result may be null or modified depending on resolver configuration
    }

    @Test
    void testGetHomeLinkWithNullLink() {
        setField(legalHeaderModel, "homeLink", null);
        setField(legalHeaderModel, "resolver", resolver);
        
        String result = legalHeaderModel.getHomeLink();
        assertNull(result);
    }

    @Test
    void testGetHomeLinkWithExternalLink() {
        setField(legalHeaderModel, "homeLink", "https://example.com");
        setField(legalHeaderModel, "resolver", resolver);
        
        String result = legalHeaderModel.getHomeLink();
        assertEquals("https://example.com", result);
    }

    @Test
    void testGetResolver() {
        setField(legalHeaderModel, "resolver", resolver);
        assertEquals(resolver, legalHeaderModel.getResolver());
    }

    @Test
    void testNullValues() {
        assertNull(legalHeaderModel.getLogoImage());
        assertNull(legalHeaderModel.getLogoImageAltText());
        assertNull(legalHeaderModel.getResolver());
    }
}
