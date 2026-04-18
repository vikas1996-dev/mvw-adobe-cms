package com.mvw.core.models;

import com.mvw.core.constants.AppConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CTAItemTest {

    private CTAItem ctaItem;

    @Mock
    private SlingHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ctaItem = new CTAItem();

        ResourceResolver mockResolver = mock(ResourceResolver.class);
        // Make resolver.map return same path
        when(mockResolver.map(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Inject resolver
        setField(ctaItem, "resolver", mockResolver);
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
    void testGetCtaTextWithoutPlaceholder() {
        setField(ctaItem, "ctaText", "Click Here");
        assertEquals("Click Here", ctaItem.getCtaText());
    }

    @Test
    void testGetCtaTextWithPlaceholderAndRequest() {
        setField(ctaItem, "ctaText", "Call {{phoneNumber}}");
        setField(ctaItem, "request", request);
        when(request.getAttribute("phoneNumber")).thenReturn("1-800-555-1234");
        
        assertEquals("Call 1-800-555-1234", ctaItem.getCtaText());
    }

    @Test
    void testGetCtaTextWithPlaceholderAndNullPhoneNumber() {
        setField(ctaItem, "ctaText", "Call {{phoneNumber}}");
        setField(ctaItem, "request", request);
        when(request.getAttribute("phoneNumber")).thenReturn(null);
        
        assertEquals("Call {{phoneNumber}}", ctaItem.getCtaText());
    }

    @Test
    void testGetCtaTextWithNullRequest() {
        setField(ctaItem, "ctaText", "Call {{phoneNumber}}");
        setField(ctaItem, "request", null);
        
        assertEquals("Call {{phoneNumber}}", ctaItem.getCtaText());
    }

    @Test
    void testGetCtaUrl() {
        setField(ctaItem, "ctaUrl", "/content/mvw/en/book");
        assertEquals("/content/mvw/en/book".concat(AppConstants.DOT_HTML_EXTENSION), ctaItem.getCtaUrl());
    }

    @Test
    void testGetCtaType() {
        setField(ctaItem, "ctaType", "button");
        assertEquals("button", ctaItem.getCtaType());
    }

    @Test
    void testGetVideoUrl() {
        setField(ctaItem, "videoUrl", "/content/dam/videos/promo.mp4");
        assertEquals("/content/dam/videos/promo.mp4", ctaItem.getVideoUrl());
    }

    @Test
    void testGetPhoneNumberWithRequestAttribute() {
        setField(ctaItem, "phoneNumber", "default-phone");
        setField(ctaItem, "request", request);
        when(request.getAttribute("phoneNumber")).thenReturn("1-800-555-5678");
        
        assertEquals("1-800-555-5678", ctaItem.getPhoneNumber());
    }

    @Test
    void testGetPhoneNumberWithNullRequestAttribute() {
        setField(ctaItem, "phoneNumber", "default-phone");
        setField(ctaItem, "request", request);
        when(request.getAttribute("phoneNumber")).thenReturn(null);
        
        assertEquals("default-phone", ctaItem.getPhoneNumber());
    }

    @Test
    void testGetPhoneNumberWithNullRequest() {
        setField(ctaItem, "phoneNumber", "default-phone");
        setField(ctaItem, "request", null);
        
        assertEquals("default-phone", ctaItem.getPhoneNumber());
    }

    @Test
    void testGetCtaTextUnder() {
        setField(ctaItem, "ctaTextUnder", "Additional text");
        assertEquals("Additional text", ctaItem.getCtaTextUnder());
    }

    @Test
    void testGetVideoID() {
        setField(ctaItem, "videoID", "video123");
        assertEquals("video123", ctaItem.getVideoID());
    }

    @Test
    void testGetCtaStyle() {
        setField(ctaItem, "ctaStyle", "primary");
        assertEquals("primary", ctaItem.getCtaStyle());
    }

    @Test
    void testGetCtaPlacement() {
        setField(ctaItem, "ctaPlacement", "center");
        assertEquals("center", ctaItem.getCtaPlacement());
    }

    @Test
    void testGetCtaSize() {
        setField(ctaItem, "ctaSize", "large");
        assertEquals("large", ctaItem.getCtaSize());
    }

    @Test
    void testGetCtaOpensIn() {
        setField(ctaItem, "ctaOpensIn", "_blank");
        assertEquals("_blank", ctaItem.getCtaOpensIn());
    }

    @Test
    void testGetCtaAdditionalText() {
        setField(ctaItem, "ctaAdditionalText", "Limited time offer");
        assertEquals("Limited time offer", ctaItem.getCtaAdditionalText());
    }

    @Test
    void testGetFontAwesomeIcon() {
        setField(ctaItem, "fontAwesomeIcon", "fa-arrow-right");
        assertEquals("fa-arrow-right", ctaItem.getFontAwesomeIcon());
    }

    @Test
    void testGetThirdPartyLink() {
        setField(ctaItem, "thirdPartyLink", "true");
        assertEquals("true", ctaItem.getThirdPartyLink());
    }

    @Test
    void testGetAppendPhoneNumber() {
        setField(ctaItem, "appendPhoneNumber", "true");
        assertEquals("true", ctaItem.getAppendPhoneNumber());
    }

    @Test
    void testGetExtractedVideoID() {
        setField(ctaItem, "extractedVideoID", "extracted123");
        assertEquals("extracted123", ctaItem.getExtractedVideoID());
    }

    @Test
    void testInitWithVideoUrl() {
        setField(ctaItem, "videoUrl", "/content/dam/videos/promo-video.mp4");
        setField(ctaItem, "videoID", null);
        
        invokeInit();
        
        assertEquals("promo-video", ctaItem.getVideoID());
        assertEquals("promo-video", ctaItem.getExtractedVideoID());
    }

    @Test
    void testInitWithVideoUrlAndExistingVideoID() {
        setField(ctaItem, "videoUrl", "/content/dam/videos/promo-video.mp4");
        setField(ctaItem, "videoID", "manual-id");
        
        invokeInit();
        
        assertEquals("manual-id", ctaItem.getVideoID());
    }

    @Test
    void testInitWithNullVideoUrl() {
        setField(ctaItem, "videoUrl", null);
        setField(ctaItem, "videoID", "test-id");
        
        invokeInit();
        
        assertEquals("test-id", ctaItem.getVideoID());
        assertNull(ctaItem.getExtractedVideoID());
    }

    @Test
    void testInitWithEmptyVideoUrl() {
        setField(ctaItem, "videoUrl", "");
        setField(ctaItem, "videoID", "test-id");
        
        invokeInit();
        
        assertEquals("test-id", ctaItem.getVideoID());
    }

    @Test
    void testInitWithVideoUrlNoExtension() {
        setField(ctaItem, "videoUrl", "/content/dam/videos/promo-video");
        setField(ctaItem, "videoID", null);
        
        invokeInit();
        
        assertEquals("promo-video", ctaItem.getVideoID());
    }

    @Test
    void testInitWithVideoUrlMultipleDots() {
        setField(ctaItem, "videoUrl", "/content/dam/videos/promo.video.mp4");
        setField(ctaItem, "videoID", null);
        
        invokeInit();
        
        assertEquals("promo.video", ctaItem.getVideoID());
    }

    @Test
    void testNullValues() {
        CTAItem emptyModel = new CTAItem();
        assertNull(emptyModel.getCtaUrl());
        assertNull(emptyModel.getCtaType());
        assertNull(emptyModel.getVideoUrl());
        assertNull(emptyModel.getCtaTextUnder());
        assertNull(emptyModel.getCtaStyle());
        assertNull(emptyModel.getCtaPlacement());
        assertNull(emptyModel.getCtaSize());
        assertNull(emptyModel.getCtaOpensIn());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = CTAItem.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(ctaItem);
        } catch (Exception e) {
            // Init may throw exceptions
        }
    }
}
