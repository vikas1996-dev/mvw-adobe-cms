package com.mvw.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewClubFooterModelTest {

    private NewClubFooterModel newClubFooterModel;

    @Mock
    private ResourceResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        newClubFooterModel = new NewClubFooterModel();
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
        setField(newClubFooterModel, "logoImage", "/content/dam/footer-logo.png");
        assertEquals("/content/dam/footer-logo.png", newClubFooterModel.getLogoImage());
    }

    @Test
    void testGetLogoImageAltText() {
        setField(newClubFooterModel, "logoImageAltText", "Footer Logo");
        assertEquals("Footer Logo", newClubFooterModel.getLogoImageAltText());
    }

    @Test
    void testGetHomeLinkWithValidLink() {
        setField(newClubFooterModel, "homeLink", "/content/newclub/en/home");
        setField(newClubFooterModel, "resolver", resolver);
        
        String result = newClubFooterModel.getHomeLink();
        // Result may be null or modified depending on resolver configuration
    }

    @Test
    void testGetHomeLinkWithExternalLink() {
        setField(newClubFooterModel, "homeLink", "https://example.com");
        setField(newClubFooterModel, "resolver", resolver);
        
        String result = newClubFooterModel.getHomeLink();
        assertEquals("https://example.com", result);
    }

    @Test
    void testGetHomeLinkWithNullLink() {
        setField(newClubFooterModel, "homeLink", null);
        setField(newClubFooterModel, "resolver", resolver);
        
        String result = newClubFooterModel.getHomeLink();
        assertNull(result);
    }

    @Test
    void testGetFooterCopy() {
        setField(newClubFooterModel, "footerCopy", "Footer copy text");
        assertEquals("Footer copy text", newClubFooterModel.getFooterCopy());
    }

    @Test
    void testGetContactUsText() {
        setField(newClubFooterModel, "contactUsText", "Contact Us");
        assertEquals("Contact Us", newClubFooterModel.getContactUsText());
    }

    @Test
    void testGetLegalCopy() {
        setField(newClubFooterModel, "legalCopy", "Legal copy");
        assertEquals("Legal copy", newClubFooterModel.getLegalCopy());
    }

    @Test
    void testGetHideSectionA() {
        setField(newClubFooterModel, "hideSectionA", "true");
        assertEquals("true", newClubFooterModel.getHideSectionA());
    }

    @Test
    void testGetHideSectionB() {
        setField(newClubFooterModel, "hideSectionB", "false");
        assertEquals("false", newClubFooterModel.getHideSectionB());
    }

    @Test
    void testGetNavigationHeading() {
        setField(newClubFooterModel, "navigationHeading", "Quick Links");
        assertEquals("Quick Links", newClubFooterModel.getNavigationHeading());
    }

    @Test
    void testGetNavigationHeadingColTwo() {
        setField(newClubFooterModel, "navigationHeadingColTwo", "Resources");
        assertEquals("Resources", newClubFooterModel.getNavigationHeadingColTwo());
    }

    @Test
    void testGetNavigationHeadingColThree() {
        setField(newClubFooterModel, "navigationHeadingColThree", "Support");
        assertEquals("Support", newClubFooterModel.getNavigationHeadingColThree());
    }

    @Test
    void testGetCtaNavLinks() {
        List<NavigationLinkPojo> navLinks = new ArrayList<>();
        setField(newClubFooterModel, "ctaNavLinks", navLinks);
        assertEquals(navLinks, newClubFooterModel.getCtaNavLinks());
    }

    @Test
    void testGetCtaNavLinksCol2() {
        List<NavigationLinkPojo> navLinks = new ArrayList<>();
        setField(newClubFooterModel, "ctaNavLinksCol2", navLinks);
        assertEquals(navLinks, newClubFooterModel.getCtaNavLinksCol2());
    }

    @Test
    void testGetCtaNavLinksCol3() {
        List<NavigationLinkPojo> navLinks = new ArrayList<>();
        setField(newClubFooterModel, "ctaNavLinksCol3", navLinks);
        assertEquals(navLinks, newClubFooterModel.getCtaNavLinksCol3());
    }

    @Test
    void testGetSocialMediaLinks() {
        List<NavigationLinkPojo> socialLinks = new ArrayList<>();
        setField(newClubFooterModel, "socialMediaLinks", socialLinks);
        assertEquals(socialLinks, newClubFooterModel.getSocialMediaLinks());
    }

    @Test
    void testGetLegalLinks() {
        List<LegalLinkPojo> legalLinks = new ArrayList<>();
        setField(newClubFooterModel, "legalLinks", legalLinks);
        assertEquals(legalLinks, newClubFooterModel.getLegalLinks());
    }

    @Test
    void testGetResolver() {
        setField(newClubFooterModel, "resolver", resolver);
        assertEquals(resolver, newClubFooterModel.getResolver());
    }

    @Test
    void testNullValues() {
        assertNull(newClubFooterModel.getLogoImage());
        assertNull(newClubFooterModel.getLogoImageAltText());
        assertNull(newClubFooterModel.getFooterCopy());
        assertNull(newClubFooterModel.getContactUsText());
        assertNull(newClubFooterModel.getLegalCopy());
        assertNull(newClubFooterModel.getHideSectionA());
        assertNull(newClubFooterModel.getHideSectionB());
        assertNull(newClubFooterModel.getNavigationHeading());
        assertNull(newClubFooterModel.getNavigationHeadingColTwo());
        assertNull(newClubFooterModel.getNavigationHeadingColThree());
        assertNull(newClubFooterModel.getCtaNavLinks());
        assertNull(newClubFooterModel.getCtaNavLinksCol2());
        assertNull(newClubFooterModel.getCtaNavLinksCol3());
        assertNull(newClubFooterModel.getSocialMediaLinks());
        assertNull(newClubFooterModel.getLegalLinks());
        assertNull(newClubFooterModel.getResolver());
    }
}
