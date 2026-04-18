package com.mvw.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class LegalErrorInformationModelTest {

    private final AemContext context = new AemContext();

    private LegalErrorInformationModel model;

    @BeforeEach
    void setUp() {
        // Load JSON under the path where the component would live
        context.load().json("/legalErrorInformationModel.json",
                "/content/mvw/test-page/jcr:content/legalErrorInformation");

        // Register models package (same pattern you used)
        context.addModelsForPackage("com.mvw.core.models");

        Resource resource = context.resourceResolver().getResource(
                "/content/mvw/test-page/jcr:content/legalErrorInformation");

        assertNotNull(resource, "Component resource should exist");
        model = resource.adaptTo(LegalErrorInformationModel.class);
        assertNotNull(model, "Model should adapt from resource");
    }

    @Test
    void testImageFields() {
        assertEquals("/content/dam/usb-legal/images/legal-error/main.png",
                model.getMainImageFileReference());
        assertEquals("Main legal error image alt text",
                model.getMainImageAltText());

        assertEquals("/content/dam/usb-legal/images/legal-error/overlay.png",
                model.getOverlayImagePath());

        // As per current annotation:
        // @ValueMapValue(name="mboverlay/fileReference") -> overlayImageAltText
        assertEquals("Mobile overlay alt text (as per current model mapping)",
                model.getOverlayImageAltText());
    }

    @Test
    void testCopyText() {
        assertNotNull(model.getCopyText());
        org.junit.jupiter.api.Assertions.assertTrue(
                model.getCopyText().contains("Please contact support"));
    }

    @Test
    void testCardList() {
        List<SupportDetailsPojo> cards = model.getCardList();
        assertNotNull(cards);
        assertEquals(2, cards.size());
        assertEquals("fa-sharp fa-regular fa-envelope", cards.get(0).getIconClass());
        assertEquals("Call Us", cards.get(0).getLabel());
        assertEquals("Available 24/7", cards.get(0).getLinkText());
        assertEquals("+1-800-123-4567", cards.get(0).getButtonLink());

        assertEquals("fa-sharp fa-light fa-clock", cards.get(1).getIconClass());
        assertEquals("Email Us", cards.get(1).getLabel());
        assertEquals("Response within 1 business day", cards.get(1).getLinkText());
        assertEquals("support@example.com", cards.get(1).getButtonLink());
    }
}
