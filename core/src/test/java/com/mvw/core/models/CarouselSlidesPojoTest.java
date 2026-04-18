package com.mvw.core.models;

import com.mvw.core.constants.AppConstants;
import com.mvw.core.utils.UrlUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CarouselSlidesPojoTest {

    private CarouselSlidesPojo carouselSlide;

    @Mock
    private ResourceResolver resolver;

    @BeforeEach
    void setUp() {
        carouselSlide = new CarouselSlidesPojo();
        setField(carouselSlide, "resolver", resolver);
        when(resolver.map(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        setField(carouselSlide, "carouselHeadline", "Amazing Resort");
        setField(carouselSlide, "carouselHeadlineType", "heading3");
        setField(carouselSlide, "resortName", "Marriott Resort & Spa");
        setField(carouselSlide, "feature1FontAwesomeIcon", "fa-swimming-pool");
        setField(carouselSlide, "feature1Label", "Pool");
        setField(carouselSlide, "feature2FontAwesomeIcon", "fa-utensils");
        setField(carouselSlide, "feature2Label", "Restaurant");
        setField(carouselSlide, "carouselImage", "/content/dam/mvw/carousel.jpg");
        setField(carouselSlide, "mobileImage", "/content/dam/mvw/carousel-mobile.jpg");
        setField(carouselSlide, "carouselImageAlt", "Resort View");
        setField(carouselSlide, "badge", "New");
        setField(carouselSlide, "badgeColor", "#FF0000");
        setField(carouselSlide, "badgeFontAwesomeIcon", "fa-star");
        setField(carouselSlide, "brandIconImage", "/content/dam/mvw/brand-icon.png");
        setField(carouselSlide, "brandIconImageAlt", "Brand Icon");
        setField(carouselSlide, "ctaText", "Book Now");
        setField(carouselSlide, "ctaDestination", "/content/mvw/booking");
        setField(carouselSlide, "ctaTab", "newTab");
        setField(carouselSlide, "ctaStyle", "Primary");
        setField(carouselSlide, "ctaPlacement", "bottom");
        setField(carouselSlide, "fontAwesomeIcon", "fa-arrow-right");
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
    void testGetCarouselHeadline() {
        assertEquals("Amazing Resort", carouselSlide.getCarouselHeadline());
    }

    @Test
    void testGetCarouselHeadlineType() {
        assertEquals("heading3", carouselSlide.getCarouselHeadlineType());
    }

    @Test
    void testGetResortName() {
        assertEquals("Marriott Resort & Spa", carouselSlide.getResortName());
    }

    @Test
    void testGetFeature1FontAwesomeIcon() {
        assertEquals("fa-swimming-pool", carouselSlide.getFeature1FontAwesomeIcon());
    }

    @Test
    void testGetFeature1Label() {
        assertEquals("Pool", carouselSlide.getFeature1Label());
    }

    @Test
    void testGetFeature2FontAwesomeIcon() {
        assertEquals("fa-utensils", carouselSlide.getFeature2FontAwesomeIcon());
    }

    @Test
    void testGetFeature2Label() {
        assertEquals("Restaurant", carouselSlide.getFeature2Label());
    }

    @Test
    void testGetCarouselImage() {
        assertEquals("/content/dam/mvw/carousel.jpg", carouselSlide.getCarouselImage());
    }

    @Test
    void testGetMobileImage() {
        assertEquals("/content/dam/mvw/carousel-mobile.jpg", carouselSlide.getMobileImage());
    }

    @Test
    void testGetCarouselImageAlt() {
        assertEquals("Resort View", carouselSlide.getCarouselImageAlt());
    }

    @Test
    void testGetBadge() {
        assertEquals("New", carouselSlide.getBadge());
    }

    @Test
    void testGetBadgeColor() {
        assertEquals("#FF0000", carouselSlide.getBadgeColor());
    }

    @Test
    void testGetBadgeFontAwesomeIcon() {
        assertEquals("fa-star", carouselSlide.getBadgeFontAwesomeIcon());
    }

    @Test
    void testGetBrandIconImage() {
        assertEquals("/content/dam/mvw/brand-icon.png", carouselSlide.getBrandIconImage());
    }

    @Test
    void testGetBrandIconImageAlt() {
        assertEquals("Brand Icon", carouselSlide.getBrandIconImageAlt());
    }

    @Test
    void testGetCtaText() {
        assertEquals("Book Now", carouselSlide.getCtaText());
    }

    @Test
    void testGetCtaDestination() {
        assertEquals("/content/mvw/booking".concat(AppConstants.DOT_HTML_EXTENSION), carouselSlide.getCtaDestination());
    }

    @Test
    void testGetCtaTab() {
        assertEquals("newTab", carouselSlide.getCtaTab());
    }

    @Test
    void testGetCtaStyle() {
        assertEquals("Primary", carouselSlide.getCtaStyle());
    }

    @Test
    void testGetCtaPlacement() {
        assertEquals("bottom", carouselSlide.getCtaPlacement());
    }

    @Test
    void testGetFontAwesomeIcon() {
        assertEquals("fa-arrow-right", carouselSlide.getFontAwesomeIcon());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        setField(carouselSlide, "carouselHeadlineType", "heading1");
        assertEquals("h1", carouselSlide.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(carouselSlide, "carouselHeadlineType", "heading2");
        assertEquals("h2", carouselSlide.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(carouselSlide, "carouselHeadlineType", "heading3");
        assertEquals("h3", carouselSlide.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(carouselSlide, "carouselHeadlineType", "heading4");
        assertEquals("h4", carouselSlide.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(carouselSlide, "carouselHeadlineType", "heading5");
        assertEquals("h5", carouselSlide.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(carouselSlide, "carouselHeadlineType", "heading6");
        assertEquals("h6", carouselSlide.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(carouselSlide, "carouselHeadlineType", "invalid");
        assertEquals("h3", carouselSlide.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(carouselSlide, "carouselHeadlineType", null);
        assertEquals("h3", carouselSlide.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        CarouselSlidesPojo emptySlide = new CarouselSlidesPojo();
        assertNull(emptySlide.getCarouselHeadline());
        assertNull(emptySlide.getResortName());
        assertNull(emptySlide.getCarouselImage());
        assertNull(emptySlide.getBadge());
        assertNull(emptySlide.getCtaText());
    }
}
