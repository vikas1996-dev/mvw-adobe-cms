package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BubbleStatsModelTest {

    private BubbleStatsModel bubbleStatsModel;

    @BeforeEach
    void setUp() {
        bubbleStatsModel = new BubbleStatsModel();
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
        setField(bubbleStatsModel, "style", "rounded");
        assertEquals("rounded", bubbleStatsModel.getStyle());
    }

    @Test
    void testGetBgColor() {
        setField(bubbleStatsModel, "bgColor", "blue");
        assertEquals("blue", bubbleStatsModel.getBgColor());
    }

    @Test
    void testGetHeadlineText() {
        setField(bubbleStatsModel, "headlineText", "Statistics");
        assertEquals("Statistics", bubbleStatsModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineSize() {
        setField(bubbleStatsModel, "headlineSize", "heading2");
        assertEquals("heading2", bubbleStatsModel.getHeadlineSize());
    }

    @Test
    void testGetHeadlineAlignment() {
        setField(bubbleStatsModel, "headlineAlignment", "center");
        assertEquals("center", bubbleStatsModel.getHeadlineAlignment());
    }

    @Test
    void testGetDescription() {
        setField(bubbleStatsModel, "description", "A description");
        assertEquals("A description", bubbleStatsModel.getDescription());
    }

    @Test
    void testGetDescriptionAlignment() {
        setField(bubbleStatsModel, "descriptionAlignment", "left");
        assertEquals("left", bubbleStatsModel.getDescriptionAlignment());
    }

    @Test
    void testGetCopyWithinCircle1() {
        setField(bubbleStatsModel, "copyWithinCircle1", "100+");
        assertEquals("100+", bubbleStatsModel.getCopyWithinCircle1());
    }

    @Test
    void testGetCopyUnderCircle1() {
        setField(bubbleStatsModel, "copyUnderCircle1", "Resorts");
        assertEquals("Resorts", bubbleStatsModel.getCopyUnderCircle1());
    }

    @Test
    void testGetCopyWithinCircle2() {
        setField(bubbleStatsModel, "copyWithinCircle2", "200+");
        assertEquals("200+", bubbleStatsModel.getCopyWithinCircle2());
    }

    @Test
    void testGetCopyUnderCircle2() {
        setField(bubbleStatsModel, "copyUnderCircle2", "Destinations");
        assertEquals("Destinations", bubbleStatsModel.getCopyUnderCircle2());
    }

    @Test
    void testGetCopyWithinCircle3() {
        setField(bubbleStatsModel, "copyWithinCircle3", "300+");
        assertEquals("300+", bubbleStatsModel.getCopyWithinCircle3());
    }

    @Test
    void testGetCopyUnderCircle3() {
        setField(bubbleStatsModel, "copyUnderCircle3", "Activities");
        assertEquals("Activities", bubbleStatsModel.getCopyUnderCircle3());
    }

    @Test
    void testGetCopyWithinCircle4() {
        setField(bubbleStatsModel, "copyWithinCircle4", "400+");
        assertEquals("400+", bubbleStatsModel.getCopyWithinCircle4());
    }

    @Test
    void testGetCopyUnderCircle4() {
        setField(bubbleStatsModel, "copyUnderCircle4", "Amenities");
        assertEquals("Amenities", bubbleStatsModel.getCopyUnderCircle4());
    }

    @Test
    void testGetCopyWithinCircle5() {
        setField(bubbleStatsModel, "copyWithinCircle5", "500+");
        assertEquals("500+", bubbleStatsModel.getCopyWithinCircle5());
    }

    @Test
    void testGetCopyUnderCircle5() {
        setField(bubbleStatsModel, "copyUnderCircle5", "Experiences");
        assertEquals("Experiences", bubbleStatsModel.getCopyUnderCircle5());
    }

    @Test
    void testGetMobileHeadlineAlignment() {
        setField(bubbleStatsModel, "mobileHeadlineAlignment", "center");
        assertEquals("center", bubbleStatsModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignment() {
        setField(bubbleStatsModel, "mobileDescriptionAlignment", "left");
        assertEquals("left", bubbleStatsModel.getMobileDescriptionAlignment());
    }

    @Test
    void testGetSecondaryCopyText() {
        setField(bubbleStatsModel, "secondaryCopyText", "Secondary copy");
        assertEquals("Secondary copy", bubbleStatsModel.getSecondaryCopyText());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(bubbleStatsModel, "headlineSize", "heading2");
        assertEquals("h2", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(bubbleStatsModel, "headlineSize", "heading3");
        assertEquals("h3", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(bubbleStatsModel, "headlineSize", "heading4");
        assertEquals("h4", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(bubbleStatsModel, "headlineSize", "heading5");
        assertEquals("h5", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(bubbleStatsModel, "headlineSize", "heading6");
        assertEquals("h6", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullSize() {
        setField(bubbleStatsModel, "headlineSize", null);
        assertEquals("h2", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidSize() {
        setField(bubbleStatsModel, "headlineSize", "paragraph");
        assertEquals("h2", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        // heading1 doesn't match the regex heading[2-6]
        setField(bubbleStatsModel, "headlineSize", "heading1");
        assertEquals("h2", bubbleStatsModel.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(bubbleStatsModel.getStyle());
        assertNull(bubbleStatsModel.getBgColor());
        assertNull(bubbleStatsModel.getHeadlineText());
        assertNull(bubbleStatsModel.getHeadlineSize());
        assertNull(bubbleStatsModel.getHeadlineAlignment());
        assertNull(bubbleStatsModel.getDescription());
        assertNull(bubbleStatsModel.getDescriptionAlignment());
        assertNull(bubbleStatsModel.getCopyWithinCircle1());
        assertNull(bubbleStatsModel.getCopyUnderCircle1());
        assertNull(bubbleStatsModel.getCopyWithinCircle2());
        assertNull(bubbleStatsModel.getCopyUnderCircle2());
        assertNull(bubbleStatsModel.getCopyWithinCircle3());
        assertNull(bubbleStatsModel.getCopyUnderCircle3());
        assertNull(bubbleStatsModel.getCopyWithinCircle4());
        assertNull(bubbleStatsModel.getCopyUnderCircle4());
        assertNull(bubbleStatsModel.getCopyWithinCircle5());
        assertNull(bubbleStatsModel.getCopyUnderCircle5());
        assertNull(bubbleStatsModel.getMobileHeadlineAlignment());
        assertNull(bubbleStatsModel.getMobileDescriptionAlignment());
        assertNull(bubbleStatsModel.getSecondaryCopyText());
    }
}
