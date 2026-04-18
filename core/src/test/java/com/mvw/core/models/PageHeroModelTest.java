package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PageHeroModelTest {

    private PageHeroModel pageHeroModel;

    @BeforeEach
    void setUp() {
        pageHeroModel = new PageHeroModel();
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
    void testGetBackgroundImage() {
        setField(pageHeroModel, "backgroundImage", "/content/dam/hero-bg.jpg");
        assertEquals("/content/dam/hero-bg.jpg", pageHeroModel.getBackgroundImage());
    }

    @Test
    void testGetBgPositionLeftAndRight() {
        setField(pageHeroModel, "bgPositionLeftAndRight", "center");
        assertEquals("center", pageHeroModel.getBgPositionLeftAndRight());
    }

    @Test
    void testGetBgPositionTopAndBottom() {
        setField(pageHeroModel, "bgPositionTopAndBottom", "top");
        assertEquals("top", pageHeroModel.getBgPositionTopAndBottom());
    }

    @Test
    void testGetHeadlineText() {
        setField(pageHeroModel, "headlineText", "Welcome Hero");
        assertEquals("Welcome Hero", pageHeroModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineAlignment() {
        setField(pageHeroModel, "headlineAlignment", "left");
        assertEquals("left", pageHeroModel.getHeadlineAlignment());
    }

    @Test
    void testGetShortDescription() {
        setField(pageHeroModel, "shortDescription", "A short description text");
        assertEquals("A short description text", pageHeroModel.getShortDescription());
    }

    @Test
    void testGetCopySectionAlignment() {
        setField(pageHeroModel, "copySectionAlignment", "center");
        assertEquals("center", pageHeroModel.getCopySectionAlignment());
    }

    @Test
    void testGetCopySectionBgColor() {
        setField(pageHeroModel, "copySectionBgColor", "white");
        assertEquals("white", pageHeroModel.getCopySectionBgColor());
    }

    @Test
    void testGetCopySectionBgBorderControl() {
        setField(pageHeroModel, "copySectionBgBorderControl", "true");
        assertEquals("true", pageHeroModel.getCopySectionBgBorderControl());
    }

    @Test
    void testGetBackgroundTransControl() {
        setField(pageHeroModel, "backgroundTransControl", "50");
        assertEquals("50", pageHeroModel.getBackgroundTransControl());
    }

    @Test
    void testGetMobileBackgroundImage() {
        setField(pageHeroModel, "mobileBackgroundImage", "/content/dam/mobile-hero.jpg");
        assertEquals("/content/dam/mobile-hero.jpg", pageHeroModel.getMobileBackgroundImage());
    }

    @Test
    void testGetMobileBgPositionLeftAndRight() {
        setField(pageHeroModel, "mobileBgPositionLeftAndRight", "left");
        assertEquals("left", pageHeroModel.getMobileBgPositionLeftAndRight());
    }

    @Test
    void testGetMobileBgPositionTopAndBottom() {
        setField(pageHeroModel, "mobileBgPositionTopAndBottom", "bottom");
        assertEquals("bottom", pageHeroModel.getMobileBgPositionTopAndBottom());
    }

    @Test
    void testGetMobileHeadlineAlignment() {
        setField(pageHeroModel, "mobileHeadlineAlignment", "center");
        assertEquals("center", pageHeroModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileShortDescriptionAlignment() {
        setField(pageHeroModel, "mobileShortDescriptionAlignment", "right");
        assertEquals("right", pageHeroModel.getMobileShortDescriptionAlignment());
    }

    @Test
    void testGetMobileBgColor() {
        setField(pageHeroModel, "mobileBgColor", "black");
        assertEquals("black", pageHeroModel.getMobileBgColor());
    }

    @Test
    void testGetMobileBgColorBorderControl() {
        setField(pageHeroModel, "mobileBgColorBorderControl", "false");
        assertEquals("false", pageHeroModel.getMobileBgColorBorderControl());
    }

    @Test
    void testGetMobileBackgroundTransControl() {
        setField(pageHeroModel, "mobileBackgroundTransControl", "75");
        assertEquals("75", pageHeroModel.getMobileBackgroundTransControl());
    }

    @Test
    void testNullValues() {
        assertNull(pageHeroModel.getBackgroundImage());
        assertNull(pageHeroModel.getBgPositionLeftAndRight());
        assertNull(pageHeroModel.getBgPositionTopAndBottom());
        assertNull(pageHeroModel.getHeadlineText());
        assertNull(pageHeroModel.getHeadlineAlignment());
        assertNull(pageHeroModel.getShortDescription());
        assertNull(pageHeroModel.getCopySectionAlignment());
        assertNull(pageHeroModel.getCopySectionBgColor());
        assertNull(pageHeroModel.getCopySectionBgBorderControl());
        assertNull(pageHeroModel.getBackgroundTransControl());
        assertNull(pageHeroModel.getMobileBackgroundImage());
        assertNull(pageHeroModel.getMobileBgPositionLeftAndRight());
        assertNull(pageHeroModel.getMobileBgPositionTopAndBottom());
        assertNull(pageHeroModel.getMobileHeadlineAlignment());
        assertNull(pageHeroModel.getMobileShortDescriptionAlignment());
        assertNull(pageHeroModel.getMobileBgColor());
        assertNull(pageHeroModel.getMobileBgColorBorderControl());
        assertNull(pageHeroModel.getMobileBackgroundTransControl());
    }
}
