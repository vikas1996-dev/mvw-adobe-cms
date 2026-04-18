package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BonvoyBlockCardModelTest {

    private BonvoyBlockCardModel bonvoyBlockCardModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bonvoyBlockCardModel = new BonvoyBlockCardModel();
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
    void testGetHeadline() {
        setField(bonvoyBlockCardModel, "headline", "Test Headline");
        assertEquals("Test Headline", bonvoyBlockCardModel.getHeadline());
    }

    @Test
    void testGetHeadlineWithNull() {
        assertNull(bonvoyBlockCardModel.getHeadline());
    }

    @Test
    void testGetHeadlineSize() {
        setField(bonvoyBlockCardModel, "headlineSize", "h4");
        assertEquals("h4", bonvoyBlockCardModel.getHeadlineSize());
    }

    @Test
    void testGetHeadlineSizeWithNull() {
        assertNull(bonvoyBlockCardModel.getHeadlineSize());
    }

    @Test
    void testGetDescription() {
        setField(bonvoyBlockCardModel, "description", "This is a test description");
        assertEquals("This is a test description", bonvoyBlockCardModel.getDescription());
    }

    @Test
    void testGetDescriptionWithNull() {
        assertNull(bonvoyBlockCardModel.getDescription());
    }

    @Test
    void testGetCopyAlignment() {
        setField(bonvoyBlockCardModel, "copyAlignment", "center");
        assertEquals("center", bonvoyBlockCardModel.getCopyAlignment());
    }

    @Test
    void testGetCopyAlignmentWithNull() {
        assertNull(bonvoyBlockCardModel.getCopyAlignment());
    }

    @Test
    void testGetMobileCopyAlignment() {
        setField(bonvoyBlockCardModel, "mobileCopyAlignment", "left");
        assertEquals("left", bonvoyBlockCardModel.getMobileCopyAlignment());
    }

    @Test
    void testGetMobileCopyAlignmentWithNull() {
        assertNull(bonvoyBlockCardModel.getMobileCopyAlignment());
    }

    @Test
    void testGetBrandLogos() {
        List<BrandItem> brandLogos = new ArrayList<>();
        BrandItem brandItem = new BrandItem();
        brandLogos.add(brandItem);
        setField(bonvoyBlockCardModel, "brandLogos", brandLogos);
        
        List<BrandItem> result = bonvoyBlockCardModel.getBrandLogos();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetBrandLogosWithNull() {
        assertNull(bonvoyBlockCardModel.getBrandLogos());
    }

    @Test
    void testGetBrandLogosWithEmptyList() {
        setField(bonvoyBlockCardModel, "brandLogos", new ArrayList<>());
        List<BrandItem> result = bonvoyBlockCardModel.getBrandLogos();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetHeadlineTagWithH2() {
        setField(bonvoyBlockCardModel, "headlineSize", "h2");
        assertEquals("h2", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH3() {
        setField(bonvoyBlockCardModel, "headlineSize", "h3");
        assertEquals("h3", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH4() {
        setField(bonvoyBlockCardModel, "headlineSize", "h4");
        assertEquals("h4", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH5() {
        setField(bonvoyBlockCardModel, "headlineSize", "h5");
        assertEquals("h5", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH6() {
        setField(bonvoyBlockCardModel, "headlineSize", "h6");
        assertEquals("h6", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNull() {
        setField(bonvoyBlockCardModel, "headlineSize", null);
        assertEquals("h3", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidValue() {
        setField(bonvoyBlockCardModel, "headlineSize", "paragraph");
        assertEquals("h3", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH1() {
        setField(bonvoyBlockCardModel, "headlineSize", "h1");
        assertEquals("h3", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithH7() {
        setField(bonvoyBlockCardModel, "headlineSize", "h7");
        assertEquals("h3", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithEmptyString() {
        setField(bonvoyBlockCardModel, "headlineSize", "");
        assertEquals("h3", bonvoyBlockCardModel.getHeadlineTag());
    }

    @Test
    void testAllFieldsPopulated() {
        setField(bonvoyBlockCardModel, "headline", "Card Headline");
        setField(bonvoyBlockCardModel, "headlineSize", "h4");
        setField(bonvoyBlockCardModel, "description", "Card Description");
        setField(bonvoyBlockCardModel, "copyAlignment", "center");
        setField(bonvoyBlockCardModel, "mobileCopyAlignment", "left");
        
        List<BrandItem> brandLogos = new ArrayList<>();
        brandLogos.add(new BrandItem());
        brandLogos.add(new BrandItem());
        setField(bonvoyBlockCardModel, "brandLogos", brandLogos);
        
        assertEquals("Card Headline", bonvoyBlockCardModel.getHeadline());
        assertEquals("h4", bonvoyBlockCardModel.getHeadlineSize());
        assertEquals("Card Description", bonvoyBlockCardModel.getDescription());
        assertEquals("center", bonvoyBlockCardModel.getCopyAlignment());
        assertEquals("left", bonvoyBlockCardModel.getMobileCopyAlignment());
        assertEquals("h4", bonvoyBlockCardModel.getHeadlineTag());
        assertEquals(2, bonvoyBlockCardModel.getBrandLogos().size());
    }

    @Test
    void testNullValues() {
        assertNull(bonvoyBlockCardModel.getHeadline());
        assertNull(bonvoyBlockCardModel.getHeadlineSize());
        assertNull(bonvoyBlockCardModel.getDescription());
        assertNull(bonvoyBlockCardModel.getCopyAlignment());
        assertNull(bonvoyBlockCardModel.getMobileCopyAlignment());
        assertNull(bonvoyBlockCardModel.getBrandLogos());
    }
}
