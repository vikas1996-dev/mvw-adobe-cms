package com.mvw.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CopyBlockModelTest {

    private CopyBlockModel copyBlockModel;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private Resource resource1;

    @Mock
    private Resource resource2;

    @Mock
    private ValueMap valueMap1;

    @Mock
    private ValueMap valueMap2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        copyBlockModel = new CopyBlockModel();
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
    void testGetCopySectionBgColor() {
        setField(copyBlockModel, "copySectionBgColor", "blue");
        assertEquals("blue", copyBlockModel.getCopySectionBgColor());
    }

    @Test
    void testGetCopyAlignment() {
        setField(copyBlockModel, "copyAlignment", "left");
        assertEquals("left", copyBlockModel.getCopyAlignment());
    }

    @Test
    void testGetMobileCopyAlignment() {
        setField(copyBlockModel, "mobileCopyAlignment", "center");
        assertEquals("center", copyBlockModel.getMobileCopyAlignment());
    }

    @Test
    void testGetCopyText() {
        setField(copyBlockModel, "copyText", "Sample copy text");
        assertEquals("Sample copy text", copyBlockModel.getCopyText());
    }

    @Test
    void testGetCopyTextWithPhoneNumberPlaceholder() {
        setField(copyBlockModel, "copyText", "Call us at {{phoneNumber}}");
        setField(copyBlockModel, "request", request);
        when(request.getAttribute("phoneNumber")).thenReturn("1-800-555-1234");
        
        String result = copyBlockModel.getCopyText();
        assertEquals("Call us at 1-800-555-1234", result);
    }

    @Test
    void testGetCopyTextWithPlaceholderButNullPhoneNumber() {
        setField(copyBlockModel, "copyText", "Call us at {{phoneNumber}}");
        setField(copyBlockModel, "request", request);
        when(request.getAttribute("phoneNumber")).thenReturn(null);
        
        String result = copyBlockModel.getCopyText();
        assertEquals("Call us at {{phoneNumber}}", result);
    }

    @Test
    void testGetCopyTextWithNullRequest() {
        setField(copyBlockModel, "copyText", "Call us at {{phoneNumber}}");
        setField(copyBlockModel, "request", null);
        
        String result = copyBlockModel.getCopyText();
        assertEquals("Call us at {{phoneNumber}}", result);
    }

    @Test
    void testGetCopyTextNull() {
        setField(copyBlockModel, "copyText", null);
        assertNull(copyBlockModel.getCopyText());
    }

    @Test
    void testGetCtasNull() {
        setField(copyBlockModel, "ctaList", null);
        List<CopyBlockModel.CtaItem> result = copyBlockModel.getCtas();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCtasEmpty() {
        setField(copyBlockModel, "ctaList", new ArrayList<>());
        List<CopyBlockModel.CtaItem> result = copyBlockModel.getCtas();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitWithNullCtas() {
        setField(copyBlockModel, "ctas", null);
        invokeInit();
        
        List<CopyBlockModel.CtaItem> result = copyBlockModel.getCtas();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitWithCtas() {
        List<Resource> ctaResources = new ArrayList<>();
        ResourceResolver resolver = mock(ResourceResolver.class);
        when(resource1.getResourceResolver()).thenReturn(resolver);

        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("Button 1");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn("/page1");
        when(valueMap1.get("phoneNumber", String.class)).thenReturn("123-456-7890");
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("newTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("primary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("left");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn("fa-phone");
        when(valueMap1.get("appendPhoneNumber", String.class)).thenReturn("true");

        ctaResources.add(resource1);
        setField(copyBlockModel, "ctas", ctaResources);

        invokeInit();

        List<CopyBlockModel.CtaItem> result = copyBlockModel.getCtas();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Button 1", result.get(0).getCtaText());
        assertEquals("/page1", result.get(0).getCtaDestination());
        assertEquals("newTab", result.get(0).getCtaTab());
        assertEquals("primary", result.get(0).getCtaStyle());
        assertEquals("left", result.get(0).getCtaPlacement());
        assertEquals("fa-phone", result.get(0).getFontAwesomeIcon());
        assertEquals("true", result.get(0).getAppendPhoneNumber());
    }

    @Test
    void testInitWithMultipleCtas() {
        List<Resource> ctaResources = new ArrayList<>();
        
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("Button 1");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn("/page1");
        when(valueMap1.get("phoneNumber", String.class)).thenReturn(null);
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("sameTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("tertiary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("none");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("appendPhoneNumber", String.class)).thenReturn(null);
        
        when(resource2.getValueMap()).thenReturn(valueMap2);
        when(valueMap2.get("ctaText", String.class)).thenReturn("Button 2");
        when(valueMap2.get("ctaDestination", String.class)).thenReturn("/page2");
        when(valueMap2.get("phoneNumber", String.class)).thenReturn("987-654-3210");
        when(valueMap2.get("ctaTab", "sameTab")).thenReturn("newTab");
        when(valueMap2.get("ctaStyle", "tertiary")).thenReturn("secondary");
        when(valueMap2.get("ctaPlacement", "none")).thenReturn("right");
        when(valueMap2.get("fontAwesomeIcon", String.class)).thenReturn("fa-arrow");
        when(valueMap2.get("appendPhoneNumber", String.class)).thenReturn("false");
        
        ctaResources.add(resource1);
        ctaResources.add(resource2);
        setField(copyBlockModel, "ctas", ctaResources);
        
        invokeInit();
        
        List<CopyBlockModel.CtaItem> result = copyBlockModel.getCtas();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Button 1", result.get(0).getCtaText());
        assertEquals("Button 2", result.get(1).getCtaText());
    }

    @Test
    void testCtaItemWithNullResource() {
        CopyBlockModel.CtaItem ctaItem = new CopyBlockModel.CtaItem(null);
        assertNull(ctaItem.getCtaText());
        assertNull(ctaItem.getCtaDestination());
        assertNull(ctaItem.getCtaTab());
        assertNull(ctaItem.getCtaStyle());
        assertNull(ctaItem.getCtaPlacement());
        assertNull(ctaItem.getFontAwesomeIcon());
        assertNull(ctaItem.getAppendPhoneNumber());
    }

    @Test
    void testCtaItemGetPhoneNumberFromRequest() {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("Call");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn(null);
        when(valueMap1.get("phoneNumber", String.class)).thenReturn("local-phone");
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("sameTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("tertiary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("none");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("appendPhoneNumber", String.class)).thenReturn(null);
        
        CopyBlockModel.CtaItem ctaItem = new CopyBlockModel.CtaItem(resource1);
        
        // Without request set, should return local phone
        assertEquals("local-phone", ctaItem.getPhoneNumber());
    }

    @Test
    void testCtaItemGetPhoneNumberWithRequestAttribute() throws Exception {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("Call");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn(null);
        when(valueMap1.get("phoneNumber", String.class)).thenReturn("local-phone");
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("sameTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("tertiary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("none");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("appendPhoneNumber", String.class)).thenReturn(null);
        
        CopyBlockModel.CtaItem ctaItem = new CopyBlockModel.CtaItem(resource1);
        
        // Inject request via reflection
        java.lang.reflect.Field requestField = CopyBlockModel.CtaItem.class.getDeclaredField("request");
        requestField.setAccessible(true);
        requestField.set(ctaItem, request);
        
        when(request.getAttribute("phoneNumber")).thenReturn("request-phone");
        
        assertEquals("request-phone", ctaItem.getPhoneNumber());
    }

    @Test
    void testCtaItemGetPhoneNumberWithNullRequestAttribute() throws Exception {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("ctaText", String.class)).thenReturn("Call");
        when(valueMap1.get("ctaDestination", String.class)).thenReturn(null);
        when(valueMap1.get("phoneNumber", String.class)).thenReturn("local-phone");
        when(valueMap1.get("ctaTab", "sameTab")).thenReturn("sameTab");
        when(valueMap1.get("ctaStyle", "tertiary")).thenReturn("tertiary");
        when(valueMap1.get("ctaPlacement", "none")).thenReturn("none");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("appendPhoneNumber", String.class)).thenReturn(null);
        
        CopyBlockModel.CtaItem ctaItem = new CopyBlockModel.CtaItem(resource1);
        
        // Inject request via reflection
        java.lang.reflect.Field requestField = CopyBlockModel.CtaItem.class.getDeclaredField("request");
        requestField.setAccessible(true);
        requestField.set(ctaItem, request);
        
        when(request.getAttribute("phoneNumber")).thenReturn(null);
        
        assertEquals("local-phone", ctaItem.getPhoneNumber());
    }

    @Test
    void testNullValues() {
        assertNull(copyBlockModel.getCopySectionBgColor());
        assertNull(copyBlockModel.getCopyAlignment());
        assertNull(copyBlockModel.getMobileCopyAlignment());
        assertNull(copyBlockModel.getCopyText());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = CopyBlockModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(copyBlockModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
