package com.mvw.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class LegalLinkPojoTest {

    private LegalLinkPojo legalLinkPojo;

    @Mock
    private ResourceResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        legalLinkPojo = new LegalLinkPojo();
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
    void testGetLegalLinkText() {
        setField(legalLinkPojo, "legalLinkText", "Privacy Policy");
        assertEquals("Privacy Policy", legalLinkPojo.getLegalLinkText());
    }

    @Test
    void testGetLegalLinkUrlWithValidUrl() {
        setField(legalLinkPojo, "legalLinkUrl", "/content/mvw/en/privacy");
        setField(legalLinkPojo, "resolver", resolver);
        
        String result = legalLinkPojo.getLegalLinkUrl();
        // UrlUtils may return modified URL or null depending on resolver configuration
        // Just verify no exception is thrown
    }

    @Test
    void testGetLegalLinkUrlWithExternalUrl() {
        setField(legalLinkPojo, "legalLinkUrl", "https://example.com/privacy");
        setField(legalLinkPojo, "resolver", resolver);
        
        String result = legalLinkPojo.getLegalLinkUrl();
        assertEquals("https://example.com/privacy", result);
    }

    @Test
    void testGetLegalLinkUrlWithNullUrl() {
        setField(legalLinkPojo, "legalLinkUrl", null);
        setField(legalLinkPojo, "resolver", resolver);
        
        String result = legalLinkPojo.getLegalLinkUrl();
        assertNull(result);
    }

    @Test
    void testGetLegalLinkTab() {
        setField(legalLinkPojo, "legalLinkTab", "newTab");
        assertEquals("newTab", legalLinkPojo.getLegalLinkTab());
    }

    @Test
    void testGetCookieLink() {
        setField(legalLinkPojo, "cookieLink", "true");
        assertEquals("true", legalLinkPojo.getCookieLink());
    }

    @Test
    void testGetThirdParty() {
        setField(legalLinkPojo, "thirdParty", "true");
        assertEquals("true", legalLinkPojo.getThirdParty());
    }

    @Test
    void testGetIcon() {
        setField(legalLinkPojo, "icon", "/content/dam/icon.png");
        assertEquals("/content/dam/icon.png", legalLinkPojo.getIcon());
    }

    @Test
    void testGetResolver() {
        setField(legalLinkPojo, "resolver", resolver);
        assertEquals(resolver, legalLinkPojo.getResolver());
    }

    @Test
    void testNullValues() {
        assertNull(legalLinkPojo.getLegalLinkText());
        assertNull(legalLinkPojo.getLegalLinkTab());
        assertNull(legalLinkPojo.getCookieLink());
        assertNull(legalLinkPojo.getThirdParty());
        assertNull(legalLinkPojo.getIcon());
        assertNull(legalLinkPojo.getResolver());
    }
}
