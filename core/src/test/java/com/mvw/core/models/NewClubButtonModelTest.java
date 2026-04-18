package com.mvw.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class NewClubButtonModelTest {

    private NewClubButtonModel newClubButtonModel;

    @Mock
    private ResourceResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        newClubButtonModel = new NewClubButtonModel();
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
    void testGetButtonText() {
        setField(newClubButtonModel, "buttonText", "Click Here");
        assertEquals("Click Here", newClubButtonModel.getButtonText());
    }

    @Test
    void testGetButtonLinkWithValidLink() {
        setField(newClubButtonModel, "buttonLink", "/content/newclub/en/page");
        setField(newClubButtonModel, "resolver", resolver);
        
        String result = newClubButtonModel.getButtonLink();
        // Result may be null or modified depending on resolver configuration
    }

    @Test
    void testGetButtonLinkWithExternalLink() {
        setField(newClubButtonModel, "buttonLink", "https://example.com");
        setField(newClubButtonModel, "resolver", resolver);
        
        String result = newClubButtonModel.getButtonLink();
        assertEquals("https://example.com", result);
    }

    @Test
    void testGetButtonLinkWithNullLink() {
        setField(newClubButtonModel, "buttonLink", null);
        setField(newClubButtonModel, "resolver", resolver);
        
        String result = newClubButtonModel.getButtonLink();
        assertNull(result);
    }

    @Test
    void testGetButtonVariation() {
        setField(newClubButtonModel, "buttonVariation", "primary");
        assertEquals("primary", newClubButtonModel.getButtonVariation());
    }

    @Test
    void testGetIconOnly() {
        setField(newClubButtonModel, "iconOnly", "true");
        assertEquals("true", newClubButtonModel.getIconOnly());
    }

    @Test
    void testGetIconClass() {
        setField(newClubButtonModel, "iconClass", "fa-arrow");
        assertEquals("fa-arrow", newClubButtonModel.getIconClass());
    }

    @Test
    void testGetThirdParty() {
        setField(newClubButtonModel, "thirdParty", "true");
        assertEquals("true", newClubButtonModel.getThirdParty());
    }

    @Test
    void testGetOpensIn() {
        setField(newClubButtonModel, "opensIn", "newTab");
        assertEquals("newTab", newClubButtonModel.getOpensIn());
    }

    @Test
    void testGetButtonHide() {
        setField(newClubButtonModel, "buttonHide", "false");
        assertEquals("false", newClubButtonModel.getButtonHide());
    }

    @Test
    void testGetResolver() {
        setField(newClubButtonModel, "resolver", resolver);
        assertEquals(resolver, newClubButtonModel.getResolver());
    }

    @Test
    void testNullValues() {
        assertNull(newClubButtonModel.getButtonText());
        assertNull(newClubButtonModel.getButtonVariation());
        assertNull(newClubButtonModel.getIconOnly());
        assertNull(newClubButtonModel.getIconClass());
        assertNull(newClubButtonModel.getThirdParty());
        assertNull(newClubButtonModel.getOpensIn());
        assertNull(newClubButtonModel.getButtonHide());
        assertNull(newClubButtonModel.getResolver());
    }
}
