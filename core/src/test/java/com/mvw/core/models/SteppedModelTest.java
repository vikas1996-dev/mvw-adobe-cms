package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SteppedModelTest {

    private SteppedModel steppedModel;

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
        steppedModel = new SteppedModel();
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
        setField(steppedModel, "headlineText", "Steps");
        assertEquals("Steps", steppedModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineType() {
        setField(steppedModel, "headlineType", "heading2");
        assertEquals("heading2", steppedModel.getHeadlineType());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(steppedModel, "headlineType", "heading2");
        assertEquals("h2", steppedModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(steppedModel, "headlineType", "heading3");
        assertEquals("h3", steppedModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(steppedModel, "headlineType", "heading1");
        assertEquals("h2", steppedModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(steppedModel, "headlineType", null);
        assertEquals("h2", steppedModel.getHeadlineTag());
    }

    @Test
    void testGetPrimaryCopy() {
        setField(steppedModel, "primaryCopy", "Primary copy text");
        assertEquals("Primary copy text", steppedModel.getPrimaryCopy());
    }

    @Test
    void testGetCopySectionAlignment() {
        setField(steppedModel, "copySectionAlignment", "center");
        assertEquals("center", steppedModel.getCopySectionAlignment());
    }

    @Test
    void testGetCopySectionBgColor() {
        setField(steppedModel, "copySectionBgColor", "white");
        assertEquals("white", steppedModel.getCopySectionBgColor());
    }

    @Test
    void testGetImage() {
        setField(steppedModel, "image", "/content/dam/image.jpg");
        assertEquals("/content/dam/image.jpg", steppedModel.getImage());
    }

    @Test
    void testGetFileName() {
        setField(steppedModel, "fileName", "file.pdf");
        assertEquals("file.pdf", steppedModel.getFileName());
    }

    @Test
    void testGetColumnsAlignment() {
        setField(steppedModel, "columnsAlignment", "left");
        assertEquals("left", steppedModel.getColumnsAlignment());
    }

    @Test
    void testGetStepsBgColor() {
        setField(steppedModel, "stepsBgColor", "gray");
        assertEquals("gray", steppedModel.getStepsBgColor());
    }

    @Test
    void testGetStackcolumns() {
        setField(steppedModel, "stackcolumns", true);
        assertTrue(steppedModel.getStackcolumns());
    }

    @Test
    void testGetHideCircles() {
        setField(steppedModel, "hideCircles", false);
        assertFalse(steppedModel.getHideCircles());
    }

    @Test
    void testGetShowDivider() {
        setField(steppedModel, "showDivider", true);
        assertTrue(steppedModel.getShowDivider());
    }

    @Test
    void testGetSecondaryCopy() {
        setField(steppedModel, "secondaryCopy", "Secondary copy text");
        assertEquals("Secondary copy text", steppedModel.getSecondaryCopy());
    }

    @Test
    void testGetMobileCopySectionAlignmnet() {
        setField(steppedModel, "mobileCopySectionAlignmnet", "center");
        assertEquals("center", steppedModel.getMobileCopySectionAlignmnet());
    }

    @Test
    void testGetMobileBgColor() {
        setField(steppedModel, "mobileBgColor", "blue");
        assertEquals("blue", steppedModel.getMobileBgColor());
    }

    @Test
    void testGetColumnAlignment() {
        setField(steppedModel, "columnAlignment", "right");
        assertEquals("right", steppedModel.getColumnAlignment());
    }

    @Test
    void testGetStepsWithNullStepList() {
        setField(steppedModel, "stepList", null);
        List<SteppedModel.StepItem> result = steppedModel.getSteps();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetStepsWithEmptyStepList() {
        setField(steppedModel, "stepList", new ArrayList<>());
        List<SteppedModel.StepItem> result = steppedModel.getSteps();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitWithNullSteps() {
        setField(steppedModel, "steps", null);
        invokeInit();
        
        List<SteppedModel.StepItem> result = steppedModel.getSteps();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitWithSteps() {
        List<Resource> stepResources = new ArrayList<>();
        
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn("step");
        when(valueMap1.get("stepTitle", String.class)).thenReturn("Step 1");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn("fa-check");
        when(valueMap1.get("headlineText", String.class)).thenReturn("Headline 1");
        when(valueMap1.get("headlineType", String.class)).thenReturn("heading3");
        when(valueMap1.get("shortDescription", String.class)).thenReturn("Description 1");
        
        stepResources.add(resource1);
        setField(steppedModel, "steps", stepResources);
        
        invokeInit();
        
        List<SteppedModel.StepItem> result = steppedModel.getSteps();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Step 1", result.get(0).getStepTitle());
        assertEquals("step", result.get(0).getTypeOfColumn());
        assertEquals("fa-check", result.get(0).getFontAwesomeIcon());
        assertEquals("Headline 1", result.get(0).getHeadlineText());
        assertEquals("heading3", result.get(0).getHeadlineType());
        assertEquals("Description 1", result.get(0).getShortDescription());
    }

    @Test
    void testInitWithMultipleSteps() {
        List<Resource> stepResources = new ArrayList<>();
        
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn("step");
        when(valueMap1.get("stepTitle", String.class)).thenReturn("Step 1");
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("headlineText", String.class)).thenReturn(null);
        when(valueMap1.get("headlineType", String.class)).thenReturn(null);
        when(valueMap1.get("shortDescription", String.class)).thenReturn(null);
        
        when(resource2.getValueMap()).thenReturn(valueMap2);
        when(valueMap2.get("typeOfColumn", String.class)).thenReturn("content");
        when(valueMap2.get("stepTitle", String.class)).thenReturn("Step 2");
        when(valueMap2.get("fontAwesomeIcon", String.class)).thenReturn("fa-star");
        when(valueMap2.get("headlineText", String.class)).thenReturn("Headline 2");
        when(valueMap2.get("headlineType", String.class)).thenReturn("heading4");
        when(valueMap2.get("shortDescription", String.class)).thenReturn("Description 2");
        
        stepResources.add(resource1);
        stepResources.add(resource2);
        setField(steppedModel, "steps", stepResources);
        
        invokeInit();
        
        List<SteppedModel.StepItem> result = steppedModel.getSteps();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testStepItemWithNullResource() {
        SteppedModel.StepItem stepItem = new SteppedModel.StepItem(null);
        assertNull(stepItem.getTypeOfColumn());
        assertNull(stepItem.getStepTitle());
        assertNull(stepItem.getFontAwesomeIcon());
        assertNull(stepItem.getHeadlineText());
        assertNull(stepItem.getHeadlineType());
        assertNull(stepItem.getShortDescription());
    }

    @Test
    void testStepItemGetHeadlineTagWithHeading3() {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn(null);
        when(valueMap1.get("stepTitle", String.class)).thenReturn(null);
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("headlineText", String.class)).thenReturn(null);
        when(valueMap1.get("headlineType", String.class)).thenReturn("heading3");
        when(valueMap1.get("shortDescription", String.class)).thenReturn(null);
        
        SteppedModel.StepItem stepItem = new SteppedModel.StepItem(resource1);
        assertEquals("h3", stepItem.getHeadlineTag());
    }

    @Test
    void testStepItemGetHeadlineTagWithHeading4() {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn(null);
        when(valueMap1.get("stepTitle", String.class)).thenReturn(null);
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("headlineText", String.class)).thenReturn(null);
        when(valueMap1.get("headlineType", String.class)).thenReturn("heading4");
        when(valueMap1.get("shortDescription", String.class)).thenReturn(null);
        
        SteppedModel.StepItem stepItem = new SteppedModel.StepItem(resource1);
        assertEquals("h4", stepItem.getHeadlineTag());
    }

    @Test
    void testStepItemGetHeadlineTagWithHeading5() {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn(null);
        when(valueMap1.get("stepTitle", String.class)).thenReturn(null);
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("headlineText", String.class)).thenReturn(null);
        when(valueMap1.get("headlineType", String.class)).thenReturn("heading5");
        when(valueMap1.get("shortDescription", String.class)).thenReturn(null);
        
        SteppedModel.StepItem stepItem = new SteppedModel.StepItem(resource1);
        assertEquals("h5", stepItem.getHeadlineTag());
    }

    @Test
    void testStepItemGetHeadlineTagWithHeading6() {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn(null);
        when(valueMap1.get("stepTitle", String.class)).thenReturn(null);
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("headlineText", String.class)).thenReturn(null);
        when(valueMap1.get("headlineType", String.class)).thenReturn("heading6");
        when(valueMap1.get("shortDescription", String.class)).thenReturn(null);
        
        SteppedModel.StepItem stepItem = new SteppedModel.StepItem(resource1);
        assertEquals("h6", stepItem.getHeadlineTag());
    }

    @Test
    void testStepItemGetHeadlineTagWithInvalidType() {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn(null);
        when(valueMap1.get("stepTitle", String.class)).thenReturn(null);
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("headlineText", String.class)).thenReturn(null);
        when(valueMap1.get("headlineType", String.class)).thenReturn("heading2");
        when(valueMap1.get("shortDescription", String.class)).thenReturn(null);
        
        SteppedModel.StepItem stepItem = new SteppedModel.StepItem(resource1);
        assertEquals("h3", stepItem.getHeadlineTag());
    }

    @Test
    void testStepItemGetHeadlineTagWithNullType() {
        when(resource1.getValueMap()).thenReturn(valueMap1);
        when(valueMap1.get("typeOfColumn", String.class)).thenReturn(null);
        when(valueMap1.get("stepTitle", String.class)).thenReturn(null);
        when(valueMap1.get("fontAwesomeIcon", String.class)).thenReturn(null);
        when(valueMap1.get("headlineText", String.class)).thenReturn(null);
        when(valueMap1.get("headlineType", String.class)).thenReturn(null);
        when(valueMap1.get("shortDescription", String.class)).thenReturn(null);
        
        SteppedModel.StepItem stepItem = new SteppedModel.StepItem(resource1);
        assertEquals("h3", stepItem.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(steppedModel.getHeadlineText());
        assertNull(steppedModel.getHeadlineType());
        assertNull(steppedModel.getPrimaryCopy());
        assertNull(steppedModel.getCopySectionAlignment());
        assertNull(steppedModel.getCopySectionBgColor());
        assertNull(steppedModel.getImage());
        assertNull(steppedModel.getFileName());
        assertNull(steppedModel.getColumnsAlignment());
        assertNull(steppedModel.getStepsBgColor());
        assertNull(steppedModel.getStackcolumns());
        assertNull(steppedModel.getHideCircles());
        assertNull(steppedModel.getShowDivider());
        assertNull(steppedModel.getSecondaryCopy());
        assertNull(steppedModel.getMobileCopySectionAlignmnet());
        assertNull(steppedModel.getMobileBgColor());
        assertNull(steppedModel.getColumnAlignment());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = SteppedModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(steppedModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
