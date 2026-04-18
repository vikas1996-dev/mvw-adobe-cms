package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SingleQATest {

    private SingleQA singleQA;

    @BeforeEach
    void setUp() {
        singleQA = new SingleQA();
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
        setField(singleQA, "headlineText", "Question");
        assertEquals("Question", singleQA.getHeadlineText());
    }

    @Test
    void testGetHeadlineType() {
        setField(singleQA, "headlineType", "heading2");
        assertEquals("heading2", singleQA.getHeadlineType());
    }

    @Test
    void testGetLogo() {
        setField(singleQA, "logo", "/content/dam/logo.png");
        assertEquals("/content/dam/logo.png", singleQA.getLogo());
    }

    @Test
    void testGetBgImageAlt() {
        setField(singleQA, "bgImageAlt", "Background Alt");
        assertEquals("Background Alt", singleQA.getBgImageAlt());
    }

    @Test
    void testGetCopySectionBgColor() {
        setField(singleQA, "copySectionBgColor", "white");
        assertEquals("white", singleQA.getCopySectionBgColor());
    }

    @Test
    void testGetBrandIconImageBgColor() {
        setField(singleQA, "brandIconImageBgColor", "blue");
        assertEquals("blue", singleQA.getBrandIconImageBgColor());
    }

    @Test
    void testGetCopyAlignment() {
        setField(singleQA, "copyAlignment", "center");
        assertEquals("center", singleQA.getCopyAlignment());
    }

    @Test
    void testGetCopyAlignmentDesktop() {
        setField(singleQA, "copyAlignmentDesktop", "left");
        assertEquals("left", singleQA.getCopyAlignmentDesktop());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        setField(singleQA, "headlineType", "heading1");
        assertEquals("h1", singleQA.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(singleQA, "headlineType", "heading2");
        assertEquals("h2", singleQA.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(singleQA, "headlineType", "heading3");
        assertEquals("h3", singleQA.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(singleQA, "headlineType", "heading4");
        assertEquals("h4", singleQA.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(singleQA, "headlineType", "heading5");
        assertEquals("h5", singleQA.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(singleQA, "headlineType", "heading6");
        assertEquals("h6", singleQA.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(singleQA, "headlineType", null);
        assertEquals("h2", singleQA.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(singleQA, "headlineType", "paragraph");
        assertEquals("h2", singleQA.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(singleQA.getHeadlineText());
        assertNull(singleQA.getHeadlineType());
        assertNull(singleQA.getLogo());
        assertNull(singleQA.getBgImageAlt());
        assertNull(singleQA.getCopySectionBgColor());
        assertNull(singleQA.getBrandIconImageBgColor());
        assertNull(singleQA.getCopyAlignment());
        assertNull(singleQA.getCopyAlignmentDesktop());
    }
}
