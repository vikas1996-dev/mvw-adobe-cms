package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MVCLogoTest {

    private MVCLogo mvcLogo;

    @BeforeEach
    void setUp() {
        mvcLogo = new MVCLogo();
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
    void testGetTitle() {
        setField(mvcLogo, "title", "Marriott Vacation Club");
        assertEquals("Marriott Vacation Club", mvcLogo.getTitle());
    }

    @Test
    void testGetCopySectionBorderControl() {
        setField(mvcLogo, "copySectionBorderControl", "border-top");
        assertEquals("border-top", mvcLogo.getCopySectionBorderControl());
    }

    @Test
    void testGetCopySectionBgColorTransparency() {
        setField(mvcLogo, "copySectionBgColorTransparency", "0.8");
        assertEquals("0.8", mvcLogo.getCopySectionBgColorTransparency());
    }

    @Test
    void testNullValues() {
        MVCLogo emptyLogo = new MVCLogo();
        assertNull(emptyLogo.getTitle());
        assertNull(emptyLogo.getCopySectionBorderControl());
        assertNull(emptyLogo.getCopySectionBgColorTransparency());
    }

    @Test
    void testBorderControlNone() {
        setField(mvcLogo, "copySectionBorderControl", "none");
        assertEquals("none", mvcLogo.getCopySectionBorderControl());
    }

    @Test
    void testBorderControlBottom() {
        setField(mvcLogo, "copySectionBorderControl", "border-bottom");
        assertEquals("border-bottom", mvcLogo.getCopySectionBorderControl());
    }

    @Test
    void testBorderControlBoth() {
        setField(mvcLogo, "copySectionBorderControl", "border-both");
        assertEquals("border-both", mvcLogo.getCopySectionBorderControl());
    }

    @Test
    void testTransparencyZero() {
        setField(mvcLogo, "copySectionBgColorTransparency", "0");
        assertEquals("0", mvcLogo.getCopySectionBgColorTransparency());
    }

    @Test
    void testTransparencyFull() {
        setField(mvcLogo, "copySectionBgColorTransparency", "1");
        assertEquals("1", mvcLogo.getCopySectionBgColorTransparency());
    }

    @Test
    void testTitleWithSpecialCharacters() {
        setField(mvcLogo, "title", "Marriott™ Vacation Club®");
        assertEquals("Marriott™ Vacation Club®", mvcLogo.getTitle());
    }
}
