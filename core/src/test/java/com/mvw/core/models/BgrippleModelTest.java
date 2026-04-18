package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BgrippleModelTest {

    private BgrippleModel bgrippleModel;

    @BeforeEach
    void setUp() {
        bgrippleModel = new BgrippleModel();
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
    void testGetBgImg() {
        setField(bgrippleModel, "bgImg", "/content/dam/ripple.png");
        assertEquals("/content/dam/ripple.png", bgrippleModel.getBgImg());
    }

    @Test
    void testGetBgAlt() {
        setField(bgrippleModel, "bgAlt", "Background ripple image");
        assertEquals("Background ripple image", bgrippleModel.getBgAlt());
    }

    @Test
    void testNullValues() {
        assertNull(bgrippleModel.getBgImg());
        assertNull(bgrippleModel.getBgAlt());
    }
}
