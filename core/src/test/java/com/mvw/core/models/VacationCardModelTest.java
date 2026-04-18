package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VacationCardModelTest {

    private VacationCardModel vacationCardModel;

    @BeforeEach
    void setUp() {
        vacationCardModel = new VacationCardModel();
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
    void testGetStyle() {
        setField(vacationCardModel, "style", "card-style");
        assertEquals("card-style", vacationCardModel.getStyle());
    }

    @Test
    void testGetSubheader() {
        setField(vacationCardModel, "subheader", "Subheader Text");
        assertEquals("Subheader Text", vacationCardModel.getSubheader());
    }

    @Test
    void testGetHeadlineText() {
        setField(vacationCardModel, "headlineText", "Vacation Cards");
        assertEquals("Vacation Cards", vacationCardModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineType() {
        setField(vacationCardModel, "headlineType", "heading2");
        assertEquals("heading2", vacationCardModel.getHeadlineType());
    }

    @Test
    void testGetHeadlineAlignment() {
        setField(vacationCardModel, "headlineAlignment", "center");
        assertEquals("center", vacationCardModel.getHeadlineAlignment());
    }

    @Test
    void testGetHeadlineStyledBorder() {
        setField(vacationCardModel, "headlineStyledBorder", "true");
        assertEquals("true", vacationCardModel.getHeadlineStyledBorder());
    }

    @Test
    void testGetCopyText() {
        setField(vacationCardModel, "copyText", "Card copy text");
        assertEquals("Card copy text", vacationCardModel.getCopyText());
    }

    @Test
    void testGetBackgroundColor() {
        setField(vacationCardModel, "backgroundColor", "white");
        assertEquals("white", vacationCardModel.getBackgroundColor());
    }

    @Test
    void testGetSecondaryCopyText() {
        setField(vacationCardModel, "secondaryCopyText", "Secondary copy");
        assertEquals("Secondary copy", vacationCardModel.getSecondaryCopyText());
    }

    @Test
    void testGetBrandIconImageBgColor() {
        setField(vacationCardModel, "brandIconImageBgColor", "blue");
        assertEquals("blue", vacationCardModel.getBrandIconImageBgColor());
    }

    @Test
    void testGetCopyAlignment() {
        setField(vacationCardModel, "copyAlignment", "left");
        assertEquals("left", vacationCardModel.getCopyAlignment());
    }

    @Test
    void testGetSecondaryCopyAlignment() {
        setField(vacationCardModel, "secondaryCopyAlignment", "right");
        assertEquals("right", vacationCardModel.getSecondaryCopyAlignment());
    }

    @Test
    void testGetStackOnMobile() {
        setField(vacationCardModel, "stackOnMobile", "true");
        assertEquals("true", vacationCardModel.getStackOnMobile());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        setField(vacationCardModel, "headlineType", "heading1");
        assertEquals("h1", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(vacationCardModel, "headlineType", "heading2");
        assertEquals("h2", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(vacationCardModel, "headlineType", "heading3");
        assertEquals("h3", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(vacationCardModel, "headlineType", "heading4");
        assertEquals("h4", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(vacationCardModel, "headlineType", "heading5");
        assertEquals("h5", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(vacationCardModel, "headlineType", "heading6");
        assertEquals("h6", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(vacationCardModel, "headlineType", null);
        assertEquals("h2", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(vacationCardModel, "headlineType", "paragraph");
        assertEquals("h2", vacationCardModel.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(vacationCardModel.getStyle());
        assertNull(vacationCardModel.getSubheader());
        assertNull(vacationCardModel.getHeadlineText());
        assertNull(vacationCardModel.getHeadlineType());
        assertNull(vacationCardModel.getHeadlineAlignment());
        assertNull(vacationCardModel.getHeadlineStyledBorder());
        assertNull(vacationCardModel.getCopyText());
        assertNull(vacationCardModel.getBackgroundColor());
        assertNull(vacationCardModel.getSecondaryCopyText());
        assertNull(vacationCardModel.getBrandIconImageBgColor());
        assertNull(vacationCardModel.getCopyAlignment());
        assertNull(vacationCardModel.getSecondaryCopyAlignment());
        assertNull(vacationCardModel.getStackOnMobile());
    }
}
