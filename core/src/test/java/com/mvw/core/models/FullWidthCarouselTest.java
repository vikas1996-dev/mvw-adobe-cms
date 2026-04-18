package com.mvw.core.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import com.mvw.core.constants.AppConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

class FullWidthCarouselTest {

    private CarouselEnhancementModel carousel;

    @BeforeEach
    void setUp() {
        carousel = new CarouselEnhancementModel();

        // Set all fields
        setField(carousel, "style", "full-width");
        setField(carousel, "headlineText", "DISCOVER DISTINCTIVE VACATION CLUB BRAND");
        setField(carousel, "headlineType", "heading2");
        setField(carousel, "copySectionAlignment", "center");
        setField(carousel, "copyText", "<p>Premium timeshare resorts are just the beginning.</p>");
        setField(carousel, "secondaryCopyText", "<p>Experience luxury vacations</p>");
        setField(carousel, "copyAlignment", "center");
        setField(carousel, "secondaryCopyAlignment", "left");
        setField(carousel, "carouselBgColor", "none");
        setField(carousel, "copySectionBgColor", "pearl");
        setField(carousel, "brandIconImageBgcolor", "indigo");

        // Create carousel slides
        CarouselSlidesPojo slide1 = createSlide("Marriott Vacation Club", "Hawaii, USA",
            "/content/dam/logo1.png", "Learn More", "/content/mvw/home");
        CarouselSlidesPojo slide2 = createSlide("Slide 2", "Location 2", "/content/dam/logo2.png", null, null);

        setField(carousel, "carouselSlide", Arrays.asList(slide1, slide2));
    }

