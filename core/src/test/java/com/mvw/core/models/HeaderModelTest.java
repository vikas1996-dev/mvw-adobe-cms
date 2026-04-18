package com.mvw.core.models;

import com.mvw.core.constants.AppConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class HeaderModelTest {

    private HeaderModel headerModel;

    @BeforeEach
    void setUp() {
        headerModel = new HeaderModel();
    }

    // ---------------- HELPER METHODS ----------------

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeInit(HeaderModel model) {
        try {
            Method method = model.getClass().getDeclaredMethod("init");
            method.setAccessible(true);
            method.invoke(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------- BASIC FIELD TESTS ----------------

    @Test
    void testGetPromo1BgImage() {
        setField(headerModel, "promo1BgImage", "/content/dam/images/promo1.jpg");
        assertEquals("/content/dam/images/promo1.jpg", headerModel.getPromo1BgImage());
    }

    @Test
    void testGetPromo2BgImage() {
        setField(headerModel, "promo2BgImage", "/content/dam/images/promo2.jpg");
        assertEquals("/content/dam/images/promo2.jpg", headerModel.getPromo2BgImage());
    }

    @Test
    void testGetLoginText() {
        setField(headerModel, "loginText", "Login");
        assertEquals("Login", headerModel.getLoginText());
    }

    @Test
    void testGetContactUsUrl() {
        ResourceResolver mockResolver = mock(ResourceResolver.class);

        when(mockResolver.map(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        setField(headerModel, "resolver", mockResolver);
        setField(headerModel, "contactUsUrl", "/content/mvw/en/contact");

        assertEquals("/content/mvw/en/contact" + AppConstants.DOT_HTML_EXTENSION,
                headerModel.getContactUsUrl());
    }

    @Test
    void testGetLogoUrl() {
        ResourceResolver mockResolver = mock(ResourceResolver.class);

        when(mockResolver.map(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        setField(headerModel, "resolver", mockResolver);
        setField(headerModel, "logoUrl", "/content/mvw/en");

        assertEquals("/content/mvw/en" + AppConstants.DOT_HTML_EXTENSION,
                headerModel.getLogoUrl());
    }

    @Test
    void testGetCtaItemsEmpty() {

        HeaderModel model = new HeaderModel();
        Resource resource = mock(Resource.class);

        setField(model, "resource", resource);

        when(resource.getChild("cta")).thenReturn(null);

        invokeInit(model);

        assertNotNull(model.getCtaItems());
        assertTrue(model.getCtaItems().isEmpty());
    }

    // ---------------- NAVIGATION ----------------

    @Test
    void testGetMainNavigationNull() {
        Resource resource = mock(Resource.class);

        setField(headerModel, "resource", resource);

        when(resource.getChild("main-navigation")).thenReturn(null);

        assertNull(headerModel.getMainNavigation());
    }

    // ---------------- NULL SAFETY ----------------

    @Test
    void testNullValues() {
        HeaderModel emptyModel = new HeaderModel();

        assertNull(emptyModel.getPromo1BgImage());
        assertNull(emptyModel.getPromo2BgImage());
        assertNull(emptyModel.getLoginText());
        assertNull(emptyModel.getLogoImage());
    }
}