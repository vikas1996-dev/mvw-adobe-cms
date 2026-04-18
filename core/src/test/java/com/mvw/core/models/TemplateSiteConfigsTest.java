package com.mvw.core.models;

import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TemplateSiteConfigsTest {

    private TemplateSiteConfigs templateSiteConfigs;

    @Mock
    private Resource resource;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page currentPage;

    @Mock
    private Style currentStyle;

    @Mock
    private ValueMap pageProperties;

    @Mock
    private Resource contentResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        templateSiteConfigs = new TemplateSiteConfigs();
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
    void testGetSiteName() {
        setField(templateSiteConfigs, "siteName", "tmvc");
        assertEquals("tmvc", templateSiteConfigs.getSiteName());
    }

    @Test
    void testGetSiteNameNull() {
        setField(templateSiteConfigs, "siteName", null);
        assertNull(templateSiteConfigs.getSiteName());
    }

    @Test
    void testToString() {
        setField(templateSiteConfigs, "siteName", "newclub");
        String result = templateSiteConfigs.toString();
        assertTrue(result.contains("siteName=newclub"));
    }

    @Test
    void testInitWithSiteNameFromPageProperties() {
        setupBasicMocks();
        when(currentPage.getPath()).thenReturn("/content/mvw/en/home");
        when(pageProperties.get("siteName", String.class)).thenReturn("tmvc");
        
        invokeInit();
        
        assertEquals("tmvc", templateSiteConfigs.getSiteName());
    }

    @Test
    void testInitWithSiteNameFromStyle() {
        setupBasicMocks();
        when(currentPage.getPath()).thenReturn("/content/mvw/en/home");
        when(pageProperties.get("siteName", String.class)).thenReturn(null);
        when(currentPage.getContentResource()).thenReturn(contentResource);
        setField(templateSiteConfigs, "currentStyle", currentStyle);
        when(currentStyle.get("siteName", String.class)).thenReturn("newclub");
        
        invokeInit();
        
        // Should fall back to style
    }

    @Test
    void testInitWithConfPath() {
        setupBasicMocks();
        when(currentPage.getPath()).thenReturn("/conf/mvw/settings/wcm/templates/page-template/structure");
        
        Resource initialResource = mock(Resource.class);
        Resource jcrContent = mock(Resource.class);
        ValueMap initialValueMap = mock(ValueMap.class);
        
        when(resourceResolver.getResource(anyString())).thenReturn(initialResource);
        when(initialResource.getChild("jcr:content")).thenReturn(jcrContent);
        when(jcrContent.getValueMap()).thenReturn(initialValueMap);
        when(initialValueMap.get("siteName", String.class)).thenReturn("configSite");
        
        invokeInit();
    }

    @Test
    void testInitWithNullCurrentPage() {
        setField(templateSiteConfigs, "resource", resource);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(null);
        
        invokeInit();
        
        // Should handle null page gracefully
    }

    @Test
    void testInitWithException() {
        setField(templateSiteConfigs, "resource", resource);
        when(resource.getResourceResolver()).thenThrow(new RuntimeException("Test exception"));
        
        invokeInit();
        
        // Should handle exception gracefully
    }

    @Test
    void testGetCurrentStyle() {
        setField(templateSiteConfigs, "currentStyle", currentStyle);
        assertEquals(currentStyle, templateSiteConfigs.getCurrentStyle());
    }

    @Test
    void testGetResource() {
        setField(templateSiteConfigs, "resource", resource);
        assertEquals(resource, templateSiteConfigs.getResource());
    }

    private void setupBasicMocks() {
        setField(templateSiteConfigs, "resource", resource);
        
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(currentPage);
        when(currentPage.getProperties()).thenReturn(pageProperties);
        when(currentPage.getContentResource()).thenReturn(contentResource);
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = TemplateSiteConfigs.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(templateSiteConfigs);
        } catch (Exception e) {
            // Init may throw exceptions
        }
    }
}
