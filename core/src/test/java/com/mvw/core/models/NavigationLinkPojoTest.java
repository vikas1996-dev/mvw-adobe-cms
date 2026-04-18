package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NavigationLinkPojoTest {

    private NavigationLinkPojo navigationLinkPojo;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Page currentPage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        navigationLinkPojo = new NavigationLinkPojo();
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
    void testGetNavLinkText() {
        setField(navigationLinkPojo, "navLinkText", "Home");
        assertEquals("Home", navigationLinkPojo.getNavLinkText());
    }

    @Test
    void testGetNavLinkTextNull() {
        setField(navigationLinkPojo, "navLinkText", null);
        assertNull(navigationLinkPojo.getNavLinkText());
    }

    @Test
    void testGetNavHideLink() {
        setField(navigationLinkPojo, "navHideLink", "true");
        assertEquals("true", navigationLinkPojo.getNavHideLink());
    }

    @Test
    void testGetNavLinkTab() {
        setField(navigationLinkPojo, "navLinkTab", "_blank");
        assertEquals("_blank", navigationLinkPojo.getNavLinkTab());
    }

    @Test
    void testGetThirdParty() {
        setField(navigationLinkPojo, "thirdParty", "true");
        assertEquals("true", navigationLinkPojo.getThirdParty());
    }

    @Test
    void testSetCurrentPage() {
        navigationLinkPojo.setCurrentPage(currentPage);
        // Verify the page was set - will be used in isCurrentPageUrlFlag
    }

    @Test
    void testIsCurrentPageUrlFlagTrue() {
        setField(navigationLinkPojo, "navLinkUrlStaticValue", "/content/mvw/en/home");
        setField(navigationLinkPojo, "currentPage", currentPage);
        
        when(currentPage.getPath()).thenReturn("/content/mvw/en/home");
        
        assertTrue(navigationLinkPojo.isCurrentPageUrlFlag());
    }

    @Test
    void testIsCurrentPageUrlFlagFalse() {
        setField(navigationLinkPojo, "navLinkUrlStaticValue", "/content/mvw/en/home");
        setField(navigationLinkPojo, "currentPage", currentPage);
        
        when(currentPage.getPath()).thenReturn("/content/mvw/en/about");
        
        assertFalse(navigationLinkPojo.isCurrentPageUrlFlag());
    }

    @Test
    void testNullValues() {
        NavigationLinkPojo emptyModel = new NavigationLinkPojo();
        assertNull(emptyModel.getNavLinkText());
        assertNull(emptyModel.getNavHideLink());
        assertNull(emptyModel.getNavLinkTab());
        assertNull(emptyModel.getThirdParty());
    }
}
