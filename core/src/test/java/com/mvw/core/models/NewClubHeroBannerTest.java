package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NewClubHeroBannerTest {

    private NewClubHeroBanner newClubHeroBanner;

    @BeforeEach
    void setUp() {
        newClubHeroBanner = new NewClubHeroBanner();
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
    void testGetBannerImageFileReference() {
        setField(newClubHeroBanner, "bannerImageFileReference", "/content/dam/banner.jpg");
        assertEquals("/content/dam/banner.jpg", newClubHeroBanner.getBannerImageFileReference());
    }

    @Test
    void testGetBannerImageAltText() {
        setField(newClubHeroBanner, "bannerImageAltText", "Banner Alt Text");
        assertEquals("Banner Alt Text", newClubHeroBanner.getBannerImageAltText());
    }

    @Test
    void testGetMbBannerImageFileReference() {
        setField(newClubHeroBanner, "mbBannerImageFileReference", "/content/dam/mobile-banner.jpg");
        assertEquals("/content/dam/mobile-banner.jpg", newClubHeroBanner.getMbBannerImageFileReference());
    }

    @Test
    void testGetDisableLazyLoading() {
        setField(newClubHeroBanner, "disableLazyLoading", "true");
        assertEquals("true", newClubHeroBanner.getDisableLazyLoading());
    }

    @Test
    void testGetFetchPriority() {
        setField(newClubHeroBanner, "fetchPriority", "high");
        assertEquals("high", newClubHeroBanner.getFetchPriority());
    }

    @Test
    void testGetBgType() {
        setField(newClubHeroBanner, "bgType", "image");
        assertEquals("image", newClubHeroBanner.getBgType());
    }

    @Test
    void testGetOverlayTitle() {
        setField(newClubHeroBanner, "overlayTitle", "Welcome");
        assertEquals("Welcome", newClubHeroBanner.getOverlayTitle());
    }

    @Test
    void testGetOverlayDescription() {
        setField(newClubHeroBanner, "overlayDescription", "Description text");
        assertEquals("Description text", newClubHeroBanner.getOverlayDescription());
    }

    @Test
    void testGetOverlayImagePath() {
        setField(newClubHeroBanner, "overlayImagePath", "/content/dam/overlay.png");
        assertEquals("/content/dam/overlay.png", newClubHeroBanner.getOverlayImagePath());
    }

    @Test
    void testGetOverlayImageAltText() {
        setField(newClubHeroBanner, "overlayImageAltText", "Overlay Alt");
        assertEquals("Overlay Alt", newClubHeroBanner.getOverlayImageAltText());
    }

    @Test
    void testGetMobileOverlayImagePath() {
        setField(newClubHeroBanner, "mobileOverlayImagePath", "/content/dam/mobile-overlay.png");
        assertEquals("/content/dam/mobile-overlay.png", newClubHeroBanner.getMobileOverlayImagePath());
    }

    @Test
    void testGetVideoUrl() {
        setField(newClubHeroBanner, "videoUrl", "/content/dam/videos/hero.mp4");
        assertEquals("/content/dam/videos/hero.mp4", newClubHeroBanner.getVideoUrl());
    }

    @Test
    void testGetVideoIdWithValidUrl() {
        setField(newClubHeroBanner, "videoUrl", "/content/dam/videos/myvideo.mp4");
        assertEquals("myvideo", newClubHeroBanner.getVideoId());
    }

    @Test
    void testGetVideoIdWithUrlWithoutExtension() {
        setField(newClubHeroBanner, "videoUrl", "/content/dam/videos/myvideo");
        assertEquals("myvideo", newClubHeroBanner.getVideoId());
    }

    @Test
    void testGetVideoIdWithMultipleDots() {
        setField(newClubHeroBanner, "videoUrl", "/content/dam/videos/my.video.file.mp4");
        assertEquals("my.video.file", newClubHeroBanner.getVideoId());
    }

    @Test
    void testGetVideoIdWithNullUrl() {
        setField(newClubHeroBanner, "videoUrl", null);
        // StringUtils.substringBeforeLast(null, ...) returns null
        assertNull(newClubHeroBanner.getVideoId());
    }

    @Test
    void testGetVideoIdWithEmptyUrl() {
        setField(newClubHeroBanner, "videoUrl", "");
        assertEquals("", newClubHeroBanner.getVideoId());
    }

    @Test
    void testGetVideoIdWithJustFilename() {
        setField(newClubHeroBanner, "videoUrl", "video.mp4");
        // StringUtils.substringAfterLast("video.mp4", "/") returns "" since no "/" exists
        // StringUtils.substringBeforeLast("", ".") returns ""
        assertEquals("", newClubHeroBanner.getVideoId());
    }

    @Test
    void testNullValues() {
        assertNull(newClubHeroBanner.getBannerImageFileReference());
        assertNull(newClubHeroBanner.getBannerImageAltText());
        assertNull(newClubHeroBanner.getMbBannerImageFileReference());
        assertNull(newClubHeroBanner.getDisableLazyLoading());
        assertNull(newClubHeroBanner.getFetchPriority());
        assertNull(newClubHeroBanner.getBgType());
        assertNull(newClubHeroBanner.getOverlayTitle());
        assertNull(newClubHeroBanner.getOverlayDescription());
        assertNull(newClubHeroBanner.getOverlayImagePath());
        assertNull(newClubHeroBanner.getOverlayImageAltText());
        assertNull(newClubHeroBanner.getMobileOverlayImagePath());
        assertNull(newClubHeroBanner.getVideoUrl());
    }
}
