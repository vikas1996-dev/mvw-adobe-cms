package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThirdPartyLinkModalModelTest {

    private ThirdPartyLinkModalModel model;

    @BeforeEach
    void setUp() {
        model = new ThirdPartyLinkModalModel();
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
    void testGetModalTitle() {
        setField(model, "modalTitle", "Modal Title");
        assertEquals("Modal Title", model.getModalTitle());
    }

    @Test
    void testGetModalDescription() {
        setField(model, "modalDescription", "Modal Description");
        assertEquals("Modal Description", model.getModalDescription());
    }

    @Test
    void testGetLeftCtaText() {
        setField(model, "leftCtaText", "Left Button");
        assertEquals("Left Button", model.getLeftCtaText());
    }

    @Test
    void testGetLeftIsMainButton() {
        setField(model, "leftIsMainButton", true);
        assertTrue(model.getLeftIsMainButton());
    }

    @Test
    void testGetLeftCtaStyle() {
        setField(model, "leftCtaStyle", "secondary");
        assertEquals("secondary", model.getLeftCtaStyle());
    }

    @Test
    void testGetLeftCtaPlacement() {
        setField(model, "leftCtaPlacement", "left");
        assertEquals("left", model.getLeftCtaPlacement());
    }

    @Test
    void testGetLeftCtaSize() {
        setField(model, "leftCtaSize", "small");
        assertEquals("small", model.getLeftCtaSize());
    }

    @Test
    void testGetLeftCtaTab() {
        setField(model, "leftCtaTab", "newTab");
        assertEquals("newTab", model.getLeftCtaTab());
    }

    @Test
    void testGetRightCtaText() {
        setField(model, "rightCtaText", "Right Button");
        assertEquals("Right Button", model.getRightCtaText());
    }

    @Test
    void testGetRightIsMainButton() {
        setField(model, "rightIsMainButton", true);
        assertTrue(model.getRightIsMainButton());
    }

    @Test
    void testGetRightCtaStyle() {
        setField(model, "rightCtaStyle", "tertiary");
        assertEquals("tertiary", model.getRightCtaStyle());
    }

    @Test
    void testGetRightCtaPlacement() {
        setField(model, "rightCtaPlacement", "right");
        assertEquals("right", model.getRightCtaPlacement());
    }

    @Test
    void testGetRightCtaSize() {
        setField(model, "rightCtaSize", "medium");
        assertEquals("medium", model.getRightCtaSize());
    }

    @Test
    void testGetRightCtaTab() {
        setField(model, "rightCtaTab", "sameTab");
        assertEquals("sameTab", model.getRightCtaTab());
    }

    @Test
    void testHasLeftButtonTrue() {
        setField(model, "leftCtaText", "Button");
        assertTrue(model.hasLeftButton());
    }

    @Test
    void testHasLeftButtonFalseNull() {
        setField(model, "leftCtaText", null);
        assertFalse(model.hasLeftButton());
    }

    @Test
    void testHasLeftButtonFalseEmpty() {
        setField(model, "leftCtaText", "");
        assertFalse(model.hasLeftButton());
    }

    @Test
    void testHasLeftButtonFalseWhitespace() {
        setField(model, "leftCtaText", "   ");
        assertFalse(model.hasLeftButton());
    }

    @Test
    void testHasRightButtonTrue() {
        setField(model, "rightCtaText", "Button");
        assertTrue(model.hasRightButton());
    }

    @Test
    void testHasRightButtonFalseNull() {
        setField(model, "rightCtaText", null);
        assertFalse(model.hasRightButton());
    }

    @Test
    void testHasRightButtonFalseEmpty() {
        setField(model, "rightCtaText", "");
        assertFalse(model.hasRightButton());
    }

    @Test
    void testHasRightButtonFalseWhitespace() {
        setField(model, "rightCtaText", "   ");
        assertFalse(model.hasRightButton());
    }

    @Test
    void testHasAnyButtonTrueLeft() {
        setField(model, "leftCtaText", "Button");
        assertTrue(model.hasAnyButton());
    }

    @Test
    void testHasAnyButtonTrueRight() {
        setField(model, "rightCtaText", "Button");
        assertTrue(model.hasAnyButton());
    }

    @Test
    void testHasAnyButtonTrueBoth() {
        setField(model, "leftCtaText", "Left");
        setField(model, "rightCtaText", "Right");
        assertTrue(model.hasAnyButton());
    }

    @Test
    void testHasAnyButtonFalse() {
        assertFalse(model.hasAnyButton());
    }

    @Test
    void testInitSetsDefaultsForNullBooleans() {
        setField(model, "leftIsMainButton", null);
        setField(model, "rightIsMainButton", null);
        
        invokeInit();
        
        assertFalse(model.getLeftIsMainButton());
        assertFalse(model.getRightIsMainButton());
    }

    @Test
    void testInitKeepsExistingBooleans() {
        setField(model, "leftIsMainButton", true);
        setField(model, "rightIsMainButton", true);
        
        invokeInit();
        
        assertTrue(model.getLeftIsMainButton());
        assertTrue(model.getRightIsMainButton());
    }

    @Test
    void testInitSetsDefaultStyleWhenTextExists() {
        setField(model, "leftCtaText", "Left");
        setField(model, "rightCtaText", "Right");
        setField(model, "leftCtaStyle", null);
        setField(model, "rightCtaStyle", null);
        
        invokeInit();
        
        assertEquals("primary", model.getLeftCtaStyle());
        assertEquals("primary", model.getRightCtaStyle());
    }

    @Test
    void testInitSetsDefaultSizeWhenTextExists() {
        setField(model, "leftCtaText", "Left");
        setField(model, "rightCtaText", "Right");
        setField(model, "leftCtaSize", null);
        setField(model, "rightCtaSize", null);
        
        invokeInit();
        
        assertEquals("large", model.getLeftCtaSize());
        assertEquals("large", model.getRightCtaSize());
    }

    @Test
    void testInitSetsDefaultTabWhenTextExists() {
        setField(model, "leftCtaText", "Left");
        setField(model, "rightCtaText", "Right");
        setField(model, "leftCtaTab", null);
        setField(model, "rightCtaTab", null);
        
        invokeInit();
        
        assertEquals("sameWindow", model.getLeftCtaTab());
        assertEquals("sameWindow", model.getRightCtaTab());
    }

    @Test
    void testInitDoesNotSetDefaultsWhenNoText() {
        setField(model, "leftCtaText", null);
        setField(model, "rightCtaText", null);
        setField(model, "leftCtaStyle", null);
        setField(model, "rightCtaStyle", null);
        
        invokeInit();
        
        assertNull(model.getLeftCtaStyle());
        assertNull(model.getRightCtaStyle());
    }

    @Test
    void testNullValues() {
        assertNull(model.getModalTitle());
        assertNull(model.getModalDescription());
        assertNull(model.getLeftCtaText());
        assertNull(model.getLeftIsMainButton());
        assertNull(model.getLeftCtaStyle());
        assertNull(model.getLeftCtaPlacement());
        assertNull(model.getLeftCtaSize());
        assertNull(model.getLeftCtaTab());
        assertNull(model.getRightCtaText());
        assertNull(model.getRightIsMainButton());
        assertNull(model.getRightCtaStyle());
        assertNull(model.getRightCtaPlacement());
        assertNull(model.getRightCtaSize());
        assertNull(model.getRightCtaTab());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = ThirdPartyLinkModalModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
