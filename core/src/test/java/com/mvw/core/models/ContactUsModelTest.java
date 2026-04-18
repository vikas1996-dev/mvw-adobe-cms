package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ContactUsModelTest {

    private final AemContext context = new AemContext();
    private static final String RESOURCE_PATH = "/content/test/contactus";

    @BeforeEach
    void setUp() {

        context.addModelsForClasses(ContactUsModel.class);

        context.create().resource(RESOURCE_PATH,
                "bg-color", "indigo",
                "image-position", "right",
                "initial-headline-text", "Get in Touch",
                "initial-headline-size", "heading1",
                "initial-headline-alignment", "center",
                "initial-description", "Contact us today",
                "description-alignment", "left",
                "number-of-images", "two",
                "big-image-alt", "Big Alt",
                "big-image-style", "rounded",
                "small-image-alt", "Small Alt",
                "small-image-position", "small--top-right",
                "mobile-headline-alignment", "left",
                "hide-image-on-mobile", true,
                "stacking-order", true
        );

        context.create().resource(RESOURCE_PATH + "/bigImage",
                "fileReference", "/content/dam/big.jpg");

        context.create().resource(RESOURCE_PATH + "/smallImage",
                "fileReference", "/content/dam/small.jpg");

        context.currentResource(RESOURCE_PATH);
    }

    private ContactUsModel getModel() {
        Resource resource = context.currentResource();
        assertNotNull(resource);
        return resource.adaptTo(ContactUsModel.class);
    }

    @Test
    void testModelAdaptation() {
        ContactUsModel model = getModel();
        assertNotNull(model);
    }

    @Test
    void testInjectedValues() {

        ContactUsModel model = getModel();

        assertEquals("indigo", model.getBgColor());
        assertEquals("right", model.getImagePosition());
        assertEquals("Get in Touch", model.getInitialHeadlineText());
        assertEquals("heading1", model.getInitialHeadlineSize());
        assertEquals("center", model.getInitialHeadlineAlignment());
        assertEquals("Contact us today", model.getInitialDescription());
        assertEquals("left", model.getDescriptionAlignment());
        assertEquals("two", model.getNumberOfImages());
        assertEquals("Big Alt", model.getBigImageAlt());
        assertEquals("rounded", model.getBigImageStyle());
        assertEquals("Small Alt", model.getSmallImageAlt());
        assertEquals("small--top-right", model.getSmallImagePosition());
        assertEquals("left", model.getMobileHeadlineAlignment());
        assertTrue(model.getHideImageOnMobile());
        assertTrue(model.getStackingOrder());
    }

    @Test
    void testImageReferences() {

        ContactUsModel model = getModel();

        assertEquals("/content/dam/big.jpg", model.getBigImage());
        assertEquals("/content/dam/small.jpg", model.getSmallImage());
    }

    @Test
    void testDefaultValues() {

        String path = "/content/test/default";
        context.create().resource(path);
        context.currentResource(path);

        Resource resource = context.currentResource();
        ContactUsModel model = resource.adaptTo(ContactUsModel.class);

        assertNotNull(model);

        assertEquals("none", model.getBgColor());
        assertEquals("left", model.getImagePosition());
        assertEquals("heading2", model.getInitialHeadlineSize());
        assertEquals("center", model.getInitialHeadlineAlignment());
        assertEquals("one", model.getNumberOfImages());
        assertEquals("default", model.getBigImageStyle());
        assertEquals("small--bottom-left", model.getSmallImagePosition());
        assertEquals("center", model.getMobileHeadlineAlignment());
        assertFalse(model.getHideImageOnMobile());
        assertFalse(model.getStackingOrder());
        assertNull(model.getBigImage());
        assertNull(model.getSmallImage());
    }

    @Test
    void testBlankValuesFallback() {

        String path = "/content/test/blank";
        context.create().resource(path,
                "bg-color", "",
                "image-position", "",
                "initial-headline-size", "",
                "number-of-images", ""
        );

        context.currentResource(path);

        ContactUsModel model =
                context.currentResource().adaptTo(ContactUsModel.class);

        assertEquals("none", model.getBgColor());
        assertEquals("left", model.getImagePosition());
        assertEquals("heading2", model.getInitialHeadlineSize());
        assertEquals("one", model.getNumberOfImages());
    }
}