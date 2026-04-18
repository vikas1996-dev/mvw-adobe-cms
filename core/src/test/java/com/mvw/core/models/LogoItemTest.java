package com.mvw.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogoItemTest {

    private LogoItem logoItem;
    private ResourceResolver mockResolver;

    @BeforeEach
    void setUp() {
        logoItem = new LogoItem();
        mockResolver = mock(ResourceResolver.class);

        when(mockResolver.map("/content/mvw/home.html")).thenReturn("/content/mvw/home.html");

        setField(logoItem, "logoImage", "/content/dam/mvw/logo.png");
        setField(logoItem, "logoAltText", "MVW Logo");
        setField(logoItem, "logoCtaUrl", "/content/mvw/home");
        setField(logoItem, "logoCtaTab", "_self");
        setField(logoItem, "ariaLabel", "Go to homepage");
        setField(logoItem, "resolver", mockResolver);
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
        assertEquals("/content/dam/mvw/logo.png", logoItem.getLogoImage());
    }

    @Test
    void testGetLogoAltText() {
        assertEquals("MVW Logo", logoItem.getLogoAltText());
    }

    @Test
    void testGetLogoCtaUrl() {
        assertEquals("/content/mvw/home.html", logoItem.getLogoCtaUrl());
    }

    @Test
    void testGetLogoCtaUrlWithExternalUrl() {
        setField(logoItem, "logoCtaUrl", "https://www.marriottvacations.com");
        assertEquals("https://www.marriottvacations.com", logoItem.getLogoCtaUrl());
    }

    @Test
    void testGetLogoCtaUrlWithNullResolver() {
        setField(logoItem, "resolver", null);
        assertNull(logoItem.getLogoCtaUrl());
    }

    @Test
    void testGetLogoCtaTab() {
        assertEquals("_self", logoItem.getLogoCtaTab());
    }

    @Test
    void testGetAriaLabel() {
        assertEquals("Go to homepage", logoItem.getAriaLabel());
    }

    @Test
    void testNullValues() {
        LogoItem emptyLogoItem = new LogoItem();
        assertNull(emptyLogoItem.getLogoImage());
        assertNull(emptyLogoItem.getLogoAltText());
        assertNull(emptyLogoItem.getLogoCtaUrl());
        assertNull(emptyLogoItem.getLogoCtaTab());
        assertNull(emptyLogoItem.getAriaLabel());
    }
}
