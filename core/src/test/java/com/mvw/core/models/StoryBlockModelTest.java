package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoryBlockModelTest {

    private StoryBlockModel storyBlockModel;

    @BeforeEach
    void setUp() {
        storyBlockModel = new StoryBlockModel();
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
    void testGetSubHeadlineText() {
        setField(storyBlockModel, "subHeadlineText", "Sub Headline");
        assertEquals("Sub Headline", storyBlockModel.getSubHeadlineText());
    }

    @Test
    void testGetHeadlineText() {
        setField(storyBlockModel, "headlineText", "Main Headline");
        assertEquals("Main Headline", storyBlockModel.getHeadlineText());
    }

    @Test
    void testGetResolvedHeadlineTextWithoutPlaceholder() {
        setField(storyBlockModel, "headlineText", "Welcome to the Resort");
        assertEquals("Welcome to the Resort", storyBlockModel.getResolvedHeadlineText());
    }

    @Test
    void testGetResolvedHeadlineTextWithNullHeadline() {
        setField(storyBlockModel, "headlineText", null);
        assertNull(storyBlockModel.getResolvedHeadlineText());
    }

    @Test
    void testGetHeadlineStyledBorder() {
        setField(storyBlockModel, "headlineStyledBorder", "yes");
        assertEquals("yes", storyBlockModel.getHeadlineStyledBorder());
    }

    @Test
    void testGetSubTitle1() {
        setField(storyBlockModel, "subTitle1", "Subtitle 1");
        assertEquals("Subtitle 1", storyBlockModel.getSubTitle1());
    }

    @Test
    void testGetShortDescription1() {
        setField(storyBlockModel, "shortDescription1", "Description 1");
        assertEquals("Description 1", storyBlockModel.getShortDescription1());
    }

    @Test
    void testGetSubTitle2() {
        setField(storyBlockModel, "subTitle2", "Subtitle 2");
        assertEquals("Subtitle 2", storyBlockModel.getSubTitle2());
    }

    @Test
    void testGetShortDescription2() {
        setField(storyBlockModel, "shortDescription2", "Description 2");
        assertEquals("Description 2", storyBlockModel.getShortDescription2());
    }

    @Test
    void testGetLogoAltText() {
        setField(storyBlockModel, "logoAltText", "Logo Alt Text");
        assertEquals("Logo Alt Text", storyBlockModel.getLogoAltText());
    }

    @Test
    void testGetBigImageAlt() {
        setField(storyBlockModel, "bigImageAlt", "Big Image Alt");
        assertEquals("Big Image Alt", storyBlockModel.getBigImageAlt());
    }

    @Test
    void testGetSmallImageAlt() {
        setField(storyBlockModel, "smallImageAlt", "Small Image Alt");
        assertEquals("Small Image Alt", storyBlockModel.getSmallImageAlt());
    }

    @Test
    void testGetMobileBigImageAltText() {
        setField(storyBlockModel, "mobileBigImageAltText", "Mobile Big Image Alt");
        assertEquals("Mobile Big Image Alt", storyBlockModel.getMobileBigImageAltText());
    }

    @Test
    void testGetMobileSmallImageAltText() {
        setField(storyBlockModel, "mobileSmallImageAltText", "Mobile Small Image Alt");
        assertEquals("Mobile Small Image Alt", storyBlockModel.getMobileSmallImageAltText());
    }

    @Test
    void testGetNumberOfImages() {
        setField(storyBlockModel, "numberOfImages", "two");
        assertEquals("two", storyBlockModel.getNumberOfImages());
    }

    @Test
    void testGetOverlayColor() {
        setField(storyBlockModel, "overlayColor", "black");
        assertEquals("black", storyBlockModel.getOverlayColor());
    }

    @Test
    void testGetCaption() {
        setField(storyBlockModel, "caption", "Image Caption");
        assertEquals("Image Caption", storyBlockModel.getCaption());
    }

    @Test
    void testGetOverlayOpacity() {
        setField(storyBlockModel, "overlayOpacity", "0.500");
        assertEquals("0.500", storyBlockModel.getOverlayOpacity());
    }

    @Test
    void testGetSections() {
        List<StoryBlockModel.Section> sections = Collections.emptyList();
        setField(storyBlockModel, "sections", sections);
        assertEquals(sections, storyBlockModel.getSections());
    }

    @Test
    void testGetMediaType() {
        setField(storyBlockModel, "mediaType", "video");
        assertEquals("video", storyBlockModel.getMediaType());
    }

    @Test
    void testGetBgVideoID() {
        setField(storyBlockModel, "bgVideoID", "12345");
        assertEquals("12345", storyBlockModel.getBgVideoID());
    }

    @Test
    void testGetTimerCheckbox() {
        setField(storyBlockModel, "timerCheckbox", "true");
        assertEquals("true", storyBlockModel.getTimerCheckbox());
    }

    @Test
    void testGetTimerText() {
        setField(storyBlockModel, "timerText", "Timer Text");
        assertEquals("Timer Text", storyBlockModel.getTimerText());
    }

    @Test
    void testGetSmallImgPosClassDesktop() {
        setField(storyBlockModel, "smallImgPosClassDesktop", "small--top-left");
        assertEquals("small--top-left", storyBlockModel.getSmallImgPosClassDesktop());
    }

    @Test
    void testGetSmallImgPosClassMobile() {
        setField(storyBlockModel, "smallImgPosClassMobile", "m-small--top-right");
        assertEquals("m-small--top-right", storyBlockModel.getSmallImgPosClassMobile());
    }

    @Test
    void testGetBgThemeClassWithColor() {
        setField(storyBlockModel, "copySectionBgColor", "blue");
        assertEquals("bg-theme-blue", storyBlockModel.getBgThemeClass());
    }

    @Test
    void testGetBgThemeClassWithNone() {
        setField(storyBlockModel, "copySectionBgColor", "none");
        assertEquals("", storyBlockModel.getBgThemeClass());
    }

    @Test
    void testGetBgThemeClassWithNull() {
        setField(storyBlockModel, "copySectionBgColor", null);
        assertEquals("", storyBlockModel.getBgThemeClass());
    }

    @Test
    void testGetWrapperClassWithContainerV3() {
        setField(storyBlockModel, "copySectionBgBorderControl", "container-v3");
        assertEquals("", storyBlockModel.getWrapperClass());
    }

    @Test
    void testGetWrapperClassWithOtherContainer() {
        setField(storyBlockModel, "copySectionBgBorderControl", "container-v2");
        setField(storyBlockModel, "copySectionBgColor", "blue");
        assertEquals("bg-theme-blue", storyBlockModel.getWrapperClass());
    }

    @Test
    void testGetContainerClassWithContainerV2() {
        setField(storyBlockModel, "copySectionBgBorderControl", "container-v2");
        assertEquals("container-v2", storyBlockModel.getContainerClass());
    }

    @Test
    void testGetContainerClassWithContainerV3() {
        setField(storyBlockModel, "copySectionBgBorderControl", "container-v3");
        setField(storyBlockModel, "copySectionBgColor", "blue");
        assertTrue(storyBlockModel.getContainerClass().contains("container-v3"));
        assertTrue(storyBlockModel.getContainerClass().contains("bg-theme-blue"));
    }

    @Test
    void testGetContainerClassWithContainerV3AndNoColor() {
        setField(storyBlockModel, "copySectionBgBorderControl", "container-v3");
        setField(storyBlockModel, "copySectionBgColor", "none");
        assertEquals("container-v3", storyBlockModel.getContainerClass());
    }

    @Test
    void testHasOverlayWithContainerV3() {
        setField(storyBlockModel, "copySectionBgBorderControl", "container-v3");
        assertTrue(storyBlockModel.hasOverlay());
    }

    @Test
    void testHasOverlayWithOtherContainer() {
        setField(storyBlockModel, "copySectionBgBorderControl", "container-v2");
        assertFalse(storyBlockModel.hasOverlay());
    }

    @Test
    void testGetMobileOverlayClassWithNull() {
        setField(storyBlockModel, "mobileBgColorBorderControl", null);
        assertEquals("", storyBlockModel.getMobileOverlayClass());
    }

    @Test
    void testGetMobileOverlayClassWithNone() {
        setField(storyBlockModel, "mobileBgColorBorderControl", "none");
        assertEquals("", storyBlockModel.getMobileOverlayClass());
    }

    @Test
    void testGetMobileOverlayClassWithConcaveTop() {
        setField(storyBlockModel, "mobileBgColorBorderControl", "concave Top");
        assertEquals("m-concave", storyBlockModel.getMobileOverlayClass());
    }

    @Test
    void testGetMobileOverlayClassWithConcaveTopLowercase() {
        setField(storyBlockModel, "mobileBgColorBorderControl", "concave top");
        assertEquals("m-concave", storyBlockModel.getMobileOverlayClass());
    }

    @Test
    void testGetMobileOverlayClassWithConvexTop() {
        setField(storyBlockModel, "mobileBgColorBorderControl", "convex top");
        assertEquals("m-convex", storyBlockModel.getMobileOverlayClass());
    }

    @Test
    void testGetMobileOverlayClassWithOtherValue() {
        setField(storyBlockModel, "mobileBgColorBorderControl", "other");
        assertEquals("", storyBlockModel.getMobileOverlayClass());
    }

    @Test
    void testGetStackingOrderClassWithTrue() {
        setField(storyBlockModel, "stackingOrder", "true");
        assertEquals("stackingOrder", storyBlockModel.getStackingOrderClass());
    }

    @Test
    void testGetStackingOrderClassWithFalse() {
        setField(storyBlockModel, "stackingOrder", "false");
        assertEquals("", storyBlockModel.getStackingOrderClass());
    }

    @Test
    void testGetSvgColorWithOverlayColor() {
        setField(storyBlockModel, "overlayColor", "red");
        assertEquals("red", storyBlockModel.getSvgColor());
    }

    @Test
    void testGetSvgColorWithNoneOverlay() {
        setField(storyBlockModel, "overlayColor", "none");
        setField(storyBlockModel, "copySectionBgColor", "blue");
        assertEquals("blue", storyBlockModel.getSvgColor());
    }

    @Test
    void testGetSvgColorWithNullOverlay() {
        setField(storyBlockModel, "overlayColor", null);
        setField(storyBlockModel, "copySectionBgColor", "green");
        assertEquals("green", storyBlockModel.getSvgColor());
    }

    @Test
    void testGetSvgColorWithNoColors() {
        setField(storyBlockModel, "overlayColor", null);
        setField(storyBlockModel, "copySectionBgColor", "none");
        assertEquals("", storyBlockModel.getSvgColor());
    }

    @Test
    void testGetSvgBgColorWithColor() {
        setField(storyBlockModel, "copySectionBgColor", "purple");
        assertEquals("purple", storyBlockModel.getSvgBgColor());
    }

    @Test
    void testGetSvgBgColorWithNone() {
        setField(storyBlockModel, "copySectionBgColor", "none");
        assertEquals("", storyBlockModel.getSvgBgColor());
    }

    @Test
    void testGetSvgBgColorWithNull() {
        setField(storyBlockModel, "copySectionBgColor", null);
        assertEquals("", storyBlockModel.getSvgBgColor());
    }

    @Test
    void testGetAlignClassCenter() {
        setField(storyBlockModel, "copySectionAlignment", "center");
        assertEquals("align-center", storyBlockModel.getAlignClass());
    }

    @Test
    void testGetAlignClassRight() {
        setField(storyBlockModel, "copySectionAlignment", "right");
        assertEquals("align-right", storyBlockModel.getAlignClass());
    }

    @Test
    void testGetAlignClassLeft() {
        setField(storyBlockModel, "copySectionAlignment", "left");
        assertEquals("align-left", storyBlockModel.getAlignClass());
    }

    @Test
    void testGetMobileAlignClassCenter() {
        setField(storyBlockModel, "mobileCopyAlignment", "center");
        assertEquals("m-align-center", storyBlockModel.getMobileAlignClass());
    }

    @Test
    void testGetMobileAlignClassRight() {
        setField(storyBlockModel, "mobileCopyAlignment", "right");
        assertEquals("m-align-right", storyBlockModel.getMobileAlignClass());
    }

    @Test
    void testGetMobileAlignClassLeft() {
        setField(storyBlockModel, "mobileCopyAlignment", "left");
        assertEquals("m-align-left", storyBlockModel.getMobileAlignClass());
    }

    @Test
    void testGetHeadlineAlignmentClassCenter() {
        setField(storyBlockModel, "headlineAlignment", "center");
        assertEquals("align-center", storyBlockModel.getHeadlineAlignmentClass());
    }

    @Test
    void testGetHeadlineAlignmentClassRight() {
        setField(storyBlockModel, "headlineAlignment", "right");
        assertEquals("align-right", storyBlockModel.getHeadlineAlignmentClass());
    }

    @Test
    void testGetHeadlineAlignmentClassLeft() {
        setField(storyBlockModel, "headlineAlignment", "left");
        assertEquals("align-left", storyBlockModel.getHeadlineAlignmentClass());
    }

    @Test
    void testGetMobileHeadlineAlignmentClassCenter() {
        setField(storyBlockModel, "mobileHeadlineAlignment", "center");
        assertEquals("m-align-center", storyBlockModel.getMobileHeadlineAlignmentClass());
    }

    @Test
    void testGetMobileHeadlineAlignmentClassRight() {
        setField(storyBlockModel, "mobileHeadlineAlignment", "right");
        assertEquals("m-align-right", storyBlockModel.getMobileHeadlineAlignmentClass());
    }

    @Test
    void testGetMobileHeadlineAlignmentClassLeft() {
        setField(storyBlockModel, "mobileHeadlineAlignment", "left");
        assertEquals("m-align-left", storyBlockModel.getMobileHeadlineAlignmentClass());
    }

    @Test
    void testGetHeadlineSizeClassH3() {
        setField(storyBlockModel, "headlineSize", "h3");
        assertEquals("heading3", storyBlockModel.getHeadlineSizeClass());
    }

    @Test
    void testGetHeadlineSizeClassH4() {
        setField(storyBlockModel, "headlineSize", "h4");
        assertEquals("heading4", storyBlockModel.getHeadlineSizeClass());
    }

    @Test
    void testGetHeadlineSizeClassDefault() {
        setField(storyBlockModel, "headlineSize", "h2");
        assertEquals("heading2", storyBlockModel.getHeadlineSizeClass());
    }

    @Test
    void testIsHeadlineBorderEnabledYes() {
        setField(storyBlockModel, "headlineStyledBorder", "yes");
        assertTrue(storyBlockModel.isHeadlineBorderEnabled());
    }

    @Test
    void testIsHeadlineBorderEnabledNo() {
        setField(storyBlockModel, "headlineStyledBorder", "no");
        assertFalse(storyBlockModel.isHeadlineBorderEnabled());
    }

    @Test
    void testGetBigImgStyleClassRounded() {
        setField(storyBlockModel, "bigImageStyle", "rounded");
        assertEquals("img--rounded", storyBlockModel.getBigImgStyleClass());
    }

    @Test
    void testGetBigImgStyleClassDoubleRounded() {
        setField(storyBlockModel, "bigImageStyle", "doubleRounded");
        assertEquals("img--double-rounded", storyBlockModel.getBigImgStyleClass());
    }

    @Test
    void testGetBigImgStyleClassSquared() {
        setField(storyBlockModel, "bigImageStyle", "squared");
        assertEquals("img--squared", storyBlockModel.getBigImgStyleClass());
    }

    @Test
    void testGetBigImgStyleClassOther() {
        setField(storyBlockModel, "bigImageStyle", "other");
        assertEquals("", storyBlockModel.getBigImgStyleClass());
    }

    @Test
    void testGetSmallImgStyleClassRounded() {
        setField(storyBlockModel, "smallImageStyle", "rounded");
        assertEquals("img--rounded", storyBlockModel.getSmallImgStyleClass());
    }

    @Test
    void testGetContentFlipClassRight() {
        setField(storyBlockModel, "imageAlignment", "right");
        assertEquals("content-flip", storyBlockModel.getContentFlipClass());
    }

    @Test
    void testGetContentFlipClassLeft() {
        setField(storyBlockModel, "imageAlignment", "left");
        assertEquals("", storyBlockModel.getContentFlipClass());
    }

    @Test
    void testHasVideoWithVideoType() {
        setField(storyBlockModel, "mediaType", "video");
        setField(storyBlockModel, "bgVideo", "/content/dam/video.mp4");
        assertTrue(storyBlockModel.hasVideo());
    }

    @Test
    void testHasVideoWithImageType() {
        setField(storyBlockModel, "mediaType", "image");
        setField(storyBlockModel, "bgVideo", "/content/dam/video.mp4");
        assertFalse(storyBlockModel.hasVideo());
    }

    @Test
    void testHasVideoWithNullVideo() {
        setField(storyBlockModel, "mediaType", "video");
        setField(storyBlockModel, "bgVideo", null);
        assertFalse(storyBlockModel.hasVideo());
    }

    @Test
    void testHasVideoWithEmptyVideo() {
        setField(storyBlockModel, "mediaType", "video");
        setField(storyBlockModel, "bgVideo", "");
        assertFalse(storyBlockModel.hasVideo());
    }

    @Test
    void testSectionClass() {
        StoryBlockModel.Section section = new StoryBlockModel.Section("Subheading", "Description");
        assertEquals("Subheading", section.getSubheading());
        assertEquals("Description", section.getDescription());
    }

    @Test
    void testSectionClassWithNullValues() {
        StoryBlockModel.Section section = new StoryBlockModel.Section(null, null);
        assertNull(section.getSubheading());
        assertNull(section.getDescription());
    }
}
