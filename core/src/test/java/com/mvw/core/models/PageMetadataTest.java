package com.mvw.core.models;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PageMetadataTest {

    private PageMetadata pageMetadata;
    private SlingHttpServletRequest mockRequest;
    private ResourceResolver mockResolver;
    private PageManager mockPageManager;
    private Page mockPage;
    private Externalizer mockExternalizer;
    private Resource mockResource;
    private ValueMap mockValueMap;

    private static final String PAGE_PATH = "/content/mvw/en/home";
    private static final String OG_IMAGE_PATH = "/content/dam/mvw/og-image.jpg";
    private static final String TWITTER_IMAGE_PATH = "/content/dam/mvw/twitter-image.jpg";
    private static final String EXTERNAL_PAGE_URL = "https://www.marriottvacations.com/content/mvw/en/home";
    private static final String EXTERNAL_OG_IMAGE_URL = "https://www.marriottvacations.com/content/dam/mvw/og-image.jpg";
    private static final String EXTERNAL_TWITTER_IMAGE_URL = "https://www.marriottvacations.com/content/dam/mvw/twitter-image.jpg";
    private static final String FEATURED_IMAGE_PROPERTY = "cq:featuredimage/fileReference";
    private static final String TWITTERCARD_IMAGE_PROPERTY = "cq:twitterimage/fileReference";

    @BeforeEach
    void setUp() {
        pageMetadata = new PageMetadata();
        mockRequest = mock(SlingHttpServletRequest.class);
        mockResolver = mock(ResourceResolver.class);
        mockPageManager = mock(PageManager.class);
        mockPage = mock(Page.class);
        mockExternalizer = mock(Externalizer.class);
        mockResource = mock(Resource.class);
        mockValueMap = mock(ValueMap.class);

        // Set up request mock
        when(mockRequest.getResourceResolver()).thenReturn(mockResolver);
        when(mockRequest.getResource()).thenReturn(mockResource);

        // Set up resolver mock
        when(mockResolver.adaptTo(PageManager.class)).thenReturn(mockPageManager);

        // Set up page manager mock
        when(mockPageManager.getContainingPage(mockResource)).thenReturn(mockPage);

        // Set up page mock
        when(mockPage.getPath()).thenReturn(PAGE_PATH);
        when(mockPage.getProperties()).thenReturn(mockValueMap);

        // Set up value map mock
        when(mockValueMap.get(FEATURED_IMAGE_PROPERTY, String.class)).thenReturn(OG_IMAGE_PATH);
        when(mockValueMap.get(TWITTERCARD_IMAGE_PROPERTY, String.class)).thenReturn(TWITTER_IMAGE_PATH);

        // Set up externalizer mock
        when(mockExternalizer.publishLink(mockResolver, PAGE_PATH)).thenReturn(EXTERNAL_PAGE_URL);
        when(mockExternalizer.publishLink(mockResolver, OG_IMAGE_PATH)).thenReturn(EXTERNAL_OG_IMAGE_URL);
        when(mockExternalizer.publishLink(mockResolver, TWITTER_IMAGE_PATH)).thenReturn(EXTERNAL_TWITTER_IMAGE_URL);

        // Set private fields via reflection
        setField(pageMetadata, "request", mockRequest);
        setField(pageMetadata, "externalizer", mockExternalizer);
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

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = PageMetadata.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(pageMetadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeMockExternalizer() {
        when(mockExternalizer.externalLink(mockResolver, "tmvcs", PAGE_PATH))
            .thenReturn(EXTERNAL_PAGE_URL);

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", OG_IMAGE_PATH))
            .thenReturn(EXTERNAL_OG_IMAGE_URL);

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", TWITTER_IMAGE_PATH))
            .thenReturn(EXTERNAL_TWITTER_IMAGE_URL);
    }

    @Test
    void testInitWithAllProperties() {
        invokeMockExternalizer();
        invokeInit();

        assertEquals(EXTERNAL_PAGE_URL, pageMetadata.getCurrentPageAbsolutePath());
        assertEquals(EXTERNAL_OG_IMAGE_URL, pageMetadata.getOgImageAbsolutePath());
        assertEquals(EXTERNAL_TWITTER_IMAGE_URL, pageMetadata.getTwitterImageAbsolutePath());
    }

    @Test
    void testInitWithNullPageManager() {
        when(mockResolver.adaptTo(PageManager.class)).thenReturn(null);

        invokeInit();

        assertNull(pageMetadata.getCurrentPageAbsolutePath());
        assertNull(pageMetadata.getOgImageAbsolutePath());
        assertNull(pageMetadata.getTwitterImageAbsolutePath());
    }

    @Test
    void testInitWithNullPage() {
        when(mockPageManager.getContainingPage(mockResource)).thenReturn(null);

        invokeInit();

        assertNull(pageMetadata.getCurrentPageAbsolutePath());
        assertNull(pageMetadata.getOgImageAbsolutePath());
        assertNull(pageMetadata.getTwitterImageAbsolutePath());
    }

    @Test
    void testExternalizerExceptionForPagePath() {
        when(mockExternalizer.externalLink(mockResolver, "tmvcs", PAGE_PATH))
            .thenThrow(new RuntimeException("Externalizer error"));

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", OG_IMAGE_PATH))
            .thenReturn(EXTERNAL_OG_IMAGE_URL);

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", TWITTER_IMAGE_PATH))
            .thenReturn(EXTERNAL_TWITTER_IMAGE_URL);

        invokeInit();

        assertNull(pageMetadata.getCurrentPageAbsolutePath());
        assertEquals(EXTERNAL_OG_IMAGE_URL, pageMetadata.getOgImageAbsolutePath());
        assertEquals(EXTERNAL_TWITTER_IMAGE_URL, pageMetadata.getTwitterImageAbsolutePath());
    }

    @Test
    void testExternalizerExceptionForOgImage() {
        when(mockExternalizer.externalLink(mockResolver, "tmvcs", PAGE_PATH))
            .thenReturn(EXTERNAL_PAGE_URL);

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", OG_IMAGE_PATH))
            .thenThrow(new RuntimeException("Externalizer error"));

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", TWITTER_IMAGE_PATH))
            .thenReturn(EXTERNAL_TWITTER_IMAGE_URL);

        invokeInit();

        assertEquals(EXTERNAL_PAGE_URL, pageMetadata.getCurrentPageAbsolutePath());
        assertNull(pageMetadata.getOgImageAbsolutePath());
        assertEquals(EXTERNAL_TWITTER_IMAGE_URL, pageMetadata.getTwitterImageAbsolutePath());
    }

    @Test
    void testExternalizerExceptionForTwitterImage() {
        when(mockExternalizer.externalLink(mockResolver, "tmvcs", PAGE_PATH))
            .thenReturn(EXTERNAL_PAGE_URL);

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", OG_IMAGE_PATH))
            .thenReturn(EXTERNAL_OG_IMAGE_URL);

        when(mockExternalizer.externalLink(mockResolver, "tmvcs", TWITTER_IMAGE_PATH))
            .thenThrow(new RuntimeException("Externalizer error"));

        invokeInit();

        assertEquals(EXTERNAL_PAGE_URL, pageMetadata.getCurrentPageAbsolutePath());
        assertEquals(EXTERNAL_OG_IMAGE_URL, pageMetadata.getOgImageAbsolutePath());
        assertNull(pageMetadata.getTwitterImageAbsolutePath());
    }

    @Test
    void testGettersBeforeInit() {
        // Test getters return null before init is called
        PageMetadata uninitializedModel = new PageMetadata();
        assertNull(uninitializedModel.getCurrentPageAbsolutePath());
        assertNull(uninitializedModel.getOgImageAbsolutePath());
        assertNull(uninitializedModel.getTwitterImageAbsolutePath());
    }
}
