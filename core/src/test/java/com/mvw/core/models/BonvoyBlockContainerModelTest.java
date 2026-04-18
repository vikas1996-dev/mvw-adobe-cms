package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class BonvoyBlockContainerModelTest {

    private BonvoyBlockContainerModel bonvoyBlockContainerModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bonvoyBlockContainerModel = new BonvoyBlockContainerModel();
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
    void testGetLogo() {
        setField(bonvoyBlockContainerModel, "logo", "/content/dam/logo.png");
        assertEquals("/content/dam/logo.png", bonvoyBlockContainerModel.getLogo());
    }

    @Test
    void testGetLogoWithNull() {
        assertNull(bonvoyBlockContainerModel.getLogo());
    }

    @Test
    void testGetLogoAltText() {
        setField(bonvoyBlockContainerModel, "logoAltText", "Brand Logo");
        assertEquals("Brand Logo", bonvoyBlockContainerModel.getLogoAltText());
    }

    @Test
    void testGetLogoAltTextWithNull() {
        assertNull(bonvoyBlockContainerModel.getLogoAltText());
    }

    @Test
    void testGetHeadline() {
        setField(bonvoyBlockContainerModel, "headline", "Container Headline");
        assertEquals("Container Headline", bonvoyBlockContainerModel.getHeadline());
    }

    @Test
    void testGetHeadlineWithNull() {
        assertNull(bonvoyBlockContainerModel.getHeadline());
    }

    @Test
    void testGetHeadlineSize() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h3");
        assertEquals("h3", bonvoyBlockContainerModel.getHeadlineSize());
    }

    @Test
    void testGetHeadlineSizeWithNull() {
        assertNull(bonvoyBlockContainerModel.getHeadlineSize());
    }

    @Test
    void testGetCopyAlignment() {
        setField(bonvoyBlockContainerModel, "copyAlignment", "center");
        assertEquals("center", bonvoyBlockContainerModel.getCopyAlignment());
    }

    @Test
    void testGetCopyAlignmentWithNull() {
        assertNull(bonvoyBlockContainerModel.getCopyAlignment());
    }

    @Test
    void testGetMobileCopyAlignment() {
        setField(bonvoyBlockContainerModel, "mobileCopyAlignment", "right");
        assertEquals("right", bonvoyBlockContainerModel.getMobileCopyAlignment());
    }

    @Test
    void testGetMobileCopyAlignmentWithNull() {
        assertNull(bonvoyBlockContainerModel.getMobileCopyAlignment());
    }

    @Test
    void testGetHeadlineTagWithH2() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h2");
        assertEquals("h2", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH3() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h3");
        assertEquals("h3", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH4() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h4");
        assertEquals("h4", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH5() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h5");
        assertEquals("h5", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH6() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h6");
        assertEquals("h6", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNull() {
        setField(bonvoyBlockContainerModel, "headlineSize", null);
        assertEquals("h2", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidValue() {
        setField(bonvoyBlockContainerModel, "headlineSize", "span");
        assertEquals("h2", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH1() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h1");
        assertEquals("h2", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH7() {
        setField(bonvoyBlockContainerModel, "headlineSize", "h7");
        assertEquals("h2", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithEmptyString() {
        setField(bonvoyBlockContainerModel, "headlineSize", "");
        assertEquals("h2", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testAllFieldsPopulated() {
        setField(bonvoyBlockContainerModel, "logo", "/content/dam/bonvoy-logo.png");
        setField(bonvoyBlockContainerModel, "logoAltText", "Bonvoy Logo");
        setField(bonvoyBlockContainerModel, "headline", "Welcome to Bonvoy");
        setField(bonvoyBlockContainerModel, "headlineSize", "h3");
        setField(bonvoyBlockContainerModel, "copyAlignment", "left");
        setField(bonvoyBlockContainerModel, "mobileCopyAlignment", "center");
        
        assertEquals("/content/dam/bonvoy-logo.png", bonvoyBlockContainerModel.getLogo());
        assertEquals("Bonvoy Logo", bonvoyBlockContainerModel.getLogoAltText());
        assertEquals("Welcome to Bonvoy", bonvoyBlockContainerModel.getHeadline());
        assertEquals("h3", bonvoyBlockContainerModel.getHeadlineSize());
        assertEquals("left", bonvoyBlockContainerModel.getCopyAlignment());
        assertEquals("center", bonvoyBlockContainerModel.getMobileCopyAlignment());
        assertEquals("h3", bonvoyBlockContainerModel.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(bonvoyBlockContainerModel.getLogo());
        assertNull(bonvoyBlockContainerModel.getLogoAltText());
        assertNull(bonvoyBlockContainerModel.getHeadline());
        assertNull(bonvoyBlockContainerModel.getHeadlineSize());
        assertNull(bonvoyBlockContainerModel.getCopyAlignment());
        assertNull(bonvoyBlockContainerModel.getMobileCopyAlignment());
    }

    @Test
    void testDefaultHeadlineTagValue() {
        assertEquals("h2", bonvoyBlockContainerModel.getHeadlineTag());
    }
}
