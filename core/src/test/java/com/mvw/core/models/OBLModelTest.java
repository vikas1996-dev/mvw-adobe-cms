package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OBLModelTest {

    private OBLModel oblModel;

    @Mock
    private Resource offerBadgeResource;

    @Mock
    private OBLModel.OfferBadge mockOfferBadge;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        oblModel = new OBLModel();
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
    void testGetHeadlineText() {
        setField(oblModel, "headlineText", "Special Offers");
        assertEquals("Special Offers", oblModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineTextNull() {
        setField(oblModel, "headlineText", null);
        assertNull(oblModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineAlignmentWithValue() {
        setField(oblModel, "headlineAlignment", "left");
        assertEquals("left", oblModel.getHeadlineAlignment());
    }

    @Test
    void testGetHeadlineAlignmentWithNullValue() {
        setField(oblModel, "headlineAlignment", null);
        assertEquals("center", oblModel.getHeadlineAlignment());
    }

    @Test
    void testGetHeadlineAlignmentWithEmptyValue() {
        setField(oblModel, "headlineAlignment", "");
        assertEquals("center", oblModel.getHeadlineAlignment());
    }

    @Test
    void testGetHeadlineAlignmentWithWhitespace() {
        setField(oblModel, "headlineAlignment", "   ");
        assertEquals("center", oblModel.getHeadlineAlignment());
    }

    @Test
    void testGetDescription() {
        setField(oblModel, "description", "Description text");
        assertEquals("Description text", oblModel.getDescription());
    }

    @Test
    void testGetDescriptionNull() {
        setField(oblModel, "description", null);
        assertNull(oblModel.getDescription());
    }

    @Test
    void testGetDescriptionAlignmentWithValue() {
        setField(oblModel, "descriptionAlignment", "right");
        assertEquals("right", oblModel.getDescriptionAlignment());
    }

    @Test
    void testGetDescriptionAlignmentWithNullValue() {
        setField(oblModel, "descriptionAlignment", null);
        assertEquals("center", oblModel.getDescriptionAlignment());
    }

    @Test
    void testGetDescriptionAlignmentWithEmptyValue() {
        setField(oblModel, "descriptionAlignment", "");
        assertEquals("center", oblModel.getDescriptionAlignment());
    }

    @Test
    void testGetDescriptionAlignmentWithWhitespace() {
        setField(oblModel, "descriptionAlignment", "   ");
        assertEquals("center", oblModel.getDescriptionAlignment());
    }

    @Test
    void testGetMobileHeadlineAlignmentWithValue() {
        setField(oblModel, "mobileHeadlineAlignment", "left");
        assertEquals("left", oblModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileHeadlineAlignmentWithNullValue() {
        setField(oblModel, "mobileHeadlineAlignment", null);
        assertEquals("center", oblModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileHeadlineAlignmentWithEmptyValue() {
        setField(oblModel, "mobileHeadlineAlignment", "");
        assertEquals("center", oblModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileHeadlineAlignmentWithWhitespace() {
        setField(oblModel, "mobileHeadlineAlignment", "   ");
        assertEquals("center", oblModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignmentWithValue() {
        setField(oblModel, "mobileDescriptionAlignment", "right");
        assertEquals("right", oblModel.getMobileDescriptionAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignmentWithNullValue() {
        setField(oblModel, "mobileDescriptionAlignment", null);
        assertEquals("center", oblModel.getMobileDescriptionAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignmentWithEmptyValue() {
        setField(oblModel, "mobileDescriptionAlignment", "");
        assertEquals("center", oblModel.getMobileDescriptionAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignmentWithWhitespace() {
        setField(oblModel, "mobileDescriptionAlignment", "   ");
        assertEquals("center", oblModel.getMobileDescriptionAlignment());
    }

    @Test
    void testGetHeadlineClassWithHeading2() {
        setField(oblModel, "headlineSize", "heading2");
        assertEquals("heading2", oblModel.getHeadlineClass());
    }

    @Test
    void testGetHeadlineClassWithHeading3() {
        setField(oblModel, "headlineSize", "heading3");
        assertEquals("heading3", oblModel.getHeadlineClass());
    }

    @Test
    void testGetHeadlineClassWithHeading4() {
        setField(oblModel, "headlineSize", "heading4");
        assertEquals("heading4", oblModel.getHeadlineClass());
    }

    @Test
    void testGetHeadlineClassWithInvalidValue() {
        setField(oblModel, "headlineSize", "heading1");
        assertEquals("heading2", oblModel.getHeadlineClass());
    }

    @Test
    void testGetHeadlineClassWithNullValue() {
        setField(oblModel, "headlineSize", null);
        assertEquals("heading2", oblModel.getHeadlineClass());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(oblModel, "headlineSize", "heading2");
        assertEquals("h2", oblModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(oblModel, "headlineSize", "heading3");
        assertEquals("h3", oblModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(oblModel, "headlineSize", "heading4");
        assertEquals("h4", oblModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidValue() {
        setField(oblModel, "headlineSize", "invalid");
        assertEquals("h2", oblModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullValue() {
        setField(oblModel, "headlineSize", null);
        assertEquals("h2", oblModel.getHeadlineTag());
    }

    @Test
    void testGetOfferBadgeListWithNullList() {
        setField(oblModel, "offerBadgeList", null);
        List<OBLModel.OfferBadge> result = oblModel.getOfferBadgeList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOfferBadgeListWithEmptyList() {
        setField(oblModel, "offerBadgeList", Collections.emptyList());
        List<OBLModel.OfferBadge> result = oblModel.getOfferBadgeList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOfferBadgeListWithValidResources() {
        when(offerBadgeResource.adaptTo(OBLModel.OfferBadge.class)).thenReturn(mockOfferBadge);
        setField(oblModel, "offerBadgeList", Collections.singletonList(offerBadgeResource));
        
        List<OBLModel.OfferBadge> result = oblModel.getOfferBadgeList();
        assertEquals(1, result.size());
    }

    @Test
    void testGetOfferBadgeListWithNullAdaption() {
        when(offerBadgeResource.adaptTo(OBLModel.OfferBadge.class)).thenReturn(null);
        setField(oblModel, "offerBadgeList", Collections.singletonList(offerBadgeResource));
        
        List<OBLModel.OfferBadge> result = oblModel.getOfferBadgeList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testOfferBadgeGetOblColor() {
        OBLModel.OfferBadge badge = new OBLModel.OfferBadge();
        setFieldOnInnerClass(badge, "oblColor", "gold");
        assertEquals("gold", badge.getOblColor());
    }

    @Test
    void testOfferBadgeGetOblColorNull() {
        OBLModel.OfferBadge badge = new OBLModel.OfferBadge();
        assertNull(badge.getOblColor());
    }

    @Test
    void testOfferBadgeGetOblLabel() {
        OBLModel.OfferBadge badge = new OBLModel.OfferBadge();
        setFieldOnInnerClass(badge, "oblLabel", "Best Value");
        assertEquals("Best Value", badge.getOblLabel());
    }

    @Test
    void testOfferBadgeGetOblLabelNull() {
        OBLModel.OfferBadge badge = new OBLModel.OfferBadge();
        assertNull(badge.getOblLabel());
    }

    @Test
    void testOfferBadgeGetOblPointsRange() {
        OBLModel.OfferBadge badge = new OBLModel.OfferBadge();
        setFieldOnInnerClass(badge, "oblPointsRange", "5000-10000");
        assertEquals("5000-10000", badge.getOblPointsRange());
    }

    @Test
    void testOfferBadgeGetOblPointsRangeNull() {
        OBLModel.OfferBadge badge = new OBLModel.OfferBadge();
        assertNull(badge.getOblPointsRange());
    }

    private void setFieldOnInnerClass(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
