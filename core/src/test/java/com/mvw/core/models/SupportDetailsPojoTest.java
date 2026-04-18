package com.mvw.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SupportDetailsPojoTest {

    private SupportDetailsPojo supportDetailsPojo;
    private ResourceResolver mockResolver;

    @BeforeEach
    void setUp() {
        supportDetailsPojo = new SupportDetailsPojo();
        mockResolver = mock(ResourceResolver.class);

        when(mockResolver.map("/content/mvw/support.html")).thenReturn("/content/mvw/support.html");

        setField(supportDetailsPojo, "iconClass", "fa-phone");
        setField(supportDetailsPojo, "label", "Call Us");
        setField(supportDetailsPojo, "linkText", "Contact Support");
        setField(supportDetailsPojo, "buttonLink", "/content/mvw/support");
        setField(supportDetailsPojo, "resolver", mockResolver);
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
    void testGetIconClass() {
        assertEquals("fa-phone", supportDetailsPojo.getIconClass());
    }

    @Test
    void testGetLabel() {
        assertEquals("Call Us", supportDetailsPojo.getLabel());
    }

    @Test
    void testGetLinkText() {
        assertEquals("Contact Support", supportDetailsPojo.getLinkText());
    }

    @Test
    void testGetButtonLink() {
        assertEquals("/content/mvw/support.html", supportDetailsPojo.getButtonLink());
    }

    @Test
    void testGetButtonLinkWithExternalUrl() {
        setField(supportDetailsPojo, "buttonLink", "https://external.com/support");
        assertEquals("https://external.com/support", supportDetailsPojo.getButtonLink());
    }

    @Test
    void testGetButtonLinkWithNullResolver() {
        setField(supportDetailsPojo, "resolver", null);
        assertNull(supportDetailsPojo.getButtonLink());
    }

    @Test
    void testNullValues() {
        SupportDetailsPojo emptyPojo = new SupportDetailsPojo();
        assertNull(emptyPojo.getIconClass());
        assertNull(emptyPojo.getLabel());
        assertNull(emptyPojo.getLinkText());
        assertNull(emptyPojo.getButtonLink());
    }
}
