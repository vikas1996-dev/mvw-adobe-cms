package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultiBubbleModelTest {

    private MultiBubbleModel multiBubbleModel;

    @BeforeEach
    void setUp() {
        multiBubbleModel = new MultiBubbleModel();
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
    void testGetBgColor() {
        setField(multiBubbleModel, "bgColor", "white");
        assertEquals("white", multiBubbleModel.getBgColor());
    }

    @Test
    void testGetBgImage() {
        setField(multiBubbleModel, "bgImage", "/content/dam/bg.jpg");
        assertEquals("/content/dam/bg.jpg", multiBubbleModel.getBgImage());
    }

    @Test
    void testGetBgImageTransparency() {
        setField(multiBubbleModel, "bgImageTransparency", 50);
        assertEquals(50, multiBubbleModel.getBgImageTransparency());
    }

    @Test
    void testGetHeadlineText() {
        setField(multiBubbleModel, "headlineText", "Multi Bubble Section");
        assertEquals("Multi Bubble Section", multiBubbleModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineSize() {
        setField(multiBubbleModel, "headlineSize", "heading3");
        assertEquals("heading3", multiBubbleModel.getHeadlineSize());
    }

    @Test
    void testGetHeadlineAlignment() {
        setField(multiBubbleModel, "headlineAlignment", "center");
        assertEquals("center", multiBubbleModel.getHeadlineAlignment());
    }

    @Test
    void testGetDescription() {
        setField(multiBubbleModel, "description", "Description text");
        assertEquals("Description text", multiBubbleModel.getDescription());
    }

    @Test
    void testGetDescriptionAlignment() {
        setField(multiBubbleModel, "descriptionAlignment", "left");
        assertEquals("left", multiBubbleModel.getDescriptionAlignment());
    }

    @Test
    void testGetImage1() {
        setField(multiBubbleModel, "image1", "/content/dam/img1.jpg");
        assertEquals("/content/dam/img1.jpg", multiBubbleModel.getImage1());
    }

    @Test
    void testGetAltText1() {
        setField(multiBubbleModel, "altText1", "Alt text 1");
        assertEquals("Alt text 1", multiBubbleModel.getAltText1());
    }

    @Test
    void testGetImage2() {
        setField(multiBubbleModel, "image2", "/content/dam/img2.jpg");
        assertEquals("/content/dam/img2.jpg", multiBubbleModel.getImage2());
    }

    @Test
    void testGetAltText2() {
        setField(multiBubbleModel, "altText2", "Alt text 2");
        assertEquals("Alt text 2", multiBubbleModel.getAltText2());
    }

    @Test
    void testGetImage3() {
        setField(multiBubbleModel, "image3", "/content/dam/img3.jpg");
        assertEquals("/content/dam/img3.jpg", multiBubbleModel.getImage3());
    }

    @Test
    void testGetAltText3() {
        setField(multiBubbleModel, "altText3", "Alt text 3");
        assertEquals("Alt text 3", multiBubbleModel.getAltText3());
    }

    @Test
    void testGetImage4() {
        setField(multiBubbleModel, "image4", "/content/dam/img4.jpg");
        assertEquals("/content/dam/img4.jpg", multiBubbleModel.getImage4());
    }

    @Test
    void testGetAltText4() {
        setField(multiBubbleModel, "altText4", "Alt text 4");
        assertEquals("Alt text 4", multiBubbleModel.getAltText4());
    }

    @Test
    void testGetImage5() {
        setField(multiBubbleModel, "image5", "/content/dam/img5.jpg");
        assertEquals("/content/dam/img5.jpg", multiBubbleModel.getImage5());
    }

    @Test
    void testGetAltText5() {
        setField(multiBubbleModel, "altText5", "Alt text 5");
        assertEquals("Alt text 5", multiBubbleModel.getAltText5());
    }

    @Test
    void testGetImage6() {
        setField(multiBubbleModel, "image6", "/content/dam/img6.jpg");
        assertEquals("/content/dam/img6.jpg", multiBubbleModel.getImage6());
    }

    @Test
    void testGetAltText6() {
        setField(multiBubbleModel, "altText6", "Alt text 6");
        assertEquals("Alt text 6", multiBubbleModel.getAltText6());
    }

    @Test
    void testGetImage7() {
        setField(multiBubbleModel, "image7", "/content/dam/img7.jpg");
        assertEquals("/content/dam/img7.jpg", multiBubbleModel.getImage7());
    }

    @Test
    void testGetAltText7() {
        setField(multiBubbleModel, "altText7", "Alt text 7");
        assertEquals("Alt text 7", multiBubbleModel.getAltText7());
    }

    @Test
    void testGetMobileHeadlineAlignment() {
        setField(multiBubbleModel, "mobileHeadlineAlignment", "right");
        assertEquals("right", multiBubbleModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileDescriptionAlignment() {
        setField(multiBubbleModel, "mobileDescriptionAlignment", "center");
        assertEquals("center", multiBubbleModel.getMobileDescriptionAlignment());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(multiBubbleModel, "headlineSize", "heading2");
        assertEquals("h2", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(multiBubbleModel, "headlineSize", "heading3");
        assertEquals("h3", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(multiBubbleModel, "headlineSize", "heading4");
        assertEquals("h4", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(multiBubbleModel, "headlineSize", "heading5");
        assertEquals("h5", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(multiBubbleModel, "headlineSize", "heading6");
        assertEquals("h6", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullSize() {
        setField(multiBubbleModel, "headlineSize", null);
        assertEquals("h2", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidSize() {
        setField(multiBubbleModel, "headlineSize", "paragraph");
        assertEquals("h2", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        // heading1 doesn't match the regex heading[2-6]
        setField(multiBubbleModel, "headlineSize", "heading1");
        assertEquals("h2", multiBubbleModel.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(multiBubbleModel.getBgColor());
        assertNull(multiBubbleModel.getBgImage());
        assertNull(multiBubbleModel.getBgImageTransparency());
        assertNull(multiBubbleModel.getHeadlineText());
        assertNull(multiBubbleModel.getHeadlineSize());
        assertNull(multiBubbleModel.getHeadlineAlignment());
        assertNull(multiBubbleModel.getDescription());
        assertNull(multiBubbleModel.getDescriptionAlignment());
        assertNull(multiBubbleModel.getImage1());
        assertNull(multiBubbleModel.getAltText1());
        assertNull(multiBubbleModel.getImage2());
        assertNull(multiBubbleModel.getAltText2());
        assertNull(multiBubbleModel.getImage3());
        assertNull(multiBubbleModel.getAltText3());
        assertNull(multiBubbleModel.getImage4());
        assertNull(multiBubbleModel.getAltText4());
        assertNull(multiBubbleModel.getImage5());
        assertNull(multiBubbleModel.getAltText5());
        assertNull(multiBubbleModel.getImage6());
        assertNull(multiBubbleModel.getAltText6());
        assertNull(multiBubbleModel.getImage7());
        assertNull(multiBubbleModel.getAltText7());
        assertNull(multiBubbleModel.getMobileHeadlineAlignment());
        assertNull(multiBubbleModel.getMobileDescriptionAlignment());
    }
}
