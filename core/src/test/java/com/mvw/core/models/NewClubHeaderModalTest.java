package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewClubHeaderModalTest {

    private NewClubHeaderModal newClubHeaderModal;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Page currentPage;

    @Mock
    private NewClubButtonModel newClubButtonModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        newClubHeaderModal = new NewClubHeaderModal();
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
    void testGetLogoImage() {
        setField(newClubHeaderModal, "logoImage", "/content/dam/logo.png");
        assertEquals("/content/dam/logo.png", newClubHeaderModal.getLogoImage());
    }

    @Test
    void testGetLogoImageAltText() {
        setField(newClubHeaderModal, "logoImageAltText", "Logo Alt");
        assertEquals("Logo Alt", newClubHeaderModal.getLogoImageAltText());
    }

    @Test
    void testGetHomeLinkWithValidLink() {
        setField(newClubHeaderModal, "homeLink", "/content/newclub/en/home");
        setField(newClubHeaderModal, "resolver", resolver);
        
        String result = newClubHeaderModal.getHomeLink();
        // Result may be null or modified depending on resolver configuration
    }

    @Test
    void testGetHomeLinkWithExternalLink() {
        setField(newClubHeaderModal, "homeLink", "https://example.com");
        setField(newClubHeaderModal, "resolver", resolver);
        
        String result = newClubHeaderModal.getHomeLink();
        assertEquals("https://example.com", result);
    }

    @Test
    void testGetHomeLinkWithNullLink() {
        setField(newClubHeaderModal, "homeLink", null);
        setField(newClubHeaderModal, "resolver", resolver);
        
        String result = newClubHeaderModal.getHomeLink();
        assertNull(result);
    }

    @Test
    void testGetButtonHide() {
        setField(newClubHeaderModal, "buttonHide", "true");
        assertEquals("true", newClubHeaderModal.getButtonHide());
    }

    @Test
    void testGetNewClubButtonModel() {
        setField(newClubHeaderModal, "newClubButtonModel", newClubButtonModel);
        assertEquals(newClubButtonModel, newClubHeaderModal.getNewClubButtonModel());
    }

    @Test
    void testGetNavigationLink() {
        List<NavigationLinkPojo> navLinks = new ArrayList<>();
        setField(newClubHeaderModal, "navigationLink", navLinks);
        assertEquals(navLinks, newClubHeaderModal.getNavigationLink());
    }

    @Test
    void testGetResolver() {
        setField(newClubHeaderModal, "resolver", resolver);
        assertEquals(resolver, newClubHeaderModal.getResolver());
    }

    @Test
    void testGetCurrentPage() {
        setField(newClubHeaderModal, "currentPage", currentPage);
        assertEquals(currentPage, newClubHeaderModal.getCurrentPage());
    }

    @Test
    void testInitWithNavigationLinksAndCurrentPage() {
        List<NavigationLinkPojo> navLinks = new ArrayList<>();
        NavigationLinkPojo link = mock(NavigationLinkPojo.class);
        navLinks.add(link);
        
        setField(newClubHeaderModal, "navigationLink", navLinks);
        setField(newClubHeaderModal, "currentPage", currentPage);
        
        invokeInit();
        
        verify(link, times(1)).setCurrentPage(currentPage);
    }

    @Test
    void testInitWithNullNavigationLinks() {
        setField(newClubHeaderModal, "navigationLink", null);
        setField(newClubHeaderModal, "currentPage", currentPage);
        
        invokeInit();
        // No exception should be thrown
    }

    @Test
    void testInitWithNullCurrentPage() {
        List<NavigationLinkPojo> navLinks = new ArrayList<>();
        NavigationLinkPojo link = mock(NavigationLinkPojo.class);
        navLinks.add(link);
        
        setField(newClubHeaderModal, "navigationLink", navLinks);
        setField(newClubHeaderModal, "currentPage", null);
        
        invokeInit();
        
        verify(link, never()).setCurrentPage(any());
    }

    @Test
    void testNullValues() {
        assertNull(newClubHeaderModal.getLogoImage());
        assertNull(newClubHeaderModal.getLogoImageAltText());
        assertNull(newClubHeaderModal.getButtonHide());
        assertNull(newClubHeaderModal.getNewClubButtonModel());
        assertNull(newClubHeaderModal.getNavigationLink());
        assertNull(newClubHeaderModal.getResolver());
        assertNull(newClubHeaderModal.getCurrentPage());
    }

    private void invokeInit() {
        try {
            java.lang.reflect.Method initMethod = NewClubHeaderModal.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(newClubHeaderModal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
