package com.mvw.core.models;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BrandFamilyTest {

    private BrandFamily brandFamily;

    @BeforeEach
    void setUp() {
        brandFamily = new BrandFamily();

        // Headline data
        setField("headlineText", "A FAMILY OF VACATION BRANDS");
        setField("headlineType", "h2");
        setField("secondaryHeadline", "Already an Owner?");

        // Colors and alignment
        setField("copySectionBgColor", "pearl");
        setField("brandIconImageBgColor", "none");
        setField("copyAlignment", "Left");
        setField("copySectionAlignment", "right");

        // Descriptions
        setField("shortDescription", "Owners enjoy access to all the benefits of the brand family.");
        setField("secondaryDescription", "Owners enjoy access to all the benefits of the brand family.");

        // Mock logos
        ResourceResolver resolver = org.mockito.Mockito.mock(ResourceResolver.class);

        LogoItem logo1 = createLogo("Logo_1", "/content/dam/logo1.png", resolver);
        LogoItem logo2 = createLogo("Logo_2", "/content/dam/logo2.png", resolver);

        List<LogoItem> logos = Arrays.asList(logo1, logo2);
        setField("logos", logos);
    }

    // -------------------------------------------------
    // Getter tests
    // -------------------------------------------------

    @Test
    void testBasicGetters() {
        assertEquals("A FAMILY OF VACATION BRANDS", brandFamily.getHeadlineText());
        assertEquals("h2", brandFamily.getHeadlineType());
        assertEquals("Already an Owner?", brandFamily.getSecondaryHeadline());
        assertEquals("pearl", brandFamily.getCopySectionBgColor());
        assertEquals("none", brandFamily.getBrandIconImageBgColor());
        assertEquals("Left", brandFamily.getCopyAlignment());
        assertEquals("right", brandFamily.getCopySectionAlignment());
        assertNotNull(brandFamily.getShortDescription());
        assertNotNull(brandFamily.getSecondaryDescription());
    }

    @Test
    void testLogos() {
        List<LogoItem> logos = brandFamily.getLogos();
        assertEquals(2, logos.size());

        LogoItem logo = logos.get(0);
        assertEquals("Logo_1", logo.getLogoAltText());
        assertEquals("/content/dam/logo1.png", logo.getLogoImage());
        assertEquals("/bin", logo.getLogoCtaUrl());
        assertEquals("sameWindow", logo.getLogoCtaTab());
    }

    // -------------------------------------------------
    // getHeadlineTag() – FULL branch coverage
    // -------------------------------------------------

    @Test
    void testHeadlineTag_WhenHeading1() {
        setField("headlineType", "heading1");
        assertEquals("h1", brandFamily.getHeadlineTag());
    }

    @Test
    void testHeadlineTag_WhenHeading6() {
        setField("headlineType", "heading6");
        assertEquals("h6", brandFamily.getHeadlineTag());
    }

    @Test
    void testHeadlineTag_WhenNonMatchingValue() {
        setField("headlineType", "h2");
        assertEquals("h2", brandFamily.getHeadlineTag());
    }

    @Test
    void testHeadlineTag_WhenNull() {
        setField("headlineType", null);
        assertEquals("h2", brandFamily.getHeadlineTag());
    }

    // -------------------------------------------------
    // Helpers
    // -------------------------------------------------

    private LogoItem createLogo(String alt, String image, ResourceResolver resolver) {
        LogoItem logo = new LogoItem();
        setField(logo, "logoAltText", alt);
        setField(logo, "logoImage", image);
        setField(logo, "logoCtaUrl", "/bin");
        setField(logo, "logoCtaTab", "sameWindow");
        setField(logo, "resolver", resolver);
        return logo;
    }

    private void setField(String fieldName, Object value) {
        setField(brandFamily, fieldName, value);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
