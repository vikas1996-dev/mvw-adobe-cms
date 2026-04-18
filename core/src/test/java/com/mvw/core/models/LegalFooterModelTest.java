package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LegalFooterModelTest {

    private LegalFooterModel legalFooterModel;

    @BeforeEach
    void setUp() {
        legalFooterModel = new LegalFooterModel();
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
        setField(legalFooterModel, "logoImage", "/content/dam/logo.png");
        assertEquals("/content/dam/logo.png", legalFooterModel.getLogoImage());
    }

    @Test
    void testGetLogoImageAltText() {
        setField(legalFooterModel, "logoImageAltText", "Logo Alt Text");
        assertEquals("Logo Alt Text", legalFooterModel.getLogoImageAltText());
    }

    @Test
    void testGetBrandDetails() {
        List<LegalBrandImages> brandDetails = new ArrayList<>();
        setField(legalFooterModel, "brandDetails", brandDetails);
        assertEquals(brandDetails, legalFooterModel.getBrandDetails());
    }

    @Test
    void testGetFooterText() {
        setField(legalFooterModel, "footerText", "Footer text");
        assertEquals("Footer text", legalFooterModel.getFooterText());
    }

    @Test
    void testGetLegalText() {
        setField(legalFooterModel, "legalText", "Legal text");
        assertEquals("Legal text", legalFooterModel.getLegalText());
    }

    @Test
    void testGetEqualImage() {
        setField(legalFooterModel, "equalImage", "/content/dam/equal.png");
        assertEquals("/content/dam/equal.png", legalFooterModel.getEqualImage());
    }

    @Test
    void testGetLegalLinks() {
        List<LegalLinkPojo> legalLinks = new ArrayList<>();
        setField(legalFooterModel, "legalLinks", legalLinks);
        assertEquals(legalLinks, legalFooterModel.getLegalLinks());
    }

    @Test
    void testGetLegalLinkUrl() {
        setField(legalFooterModel, "legalLinkUrl", "/legal");
        assertEquals("/legal", legalFooterModel.getLegalLinkUrl());
    }

    @Test
    void testGetLegalLinkTab() {
        setField(legalFooterModel, "legalLinkTab", "sameTab");
        assertEquals("sameTab", legalFooterModel.getLegalLinkTab());
    }

    @Test
    void testGetThirdParty() {
        setField(legalFooterModel, "thirdParty", "true");
        assertEquals("true", legalFooterModel.getThirdParty());
    }

    @Test
    void testGetIcon() {
        setField(legalFooterModel, "icon", "/content/dam/icon.png");
        assertEquals("/content/dam/icon.png", legalFooterModel.getIcon());
    }

    @Test
    void testInit() {
        // Test init with null lists
        setField(legalFooterModel, "brandDetails", null);
        setField(legalFooterModel, "legalLinks", null);
        invokeInit();
        // No exception should be thrown
    }

    @Test
    void testInitWithLists() {
        List<LegalBrandImages> brandDetails = new ArrayList<>();
        List<LegalLinkPojo> legalLinks = new ArrayList<>();
        setField(legalFooterModel, "brandDetails", brandDetails);
        setField(legalFooterModel, "legalLinks", legalLinks);
        invokeInit();
        // No exception should be thrown
    }

    @Test
    void testNullValues() {
        assertNull(legalFooterModel.getLogoImage());
        assertNull(legalFooterModel.getLogoImageAltText());
        assertNull(legalFooterModel.getBrandDetails());
        assertNull(legalFooterModel.getFooterText());
        assertNull(legalFooterModel.getLegalText());
        assertNull(legalFooterModel.getEqualImage());
        assertNull(legalFooterModel.getLegalLinks());
        assertNull(legalFooterModel.getLegalLinkUrl());
        assertNull(legalFooterModel.getLegalLinkTab());
        assertNull(legalFooterModel.getThirdParty());
        assertNull(legalFooterModel.getIcon());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = LegalFooterModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(legalFooterModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
