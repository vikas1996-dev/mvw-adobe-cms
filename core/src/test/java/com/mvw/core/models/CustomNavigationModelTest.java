package com.mvw.core.models;

import com.adobe.cq.wcm.core.components.models.Navigation;
import com.mvw.core.constants.AppConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomNavigationModelTest {

    private CustomNavigationModel customNavigationModel;

    @Mock
    private Navigation navigation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customNavigationModel = new CustomNavigationModel();

        ResourceResolver mockResolver = mock(ResourceResolver.class);
        // Make resolver.map return same path
        when(mockResolver.map(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        // Inject resolver
        setField(customNavigationModel, "resolver", mockResolver);
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
    void testGetNavigation() {
        setField(customNavigationModel, "navigation", navigation);
        assertEquals(navigation, customNavigationModel.getNavigation());
    }

    @Test
    void testGetPromo1BgImage() {
        setField(customNavigationModel, "promo1BgImage", "/content/dam/images/promo1.jpg");
        assertEquals("/content/dam/images/promo1.jpg", customNavigationModel.getPromo1BgImage());
    }

    @Test
    void testGetPromo1BgImageAlt() {
        setField(customNavigationModel, "promo1BgImageAlt", "Promo 1 Alt Text");
        assertEquals("Promo 1 Alt Text", customNavigationModel.getPromo1BgImageAlt());
    }

    @Test
    void testGetPromo1ShortDescription() {
        setField(customNavigationModel, "promo1ShortDescription", "Short description for promo 1");
        assertEquals("Short description for promo 1", customNavigationModel.getPromo1ShortDescription());
    }

    @Test
    void testGetPromo1Url() {
        setField(customNavigationModel, "promo1Url", "/content/mvw/en/promo1");
        assertEquals("/content/mvw/en/promo1".concat(AppConstants.DOT_HTML_EXTENSION), customNavigationModel.getPromo1Url());
    }

    @Test
    void testGetPromo1Tab() {
        setField(customNavigationModel, "promo1Tab", "_blank");
        assertEquals("_blank", customNavigationModel.getPromo1Tab());
    }

    @Test
    void testGetCtaStyle1() {
        setField(customNavigationModel, "ctaStyle1", "primary");
        assertEquals("primary", customNavigationModel.getCtaStyle1());
    }

    @Test
    void testGetCtaAlignment1() {
        setField(customNavigationModel, "ctaAlignment1", "center");
        assertEquals("center", customNavigationModel.getCtaAlignment1());
    }

    @Test
    void testGetCtaSize1() {
        setField(customNavigationModel, "ctaSize1", "large");
        assertEquals("large", customNavigationModel.getCtaSize1());
    }

    @Test
    void testGetPromo1LinkText() {
        setField(customNavigationModel, "promo1LinkText", "Learn More");
        assertEquals("Learn More", customNavigationModel.getPromo1LinkText());
    }

    @Test
    void testGetPromo2BgImage() {
        setField(customNavigationModel, "promo2BgImage", "/content/dam/images/promo2.jpg");
        assertEquals("/content/dam/images/promo2.jpg", customNavigationModel.getPromo2BgImage());
    }

    @Test
    void testGetPromo2BgImageAlt() {
        setField(customNavigationModel, "promo2BgImageAlt", "Promo 2 Alt Text");
        assertEquals("Promo 2 Alt Text", customNavigationModel.getPromo2BgImageAlt());
    }

    @Test
    void testGetPromo2ShortDescription() {
        setField(customNavigationModel, "promo2ShortDescription", "Short description for promo 2");
        assertEquals("Short description for promo 2", customNavigationModel.getPromo2ShortDescription());
    }

    @Test
    void testGetPromo2Url() {
        setField(customNavigationModel, "promo2Url", "/content/mvw/en/promo2");
        assertEquals("/content/mvw/en/promo2".concat(AppConstants.DOT_HTML_EXTENSION), customNavigationModel.getPromo2Url());
    }

    @Test
    void testGetPromo2Tab() {
        setField(customNavigationModel, "promo2Tab", "_self");
        assertEquals("_self", customNavigationModel.getPromo2Tab());
    }

    @Test
    void testGetCtaStyle2() {
        setField(customNavigationModel, "ctaStyle2", "secondary");
        assertEquals("secondary", customNavigationModel.getCtaStyle2());
    }

    @Test
    void testGetCtaAlignment2() {
        setField(customNavigationModel, "ctaAlignment2", "left");
        assertEquals("left", customNavigationModel.getCtaAlignment2());
    }

    @Test
    void testGetCtaSize2() {
        setField(customNavigationModel, "ctaSize2", "small");
        assertEquals("small", customNavigationModel.getCtaSize2());
    }

    @Test
    void testGetPromo2LinkText() {
        setField(customNavigationModel, "promo2LinkText", "Explore");
        assertEquals("Explore", customNavigationModel.getPromo2LinkText());
    }

    @Test
    void testGetContactUsCopy() {
        setField(customNavigationModel, "contactUsCopy", "Contact Us");
        assertEquals("Contact Us", customNavigationModel.getContactUsCopy());
    }

    @Test
    void testGetContactUsUrl() {
        setField(customNavigationModel, "contactUsUrl", "/content/mvw/en/contact");
        assertEquals("/content/mvw/en/contact".concat(AppConstants.DOT_HTML_EXTENSION), customNavigationModel.getContactUsUrl());
    }

    @Test
    void testGetContactUsTab() {
        setField(customNavigationModel, "contactUsTab", "_self");
        assertEquals("_self", customNavigationModel.getContactUsTab());
    }

    @Test
    void testGetContactCtaStyle() {
        setField(customNavigationModel, "contactCtaStyle", "tertiary");
        assertEquals("tertiary", customNavigationModel.getContactCtaStyle());
    }

    @Test
    void testGetContactCtaAlignment() {
        setField(customNavigationModel, "contactCtaAlignment", "right");
        assertEquals("right", customNavigationModel.getContactCtaAlignment());
    }

    @Test
    void testGetContactCtaSize() {
        setField(customNavigationModel, "contactCtaSize", "medium");
        assertEquals("medium", customNavigationModel.getContactCtaSize());
    }

    @Test
    void testGetContactUsIcon() {
        setField(customNavigationModel, "contactUsIcon", "fa-phone");
        assertEquals("fa-phone", customNavigationModel.getContactUsIcon());
    }

    @Test
    void testNullValues() {
        CustomNavigationModel emptyModel = new CustomNavigationModel();
        assertNull(emptyModel.getNavigation());
        assertNull(emptyModel.getPromo1BgImage());
        assertNull(emptyModel.getPromo2BgImage());
        assertNull(emptyModel.getContactUsCopy());
        assertNull(emptyModel.getContactUsUrl());
    }
}
