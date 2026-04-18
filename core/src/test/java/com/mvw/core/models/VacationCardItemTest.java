package com.mvw.core.models;

import com.mvw.core.constants.AppConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VacationCardItemTest {

    private VacationCardItem vacationCardItem;

    @BeforeEach
    void setUp() {
        vacationCardItem = new VacationCardItem();
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
    void testGetCardBgColor() {
        setField(vacationCardItem, "cardBgColor", "blue");
        assertEquals("blue", vacationCardItem.getCardBgColor());
    }

    @Test
    void testGetCardHeadline() {
        setField(vacationCardItem, "cardHeadline", "Card Headline");
        assertEquals("Card Headline", vacationCardItem.getCardHeadline());
    }

    @Test
    void testGetHeadlineType() {
        setField(vacationCardItem, "headlineType", "heading4");
        assertEquals("heading4", vacationCardItem.getHeadlineType());
    }

    @Test
    void testGetCardDescription() {
        setField(vacationCardItem, "cardDescription", "Card description");
        assertEquals("Card description", vacationCardItem.getCardDescription());
    }

    @Test
    void testGetCardImage() {
        setField(vacationCardItem, "cardImage", "/content/dam/card.jpg");
        assertEquals("/content/dam/card.jpg", vacationCardItem.getCardImage());
    }

    @Test
    void testGetMobileImage() {
        setField(vacationCardItem, "mobileImage", "/content/dam/mobile-card.jpg");
        assertEquals("/content/dam/mobile-card.jpg", vacationCardItem.getMobileImage());
    }

    @Test
    void testGetCardImageAlt() {
        setField(vacationCardItem, "cardImageAlt", "Card Alt");
        assertEquals("Card Alt", vacationCardItem.getCardImageAlt());
    }

    @Test
    void testGetBadge() {
        setField(vacationCardItem, "badge", "NEW");
        assertEquals("NEW", vacationCardItem.getBadge());
    }

    @Test
    void testGetBadgeColor() {
        setField(vacationCardItem, "badgeColor", "green");
        assertEquals("green", vacationCardItem.getBadgeColor());
    }

    @Test
    void testGetBadgeFontAwesomeIcon() {
        setField(vacationCardItem, "badgeFontAwesomeIcon", "fa-star");
        assertEquals("fa-star", vacationCardItem.getBadgeFontAwesomeIcon());
    }

    @Test
    void testGetCtaText() {
        setField(vacationCardItem, "ctaText", "Learn More");
        assertEquals("Learn More", vacationCardItem.getCtaText());
    }

    @Test
    void testGetCtaDestination() {
        ResourceResolver mockResolver = mock(ResourceResolver.class);
        // Make resolver.map return same path
        when(mockResolver.map(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        // Inject resolver
        setField(vacationCardItem, "resolver", mockResolver);

        setField(vacationCardItem, "ctaDestination", "/content/tmvcs/us/en/page1");
        assertEquals("/content/tmvcs/us/en/page1".concat(AppConstants.DOT_HTML_EXTENSION), vacationCardItem.getCtaDestination());
    }

    @Test
    void testGetCtaTab() {
        setField(vacationCardItem, "ctaTab", "newTab");
        assertEquals("newTab", vacationCardItem.getCtaTab());
    }

    @Test
    void testGetCtaStyle() {
        setField(vacationCardItem, "ctaStyle", "primary");
        assertEquals("primary", vacationCardItem.getCtaStyle());
    }

    @Test
    void testGetCtaPlacement() {
        setField(vacationCardItem, "ctaPlacement", "left");
        assertEquals("left", vacationCardItem.getCtaPlacement());
    }

    @Test
    void testGetFontAwesomeIcon() {
        setField(vacationCardItem, "fontAwesomeIcon", "fa-arrow");
        assertEquals("fa-arrow", vacationCardItem.getFontAwesomeIcon());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        setField(vacationCardItem, "headlineType", "heading1");
        assertEquals("h1", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(vacationCardItem, "headlineType", "heading2");
        assertEquals("h2", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(vacationCardItem, "headlineType", "heading3");
        assertEquals("h3", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(vacationCardItem, "headlineType", "heading4");
        assertEquals("h4", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(vacationCardItem, "headlineType", "heading5");
        assertEquals("h5", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(vacationCardItem, "headlineType", "heading6");
        assertEquals("h6", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(vacationCardItem, "headlineType", null);
        assertEquals("h4", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(vacationCardItem, "headlineType", "paragraph");
        assertEquals("h4", vacationCardItem.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        assertNull(vacationCardItem.getCardBgColor());
        assertNull(vacationCardItem.getCardHeadline());
        assertNull(vacationCardItem.getHeadlineType());
        assertNull(vacationCardItem.getCardDescription());
        assertNull(vacationCardItem.getCardImage());
        assertNull(vacationCardItem.getMobileImage());
        assertNull(vacationCardItem.getCardImageAlt());
        assertNull(vacationCardItem.getBadge());
        assertNull(vacationCardItem.getBadgeColor());
        assertNull(vacationCardItem.getBadgeFontAwesomeIcon());
        assertNull(vacationCardItem.getCtaText());
        assertNull(vacationCardItem.getCtaDestination());
        assertNull(vacationCardItem.getCtaTab());
        assertNull(vacationCardItem.getCtaStyle());
        assertNull(vacationCardItem.getCtaPlacement());
        assertNull(vacationCardItem.getFontAwesomeIcon());
    }
}
