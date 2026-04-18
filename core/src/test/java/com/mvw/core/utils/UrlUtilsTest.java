package com.mvw.core.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;

@ExtendWith(MockitoExtension.class)
class UrlUtilsTest {

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Page page;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetNormalizedUrl_NullResolver() {
        String result = UrlUtils.getNormalizedUrl("/content/test", null);
        assertNull(result);
    }

    @Test
    void testGetNormalizedUrl_NullPath() {
        String result = UrlUtils.getNormalizedUrl((String) null, resolver);
        assertNull(result);
    }

    @Test
    void testGetNormalizedUrl_BlankPath() {
        String result = UrlUtils.getNormalizedUrl("   ", resolver);
        assertNull(result);
    }

    @Test
    void testGetNormalizedUrl_ExternalUrl() {
        String result = UrlUtils.getNormalizedUrl("https://example.com/page", resolver);
        assertEquals("https://example.com/page", result);
    }

    @Test
    void testGetNormalizedUrl_ExternalHttpUrl() {
        String result = UrlUtils.getNormalizedUrl("http://example.com/page", resolver);
        assertEquals("http://example.com/page", result);
    }

    @Test
    void testGetNormalizedUrl_AemContentPath() {
        when(resolver.map("/content/test/page.html")).thenReturn("/test/page.html");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/page", resolver);
        assertEquals("/test/page.html", result);
    }

    @Test
    void testGetNormalizedUrl_AemContentPathWithHtml() {
        when(resolver.map("/content/test/page.html")).thenReturn("/test/page.html");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/page.html", resolver);
        assertEquals("/test/page.html", result);
    }

    @Test
    void testGetNormalizedUrl_NonContentPath() {
        String result = UrlUtils.getNormalizedUrl("/etc/designs/test", resolver);
        assertEquals("/etc/designs/test", result);
    }

    @Test
    void testGetNormalizedUrl_WithQueryString() {
        when(resolver.map("/content/test/page.html?param=value")).thenReturn("/test/page.html?param=value");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/page?param=value", resolver);
        assertEquals("/test/page.html?param=value", result);
    }

    @Test
    void testGetNormalizedUrl_WithFragment() {
        when(resolver.map("/content/test/page.html#section")).thenReturn("/test/page.html#section");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/page#section", resolver);
        assertEquals("/test/page.html#section", result);
    }

    @Test
    void testGetNormalizedUrl_WithQueryAndFragment() {
        when(resolver.map("/content/test/page.html?param=value#section")).thenReturn("/test/page.html?param=value#section");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/page?param=value#section", resolver);
        assertEquals("/test/page.html?param=value#section", result);
    }

    @Test
    void testGetNormalizedUrl_FolderPath() {
        when(resolver.map("/content/test/folder/")).thenReturn("/test/folder/");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/folder/", resolver);
        assertEquals("/test/folder/", result);
    }

    @Test
    void testGetNormalizedUrl_WithPage_NotNull() {
        when(page.getPath()).thenReturn("/content/test/page");
        when(resolver.map("/content/test/page.html")).thenReturn("/test/page.html");
        
        String result = UrlUtils.getNormalizedUrl(page, resolver);
        assertEquals("/test/page.html", result);
    }

    @Test
    void testGetNormalizedUrl_WithPage_Null() {
        String result = UrlUtils.getNormalizedUrl((Page) null, resolver);
        assertNull(result);
    }

    @Test
    void testIsExternalUrl_Https() {
        assertTrue(UrlUtils.isExternalUrl("https://example.com"));
    }

    @Test
    void testIsExternalUrl_Http() {
        assertTrue(UrlUtils.isExternalUrl("http://example.com"));
    }

    @Test
    void testIsExternalUrl_InternalPath() {
        assertFalse(UrlUtils.isExternalUrl("/content/test"));
    }

    @Test
    void testIsExternalUrl_RelativePath() {
        assertFalse(UrlUtils.isExternalUrl("test/path"));
    }

    @Test
    void testIsAemContentPath_ContentPath() {
        assertTrue(UrlUtils.isAemContentPath("/content/test"));
    }

    @Test
    void testIsAemContentPath_NonContentPath() {
        assertFalse(UrlUtils.isAemContentPath("/etc/designs"));
    }

    @Test
    void testIsAemContentPath_EmptyPath() {
        assertFalse(UrlUtils.isAemContentPath(""));
    }

    @Test
    void testGetNormalizedUrl_PathWithExtension() {
        when(resolver.map("/content/test/page.json")).thenReturn("/test/page.json");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/page.json", resolver);
        assertEquals("/test/page.json", result);
    }

    @Test
    void testGetNormalizedUrl_EmptyContentPath() {
        // Path is /content but then empty after - edge case
        when(resolver.map("/content.html")).thenReturn("/content.html");
        
        String result = UrlUtils.getNormalizedUrl("/content", resolver);
        assertEquals("/content.html", result);
    }

    @Test
    void testGetNormalizedUrl_LeadingWhitespace() {
        when(resolver.map("/content/test/page.html")).thenReturn("/test/page.html");
        
        String result = UrlUtils.getNormalizedUrl("  /content/test/page", resolver);
        assertEquals("/test/page.html", result);
    }

    @Test
    void testGetNormalizedUrl_TrailingWhitespace() {
        when(resolver.map("/content/test/page.html")).thenReturn("/test/page.html");
        
        String result = UrlUtils.getNormalizedUrl("/content/test/page  ", resolver);
        assertEquals("/test/page.html", result);
    }
}
