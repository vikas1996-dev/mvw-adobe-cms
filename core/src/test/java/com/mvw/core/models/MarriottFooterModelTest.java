package com.mvw.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MarriottFooterModelTest {

    private MarriottFooterModel footerModel;
    private ResourceResolver mockResolver;
    private SlingHttpServletRequest mockRequest;

    private static final String LOGO_URL = "/content/home";
    private static final String LOGO_URL_MAPPED = "/content/home.html";
    private static final String PHONE_NUMBER = "1-800-555-1234";
    private static final String PHONE_NUMBER_ATTRIBUTE = "1-800-555-5678";

    @BeforeEach
    void setUp() {
        footerModel = new MarriottFooterModel();
        mockResolver = mock(ResourceResolver.class);
        mockRequest = mock(SlingHttpServletRequest.class);

        when(mockResolver.map(LOGO_URL_MAPPED)).thenReturn(LOGO_URL_MAPPED);

        // Set basic String fields
        setField(footerModel, "fileReference", "/content/dam/footer-logo.png");
        setField(footerModel, "logoAltText", "Marriott Footer Logo");
        setField(footerModel, "logoURL", LOGO_URL);
        setField(footerModel, "logoURLOpens", "_blank");
        setField(footerModel, "headlineText", "Join Our Newsletter");
        setField(footerModel, "headingText", "Contact Us");
        setField(footerModel, "ctaText4", "Call us at {{phoneNumber}}");
        setField(footerModel, "ctaStyle4", "primary");
        setField(footerModel, "phoneNumber", PHONE_NUMBER);
        setField(footerModel, "hoursTitle", "Hours of Operation");
        setField(footerModel, "hoursDescription", "Mon-Fri: 9am-5pm");
        setField(footerModel, "legalSubFooterCopy", "© 2025 Marriott International");
        setField(footerModel, "appendPhoneNumber", "true");
        setField(footerModel, "resolver", mockResolver);
        setField(footerModel, "request", mockRequest);
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
    void testGetFileReference() {
        assertEquals("/content/dam/footer-logo.png", footerModel.getFileReference());
    }

    @Test
    void testGetLogoAltText() {
        assertEquals("Marriott Footer Logo", footerModel.getLogoAltText());
    }

    @Test
    void testGetLogoURL() {
        assertEquals(LOGO_URL_MAPPED, footerModel.getLogoURL());
    }

    @Test
    void testGetLogoURLOpens() {
        assertEquals("_blank", footerModel.getLogoURLOpens());
    }

    @Test
    void testGetHeadlineText() {
        assertEquals("Join Our Newsletter", footerModel.getHeadlineText());
    }

    @Test
    void testGetHeadingText() {
        assertEquals("Contact Us", footerModel.getHeadingText());
    }

    @Test
    void testGetCtaStyle4() {
        assertEquals("primary", footerModel.getCtaStyle4());
    }

    @Test
    void testGetPhoneNumberWithoutRequestAttribute() {
        when(mockRequest.getAttribute("phoneNumber")).thenReturn(null);
        assertEquals(PHONE_NUMBER, footerModel.getPhoneNumber());
    }

    @Test
    void testGetPhoneNumberWithRequestAttribute() {
        when(mockRequest.getAttribute("phoneNumber")).thenReturn(PHONE_NUMBER_ATTRIBUTE);
        assertEquals(PHONE_NUMBER_ATTRIBUTE, footerModel.getPhoneNumber());
    }

    @Test
    void testGetPhoneNumberWithNullRequest() {
        setField(footerModel, "request", null);
        assertEquals(PHONE_NUMBER, footerModel.getPhoneNumber());
    }

    @Test
    void testGetCtaText4WithPhoneNumberPlaceholder() {
        when(mockRequest.getAttribute("phoneNumber")).thenReturn(PHONE_NUMBER_ATTRIBUTE);
        assertEquals("Call us at " + PHONE_NUMBER_ATTRIBUTE, footerModel.getCtaText4());
    }

    @Test
    void testGetCtaText4WithoutPlaceholder() {
        setField(footerModel, "ctaText4", "Call us now");
        when(mockRequest.getAttribute("phoneNumber")).thenReturn(PHONE_NUMBER_ATTRIBUTE);
        assertEquals("Call us now", footerModel.getCtaText4());
    }

    @Test
    void testGetCtaText4WithNullRequest() {
        setField(footerModel, "request", null);
        assertEquals("Call us at {{phoneNumber}}", footerModel.getCtaText4());
    }

    @Test
    void testGetCtaText4WhenNull() {
        setField(footerModel, "ctaText4", null);
        assertNull(footerModel.getCtaText4());
    }

    @Test
    void testGetHoursTitle() {
        assertEquals("Hours of Operation", footerModel.getHoursTitle());
    }

    @Test
    void testGetHoursDescription() {
        assertEquals("Mon-Fri: 9am-5pm", footerModel.getHoursDescription());
    }

    @Test
    void testGetLegalSubFooterCopy() {
        assertEquals("© 2025 Marriott International", footerModel.getLegalSubFooterCopy());
    }

    @Test
    void testGetAppendPhoneNumber() {
        assertEquals("true", footerModel.getAppendPhoneNumber());
    }

    @Test
    void testGetBrandLogos() {
        MarriottFooterModel.BrandLogo brandLogo = new MarriottFooterModel.BrandLogo();
        setField(brandLogo, "fileReference", "/content/dam/brand-logo.png");
        setField(brandLogo, "logoCTAURL", "/content/brand");
        setField(brandLogo, "logoAltText", "Brand Logo");
        setField(brandLogo, "logoCTAOpens", "_self");
        setField(brandLogo, "ariaLabel", "Visit Brand Page");
        setField(brandLogo, "resolver", mockResolver);

        when(mockResolver.map("/content/brand.html")).thenReturn("/content/brand.html");

        setField(footerModel, "brandLogos", Arrays.asList(brandLogo));

        List<MarriottFooterModel.BrandLogo> brandLogos = footerModel.getBrandLogos();
        assertEquals(1, brandLogos.size());

        MarriottFooterModel.BrandLogo result = brandLogos.get(0);
        assertEquals("/content/dam/brand-logo.png", result.getFileReference());
        assertEquals("/content/brand.html", result.getLogoCTAURL());
        assertEquals("Brand Logo", result.getLogoAltText());
        assertEquals("_self", result.getLogoCTAOpens());
        assertEquals("Visit Brand Page", result.getAriaLabel());
    }

    @Test
    void testGetCta1() {
        MarriottFooterModel.CTA cta = new MarriottFooterModel.CTA();
        setField(cta, "ctaText", "Learn More");
        setField(cta, "ctaURL", "/content/learn-more");
        setField(cta, "ctaOpens", "_self");
        setField(cta, "showNewTabIcon", "false");
        setField(cta, "thirdParty", "false");
        setField(cta, "resolver", mockResolver);

        when(mockResolver.map("/content/learn-more.html")).thenReturn("/content/learn-more.html");

        setField(footerModel, "cta1", Arrays.asList(cta));

        List<MarriottFooterModel.CTA> cta1List = footerModel.getCta1();
        assertEquals(1, cta1List.size());

        MarriottFooterModel.CTA result = cta1List.get(0);
        assertEquals("Learn More", result.getCtaText());
        assertEquals("/content/learn-more.html", result.getCtaURL());
        assertEquals("_self", result.getCtaOpens());
        assertEquals("false", result.getShowNewTabIcon());
        assertEquals("false", result.getThirdParty());
    }

    @Test
    void testGetCta2() {
        MarriottFooterModel.CTA cta = new MarriottFooterModel.CTA();
        setField(cta, "ctaText", "Contact Support");
        setField(cta, "ctaURL", "/content/support");
        setField(cta, "ctaOpens", "_blank");
        setField(cta, "showNewTabIcon", "true");
        setField(cta, "thirdParty", "true");
        setField(cta, "resolver", mockResolver);

        when(mockResolver.map("/content/support.html")).thenReturn("/content/support.html");

        setField(footerModel, "cta2", Arrays.asList(cta));

        List<MarriottFooterModel.CTA> cta2List = footerModel.getCta2();
        assertEquals(1, cta2List.size());

        MarriottFooterModel.CTA result = cta2List.get(0);
        assertEquals("Contact Support", result.getCtaText());
        assertEquals("/content/support.html", result.getCtaURL());
        assertEquals("_blank", result.getCtaOpens());
        assertEquals("true", result.getShowNewTabIcon());
        assertEquals("true", result.getThirdParty());
    }

    @Test
    void testGetCta3WithSocialMedia() {
        MarriottFooterModel.newCTA newCta = new MarriottFooterModel.newCTA();
        setField(newCta, "ctaText", "Follow Us");
        setField(newCta, "ctaURL", "/content/follow");
        setField(newCta, "ctaOpens", "_blank");
        setField(newCta, "facebook", "true");
        setField(newCta, "instagram", "true");
        setField(newCta, "youtube", "true");
        setField(newCta, "pinterest", "false");
        setField(newCta, "x", "true");
        setField(newCta, "linkedIn", "true");
        setField(newCta, "tiktok", "false");
        setField(newCta, "fbURL", "https://facebook.com/marriott");
        setField(newCta, "instaURL", "https://instagram.com/marriott");
        setField(newCta, "ytURL", "https://youtube.com/marriott");
        setField(newCta, "ttURL", "https://tiktok.com/@marriott");
        setField(newCta, "piURL", "https://pinterest.com/marriott");
        setField(newCta, "xURL", "https://x.com/marriott");
        setField(newCta, "liURL", "https://linkedin.com/company/marriott");
        setField(newCta, "showNewTabIcon", "true");
        setField(newCta, "thirdParty", "true");
        setField(newCta, "fbCtaTab", "_blank");
        setField(newCta, "fbShowNewTabIcon", "true");
        setField(newCta, "fbThirdParty", "true");
        setField(newCta, "instaCtaTab", "_blank");
        setField(newCta, "instaShowNewTabIcon", "true");
        setField(newCta, "instaThirdParty", "true");
        setField(newCta, "ytCtaTab", "_blank");
        setField(newCta, "ytShowNewTabIcon", "true");
        setField(newCta, "ytThirdParty", "true");
        setField(newCta, "ttCtaTab", "_blank");
        setField(newCta, "ttShowNewTabIcon", "true");
        setField(newCta, "ttThirdParty", "true");
        setField(newCta, "piCtaTab", "_blank");
        setField(newCta, "piShowNewTabIcon", "true");
        setField(newCta, "piThirdParty", "true");
        setField(newCta, "xCtaTab", "_blank");
        setField(newCta, "xShowNewTabIcon", "true");
        setField(newCta, "xThirdParty", "true");
        setField(newCta, "liCtaTab", "_blank");
        setField(newCta, "liShowNewTabIcon", "true");
        setField(newCta, "liThirdParty", "true");
        setField(newCta, "resolver", mockResolver);

        when(mockResolver.map("/content/follow.html")).thenReturn("/content/follow.html");

        setField(footerModel, "cta3", Arrays.asList(newCta));

        List<MarriottFooterModel.newCTA> cta3List = footerModel.getCta3();
        assertEquals(1, cta3List.size());

        MarriottFooterModel.newCTA result = cta3List.get(0);
        assertEquals("Follow Us", result.getCtaText());
        assertEquals("/content/follow.html", result.getCtaURL());
        assertEquals("_blank", result.getCtaOpens());
        assertEquals("true", result.isFacebook());
        assertEquals("true", result.isInstagram());
        assertEquals("true", result.isYoutube());
        assertEquals("false", result.isPinterest());
        assertEquals("true", result.isX());
        assertEquals("true", result.isLinkedIn());
        assertEquals("false", result.isTiktok());
        assertEquals("https://facebook.com/marriott", result.getFbURL());
        assertEquals("https://instagram.com/marriott", result.getInstaURL());
        assertEquals("https://youtube.com/marriott", result.getYtURL());
        assertEquals("https://tiktok.com/@marriott", result.getTtURL());
        assertEquals("https://pinterest.com/marriott", result.getPiURL());
        assertEquals("https://x.com/marriott", result.getXURL());
        assertEquals("https://linkedin.com/company/marriott", result.getLiURL());
        assertEquals("true", result.getShowNewTabIcon());
        assertEquals("true", result.getThirdParty());
        assertEquals("_blank", result.getFbCtaTab());
        assertEquals("true", result.getFbShowNewTabIcon());
        assertEquals("true", result.getFbThirdParty());
        assertEquals("_blank", result.getInstaCtaTab());
        assertEquals("true", result.getInstaShowNewTabIcon());
        assertEquals("true", result.getInstaThirdParty());
        assertEquals("_blank", result.getYtCtaTab());
        assertEquals("true", result.getYtShowNewTabIcon());
        assertEquals("true", result.getYtThirdParty());
        assertEquals("_blank", result.getTtCtaTab());
        assertEquals("true", result.getTtShowNewTabIcon());
        assertEquals("true", result.getTtThirdParty());
        assertEquals("_blank", result.getPiCtaTab());
        assertEquals("true", result.getPiShowNewTabIcon());
        assertEquals("true", result.getPiThirdParty());
        assertEquals("_blank", result.getXCtaTab());
        assertEquals("true", result.getXShowNewTabIcon());
        assertEquals("true", result.getXThirdParty());
        assertEquals("_blank", result.getLiCtaTab());
        assertEquals("true", result.getLiShowNewTabIcon());
        assertEquals("true", result.getLiThirdParty());
    }

    @Test
    void testGetLegalLinks() {
        MarriottFooterModel.legalLink legal = new MarriottFooterModel.legalLink();
        setField(legal, "ctaText", "Privacy Policy");
        setField(legal, "ctaURL", "/content/privacy");
        setField(legal, "ctaStyles", "link-style");
        setField(legal, "ctaOpens", "_blank");
        setField(legal, "fileReference", "/content/dam/privacy-icon.png");
        setField(legal, "iconNewAlt", "Privacy Icon");
        setField(legal, "showNewTabIcon", "true");
        setField(legal, "iconUrl", "/content/dam/icon.png");
        setField(legal, "thirdPartylink", true);
        setField(legal, "cssClass", "privacy-link");
        setField(legal, "resolver", mockResolver);

        when(mockResolver.map("/content/privacy.html")).thenReturn("/content/privacy.html");
        when(mockResolver.map("/content/dam/icon.png")).thenReturn("/content/dam/icon.png");

        setField(footerModel, "legalLinks", Arrays.asList(legal));

        List<MarriottFooterModel.legalLink> legalLinks = footerModel.getLegalLinks();
        assertEquals(1, legalLinks.size());

        MarriottFooterModel.legalLink result = legalLinks.get(0);
        assertEquals("Privacy Policy", result.getCtaText());
        assertEquals("/content/privacy.html", result.getCtaURL());
        assertEquals("link-style", result.getCtaStyles());
        assertEquals("_blank", result.getCtaOpens());
        assertEquals("/content/dam/privacy-icon.png", result.getFileReference());
        assertEquals("Privacy Icon", result.getIconNewAlt());
        assertEquals("true", result.getShowNewTabIcon());
        assertEquals("/content/dam/icon.png", result.getIconUrl());
        assertTrue(result.isThirdPartylink());
        assertEquals("privacy-link", result.getCssClass());
    }

    @Test
    void testGetLegalLinksWithMultipleLinks() {
        MarriottFooterModel.legalLink privacyLink = new MarriottFooterModel.legalLink();
        setField(privacyLink, "ctaText", "Privacy Policy");
        setField(privacyLink, "ctaURL", "/content/privacy");
        setField(privacyLink, "resolver", mockResolver);

        MarriottFooterModel.legalLink termsLink = new MarriottFooterModel.legalLink();
        setField(termsLink, "ctaText", "Terms of Service");
        setField(termsLink, "ctaURL", "/content/terms");
        setField(termsLink, "resolver", mockResolver);

        when(mockResolver.map("/content/privacy.html")).thenReturn("/content/privacy.html");
        when(mockResolver.map("/content/terms.html")).thenReturn("/content/terms.html");

        setField(footerModel, "legalLinks", Arrays.asList(privacyLink, termsLink));

        List<MarriottFooterModel.legalLink> legalLinks = footerModel.getLegalLinks();
        assertEquals(2, legalLinks.size());
        assertEquals("Privacy Policy", legalLinks.get(0).getCtaText());
        assertEquals("Terms of Service", legalLinks.get(1).getCtaText());
    }

    @Test
    void testBrandLogosEmptyList() {
        setField(footerModel, "brandLogos", null);
        assertNull(footerModel.getBrandLogos());
    }

    @Test
    void testCta1EmptyList() {
        setField(footerModel, "cta1", null);
        assertNull(footerModel.getCta1());
    }

    @Test
    void testCta2EmptyList() {
        setField(footerModel, "cta2", null);
        assertNull(footerModel.getCta2());
    }

    @Test
    void testCta3EmptyList() {
        setField(footerModel, "cta3", null);
        assertNull(footerModel.getCta3());
    }

    @Test
    void testLegalLinksEmptyList() {
        setField(footerModel, "legalLinks", null);
        assertNull(footerModel.getLegalLinks());
    }

    @Test
    void testBrandLogoWithExternalUrl() {
        MarriottFooterModel.BrandLogo brandLogo = new MarriottFooterModel.BrandLogo();
        setField(brandLogo, "logoCTAURL", "https://external-site.com/brand");
        setField(brandLogo, "resolver", mockResolver);

        assertEquals("https://external-site.com/brand", brandLogo.getLogoCTAURL());
    }

    @Test
    void testCtaWithExternalUrl() {
        MarriottFooterModel.CTA cta = new MarriottFooterModel.CTA();
        setField(cta, "ctaURL", "https://external-site.com/page");
        setField(cta, "resolver", mockResolver);

        assertEquals("https://external-site.com/page", cta.getCtaURL());
    }

    @Test
    void testNewCtaWithExternalUrl() {
        MarriottFooterModel.newCTA newCta = new MarriottFooterModel.newCTA();
        setField(newCta, "ctaURL", "https://external-site.com/social");
        setField(newCta, "resolver", mockResolver);

        assertEquals("https://external-site.com/social", newCta.getCtaURL());
    }

    @Test
    void testLegalLinkWithExternalUrl() {
        MarriottFooterModel.legalLink legal = new MarriottFooterModel.legalLink();
        setField(legal, "ctaURL", "https://legal-external.com/terms");
        setField(legal, "resolver", mockResolver);

        assertEquals("https://legal-external.com/terms", legal.getCtaURL());
    }

    @Test
    void testLegalLinkThirdPartyFalse() {
        MarriottFooterModel.legalLink legal = new MarriottFooterModel.legalLink();
        setField(legal, "thirdPartylink", false);
        setField(legal, "resolver", mockResolver);

        assertFalse(legal.isThirdPartylink());
    }

    @Test
    void testGetLogoURLWithNullResolver() {
        setField(footerModel, "resolver", null);
        assertNull(footerModel.getLogoURL());
    }

    @Test
    void testMultipleBrandLogos() {
        MarriottFooterModel.BrandLogo brandLogo1 = new MarriottFooterModel.BrandLogo();
        setField(brandLogo1, "fileReference", "/content/dam/brand1.png");
        setField(brandLogo1, "logoAltText", "Brand 1");
        setField(brandLogo1, "resolver", mockResolver);

        MarriottFooterModel.BrandLogo brandLogo2 = new MarriottFooterModel.BrandLogo();
        setField(brandLogo2, "fileReference", "/content/dam/brand2.png");
        setField(brandLogo2, "logoAltText", "Brand 2");
        setField(brandLogo2, "resolver", mockResolver);

        MarriottFooterModel.BrandLogo brandLogo3 = new MarriottFooterModel.BrandLogo();
        setField(brandLogo3, "fileReference", "/content/dam/brand3.png");
        setField(brandLogo3, "logoAltText", "Brand 3");
        setField(brandLogo3, "resolver", mockResolver);

        setField(footerModel, "brandLogos", Arrays.asList(brandLogo1, brandLogo2, brandLogo3));

        List<MarriottFooterModel.BrandLogo> brandLogos = footerModel.getBrandLogos();
        assertEquals(3, brandLogos.size());
        assertEquals("Brand 1", brandLogos.get(0).getLogoAltText());
        assertEquals("Brand 2", brandLogos.get(1).getLogoAltText());
        assertEquals("Brand 3", brandLogos.get(2).getLogoAltText());
    }
    @Test
    void testLegalLinkWhenCookieSettingDisabled() {
        MarriottFooterModel.legalLink legal = new MarriottFooterModel.legalLink();
        setField(legal, "ctaURL", "/content/privacy");
        setField(legal, "ctaOpens", "_blank");
        setField(legal, "enableCookieSetting", false);
        setField(legal, "resolver", mockResolver);

        when(mockResolver.map("/content/privacy.html"))
                .thenReturn("/content/privacy.html");

        assertEquals("/content/privacy.html", legal.getCtaURL());
        assertEquals("_blank", legal.getCtaOpens());
    }
    @Test
    void testLegalLinkWhenCookieSettingEnabled() {
        MarriottFooterModel.legalLink legal = new MarriottFooterModel.legalLink();
        setField(legal, "ctaURL", "/content/privacy");
        setField(legal, "ctaOpens", "_blank");
        setField(legal, "enableCookieSetting", true);
        setField(legal, "resolver", mockResolver);

        assertEquals("#", legal.getCtaURL());
        assertNull(legal.getCtaOpens());
    }
}