    private CarouselSlidesPojo createSlide(String headline, String badge, String icon, String ctaText, String ctaUrl) {
        CarouselSlidesPojo slide = new CarouselSlidesPojo();

        ResourceResolver mockResolver = mock(ResourceResolver.class);
        // Make resolver.map return same path
        when(mockResolver.map(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Inject resolver
        setField(slide, "resolver", mockResolver);

        // Set other fields
        setField(slide, "carouselHeadline", headline);
        setField(slide, "badge", badge);
        setField(slide, "brandIconImage", icon);
        setField(slide, "ctaText", ctaText);
        setField(slide, "ctaDestination", ctaUrl);
        return slide;
    }

    private void setField(Object target, String name, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ========== Comprehensive Getter Tests ==========

    @Test
    void testAllGetters() {
        // Test all simple getters in one test
        assertEquals("full-width", carousel.getStyle());
        assertEquals("DISCOVER DISTINCTIVE VACATION CLUB BRAND", carousel.getHeadlineText());
        assertEquals("heading2", carousel.getHeadlineType());
        assertEquals("center", carousel.getCopySectionAlignment());
        assertEquals("<p>Premium timeshare resorts are just the beginning.</p>", carousel.getCopyText());
        assertEquals("<p>Experience luxury vacations</p>", carousel.getSecondaryCopyText());
        assertEquals("center", carousel.getCopyAlignment());
        assertEquals("left", carousel.getSecondaryCopyAlignment());
        assertEquals("none", carousel.getCarouselBgColor());
        assertEquals("pearl", carousel.getCopySectionBgColor());
        assertEquals("indigo", carousel.getBrandIconImageBgcolor());

        // Verify carousel slides
        assertNotNull(carousel.getCarouselSlide());
        assertEquals(2, carousel.getCarouselSlide().size());
    }

    @Test
    void testCarouselSlideDetails() {
        List<CarouselSlidesPojo> slides = carousel.getCarouselSlide();

        CarouselSlidesPojo slide1 = slides.get(0);
        assertEquals("Marriott Vacation Club", slide1.getCarouselHeadline());
        assertEquals("Hawaii, USA", slide1.getBadge());
        assertEquals("/content/dam/logo1.png", slide1.getBrandIconImage());
        assertEquals("Learn More", slide1.getCtaText());
        assertEquals("/content/mvw/home".concat(AppConstants.DOT_HTML_EXTENSION), slide1.getCtaDestination());

        CarouselSlidesPojo slide2 = slides.get(1);
        assertEquals("Slide 2", slide2.getCarouselHeadline());
        assertEquals("Location 2", slide2.getBadge());
    }

    // ========== HeadlineTag Logic Tests ==========

    @Test
    void testHeadlineTagValidHeadings() {
        // Test all valid headings (heading2-heading6)
        String[] headings = {"heading2", "heading3", "heading4", "heading5", "heading6"};
        String[] expected = {"h2", "h3", "h4", "h5", "h6"};

        for (int i = 0; i < headings.length; i++) {
            setField(carousel, "headlineType", headings[i]);
            assertEquals(expected[i], carousel.getHeadlineTag(),
                "Failed for " + headings[i]);
        }
    }

    @Test
    void testHeadlineTagHeading1() {
        // heading1 matches pattern but returns h1
        setField(carousel, "headlineType", "heading1");
        assertEquals("h1", carousel.getHeadlineTag());
    }

    @Test
    void testHeadlineTagInvalidCases() {
        // Test all invalid cases that should return default "h2"
        String[] invalidCases = {null, "", "invalid", "h3", "heading7", "HEADING2"};

        for (String invalidCase : invalidCases) {
            setField(carousel, "headlineType", invalidCase);
            assertEquals("h2", carousel.getHeadlineTag(),
                "Failed for input: " + invalidCase);
        }
    }

    // ========== Null Value Tests ==========

    @Test
    void testNullValues() {
        // Test all fields with null values
        setField(carousel, "style", null);
        setField(carousel, "headlineText", null);
        setField(carousel, "headlineType", null);
        setField(carousel, "copySectionAlignment", null);
        setField(carousel, "copyText", null);
        setField(carousel, "secondaryCopyText", null);
        setField(carousel, "copyAlignment", null);
        setField(carousel, "secondaryCopyAlignment", null);
        setField(carousel, "carouselBgColor", null);
        setField(carousel, "copySectionBgColor", null);
        setField(carousel, "brandIconImageBgcolor", null);
        setField(carousel, "carouselSlide", null);

        assertNull(carousel.getStyle());
        assertNull(carousel.getHeadlineText());
        assertNull(carousel.getHeadlineType());
        assertNull(carousel.getCopySectionAlignment());
        assertNull(carousel.getCopyText());
        assertNull(carousel.getSecondaryCopyText());
        assertNull(carousel.getCopyAlignment());
        assertNull(carousel.getSecondaryCopyAlignment());
        assertNull(carousel.getCarouselBgColor());
        assertNull(carousel.getCopySectionBgColor());
        assertNull(carousel.getBrandIconImageBgcolor());
        assertNull(carousel.getCarouselSlide());
        assertEquals("h2", carousel.getHeadlineTag()); // Default when null
    }

    // ========== Empty String Tests ==========

    @Test
    void testEmptyStringValues() {
        setField(carousel, "style", "");
        setField(carousel, "headlineText", "");
        setField(carousel, "copyText", "");
        setField(carousel, "headlineType", "");

        assertEquals("", carousel.getStyle());
        assertEquals("", carousel.getHeadlineText());
        assertEquals("", carousel.getCopyText());
        assertEquals("h2", carousel.getHeadlineTag()); // Empty string returns default
    }

    // ========== Alignment Value Tests ==========

    @Test
    void testAlignmentValues() {
        String[] alignments = {"left", "center", "right"};

        for (String alignment : alignments) {
            setField(carousel, "copySectionAlignment", alignment);
            setField(carousel, "copyAlignment", alignment);
            setField(carousel, "secondaryCopyAlignment", alignment);

            assertEquals(alignment, carousel.getCopySectionAlignment());
            assertEquals(alignment, carousel.getCopyAlignment());
            assertEquals(alignment, carousel.getSecondaryCopyAlignment());
        }
    }

    // ========== Background Color Tests ==========

    @Test
    void testBackgroundColors() {
        String[] colors = {"none", "indigo", "pearl", "teal", "linen"};

        for (String color : colors) {
            setField(carousel, "carouselBgColor", color);
            setField(carousel, "copySectionBgColor", color);
            setField(carousel, "brandIconImageBgcolor", color);

            assertEquals(color, carousel.getCarouselBgColor());
            assertEquals(color, carousel.getCopySectionBgColor());
            assertEquals(color, carousel.getBrandIconImageBgcolor());
        }
    }

    // ========== Style Value Tests ==========

    @Test
    void testStyleValues() {
        String[] styles = {"full-width", "compact", "standard"};

        for (String style : styles) {
            setField(carousel, "style", style);
            assertEquals(style, carousel.getStyle());
        }
    }

    // ========== Edge Cases ==========

    @Test
    void testEmptyCarouselSlideList() {
        setField(carousel, "carouselSlide", Arrays.asList());
        assertNotNull(carousel.getCarouselSlide());
        assertEquals(0, carousel.getCarouselSlide().size());
    }

    @Test
    void testLongTextValues() {
        String longText = "A".repeat(1000);
        setField(carousel, "headlineText", longText);
        setField(carousel, "copyText", longText);

        assertEquals(longText, carousel.getHeadlineText());
        assertEquals(longText, carousel.getCopyText());
    }

    @Test
    void testSpecialCharacters() {
        String specialText = "<script>alert('test')</script>";
        setField(carousel, "copyText", specialText);
        assertEquals(specialText, carousel.getCopyText());
    }

    @Test
    void testHeadlineTypeCase() {
        // Verify that headlineType is case-sensitive
        setField(carousel, "headlineType", "HEADING2");
        assertEquals("h2", carousel.getHeadlineTag()); // Should return default

        setField(carousel, "headlineType", "Heading2");
        assertEquals("h2", carousel.getHeadlineTag()); // Should return default
    }
}