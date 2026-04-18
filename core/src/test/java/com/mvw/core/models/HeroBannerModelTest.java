package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HeroBannerModelTest {

    private HeroBannerModel heroBannerModel;

    @BeforeEach
    void setUp() {
        heroBannerModel = new HeroBannerModel();
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
    void testGetBgType() {
        setField(heroBannerModel, "bgType", "image");
        assertEquals("image", heroBannerModel.getBgType());
    }

    @Test
    void testGetBgVideo() {
        setField(heroBannerModel, "bgVideo", "/content/dam/videos/hero.mp4");
        assertEquals("/content/dam/videos/hero.mp4", heroBannerModel.getBgVideo());
    }

    @Test
    void testGetBgVideoID() {
        setField(heroBannerModel, "bgVideoID", "hero");
        assertEquals("hero", heroBannerModel.getBgVideoID());
    }

    @Test
    void testGetBgImage() {
        setField(heroBannerModel, "bgImage", "/content/dam/images/hero.jpg");
        assertEquals("/content/dam/images/hero.jpg", heroBannerModel.getBgImage());
    }

    @Test
    void testGetBgImageAlignmentLeftRight() {
        setField(heroBannerModel, "bgImageAlignmentLeftRight", "center");
        assertEquals("center", heroBannerModel.getBgImageAlignmentLeftRight());
    }

    @Test
    void testGetBgImageAlignmentTopBottom() {
        setField(heroBannerModel, "bgImageAlignmentTopBottom", "top");
        assertEquals("top", heroBannerModel.getBgImageAlignmentTopBottom());
    }

    @Test
    void testGetTaglineText() {
        setField(heroBannerModel, "taglineText", "Welcome to MVW");
        assertEquals("Welcome to MVW", heroBannerModel.getTaglineText());
    }

    @Test
    void testGetTaglineColor() {
        setField(heroBannerModel, "taglineColor", "white");
        assertEquals("white", heroBannerModel.getTaglineColor());
    }

    @Test
    void testGetHeadlineText() {
        setField(heroBannerModel, "headlineText", "Main Headline");
        assertEquals("Main Headline", heroBannerModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineAlignment() {
        setField(heroBannerModel, "headlineAlignment", "left");
        assertEquals("left", heroBannerModel.getHeadlineAlignment());
    }

    @Test
    void testGetCopySectionAlignment() {
        setField(heroBannerModel, "copySectionAlignment", "center");
        assertEquals("center", heroBannerModel.getCopySectionAlignment());
    }

    @Test
    void testGetCopySectionBgColor() {
        setField(heroBannerModel, "copySectionBgColor", "white");
        assertEquals("white", heroBannerModel.getCopySectionBgColor());
    }

    @Test
    void testGetCopySectionBorderControl() {
        setField(heroBannerModel, "copySectionBorderControl", "true");
        assertEquals("true", heroBannerModel.getCopySectionBorderControl());
    }

    @Test
    void testGetCopySectionBgColorTransparency() {
        setField(heroBannerModel, "copySectionBgColorTransparency", "50");
        assertEquals("50", heroBannerModel.getCopySectionBgColorTransparency());
    }

    @Test
    void testGetCtaAlignmentControl() {
        setField(heroBannerModel, "ctaAlignmentControl", "right");
        assertEquals("right", heroBannerModel.getCtaAlignmentControl());
    }

    @Test
    void testGetMobileBackgroundImage() {
        setField(heroBannerModel, "mobileBackgroundImage", "/content/dam/mobile/hero.jpg");
        assertEquals("/content/dam/mobile/hero.jpg", heroBannerModel.getMobileBackgroundImage());
    }

    @Test
    void testGetMobileBgImageAlignmentLeftRight() {
        setField(heroBannerModel, "mobileBgImageAlignmentLeftRight", "left");
        assertEquals("left", heroBannerModel.getMobileBgImageAlignmentLeftRight());
    }

    @Test
    void testGetMobileBgImageAlignmentTopBottom() {
        setField(heroBannerModel, "mobileBgImageAlignmentTopBottom", "bottom");
        assertEquals("bottom", heroBannerModel.getMobileBgImageAlignmentTopBottom());
    }

    @Test
    void testGetMobileHeadlineAlignment() {
        setField(heroBannerModel, "mobileHeadlineAlignment", "center");
        assertEquals("center", heroBannerModel.getMobileHeadlineAlignment());
    }

    @Test
    void testGetMobileTertiaryLinkAlignment() {
        setField(heroBannerModel, "mobileTertiaryLinkAlignment", "right");
        assertEquals("right", heroBannerModel.getMobileTertiaryLinkAlignment());
    }

    @Test
    void testGetMobileBgColorAlignment() {
        setField(heroBannerModel, "mobileBgColorAlignment", "left");
        assertEquals("left", heroBannerModel.getMobileBgColorAlignment());
    }

    @Test
    void testGetMobileBgColorBorderControl() {
        setField(heroBannerModel, "mobileBgColorBorderControl", "false");
        assertEquals("false", heroBannerModel.getMobileBgColorBorderControl());
    }

    @Test
    void testGetLogos() {
        List<LogoItem> logos = new ArrayList<>();
        setField(heroBannerModel, "logos", logos);
        assertEquals(logos, heroBannerModel.getLogos());
    }

    @Test
    void testGetLogosNull() {
        setField(heroBannerModel, "logos", null);
        assertNull(heroBannerModel.getLogos());
    }

    @Test
    void testInitWithBgVideoExtractionWithMp4() {
        setField(heroBannerModel, "bgVideo", "/content/dam/videos/myvideo.mp4");
        setField(heroBannerModel, "bgVideoID", null);
        
        invokeInit();
        
        assertEquals("myvideo", heroBannerModel.getBgVideoID());
    }

    @Test
    void testInitWithBgVideoExtractionWithMov() {
        setField(heroBannerModel, "bgVideo", "/content/dam/videos/clip.mov");
        setField(heroBannerModel, "bgVideoID", null);
        
        invokeInit();
        
        assertEquals("clip", heroBannerModel.getBgVideoID());
    }

    @Test
    void testInitWithBgVideoKeepsManualVideoID() {
        setField(heroBannerModel, "bgVideo", "/content/dam/videos/myvideo.mp4");
        setField(heroBannerModel, "bgVideoID", "customID");
        
        invokeInit();
        
        assertEquals("customID", heroBannerModel.getBgVideoID());
    }

    @Test
    void testInitWithBgVideoReplacesEmptyVideoID() {
        setField(heroBannerModel, "bgVideo", "/content/dam/videos/myvideo.mp4");
        setField(heroBannerModel, "bgVideoID", "");
        
        invokeInit();
        
        assertEquals("myvideo", heroBannerModel.getBgVideoID());
    }

    @Test
    void testInitWithNullBgVideo() {
        setField(heroBannerModel, "bgVideo", null);
        setField(heroBannerModel, "bgVideoID", null);
        
        invokeInit();
        
        assertNull(heroBannerModel.getBgVideoID());
    }

    @Test
    void testInitWithEmptyBgVideo() {
        setField(heroBannerModel, "bgVideo", "");
        setField(heroBannerModel, "bgVideoID", null);
        
        invokeInit();
        
        assertNull(heroBannerModel.getBgVideoID());
    }

    @Test
    void testInitWithBgVideoNoExtension() {
        setField(heroBannerModel, "bgVideo", "/content/dam/videos/noextensionvideo");
        setField(heroBannerModel, "bgVideoID", null);
        
        invokeInit();
        
        assertEquals("noextensionvideo", heroBannerModel.getBgVideoID());
    }

    @Test
    void testInitWithBgVideoMultipleDots() {
        setField(heroBannerModel, "bgVideo", "/content/dam/videos/my.video.file.mp4");
        setField(heroBannerModel, "bgVideoID", null);
        
        invokeInit();
        
        assertEquals("my.video.file", heroBannerModel.getBgVideoID());
    }

    @Test
    void testNullValues() {
        assertNull(heroBannerModel.getBgType());
        assertNull(heroBannerModel.getBgVideo());
        assertNull(heroBannerModel.getBgVideoID());
        assertNull(heroBannerModel.getBgImage());
        assertNull(heroBannerModel.getBgImageAlignmentLeftRight());
        assertNull(heroBannerModel.getBgImageAlignmentTopBottom());
        assertNull(heroBannerModel.getTaglineText());
        assertNull(heroBannerModel.getTaglineColor());
        assertNull(heroBannerModel.getHeadlineText());
        assertNull(heroBannerModel.getHeadlineAlignment());
        assertNull(heroBannerModel.getCopySectionAlignment());
        assertNull(heroBannerModel.getCopySectionBgColor());
        assertNull(heroBannerModel.getCopySectionBorderControl());
        assertNull(heroBannerModel.getCopySectionBgColorTransparency());
        assertNull(heroBannerModel.getCtaAlignmentControl());
        assertNull(heroBannerModel.getMobileBackgroundImage());
        assertNull(heroBannerModel.getMobileBgImageAlignmentLeftRight());
        assertNull(heroBannerModel.getMobileBgImageAlignmentTopBottom());
        assertNull(heroBannerModel.getMobileHeadlineAlignment());
        assertNull(heroBannerModel.getMobileTertiaryLinkAlignment());
        assertNull(heroBannerModel.getMobileBgColorAlignment());
        assertNull(heroBannerModel.getMobileBgColorBorderControl());
        assertNull(heroBannerModel.getLogos());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = HeroBannerModel.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(heroBannerModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
