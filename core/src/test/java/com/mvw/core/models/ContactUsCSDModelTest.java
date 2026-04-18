package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ContactUsCSDModelTest {

    private final AemContext context = new AemContext();
    private static final String RESOURCE_PATH = "/content/test/contactuscsd";

    @BeforeEach
    void setUp() {

        context.addModelsForClasses(ContactUsCSDModel.class);

        context.create().resource(RESOURCE_PATH,
                "form-type", "timeshare-owned-csd",
                "copySectionBgColor", "gray",
                "headlineText", "Owner Headline",
                "headlineType", "heading2",
                "shortDescription", "Owner short description",
                "secondaryDescription", "Owner secondary description",
                "owner-headline-alignment", "center",
                "propsect-headline-text", "Prospect Headline",
                "copyText", "Prospect copy text",
                "secondaryCopyText", "Prospect secondary text",
                "prospect-headline-alignment", "right"
        );

        context.create().resource(RESOURCE_PATH + "/ctas/cta1",
                "ctaText", "Visit Website",
                "ctaUrl", "/content/page.html",
                "ctaOpensIn", "newTab",
                "ctaStyle", "secondary"
        );

        context.create().resource(RESOURCE_PATH + "/ctas/cta2",
                "ctaText", "Call Now",
                "phoneNumber", "1234567890"
        );

        context.create().resource(RESOURCE_PATH + "/speakButtons/button1",
                "ctaText", "Speak With Us",
                "ctaUrl", "/contact.html"
        );

        context.currentResource(RESOURCE_PATH);
    }

    private ContactUsCSDModel getModel() {
        return context.currentResource().adaptTo(ContactUsCSDModel.class);
    }

    @Test
    void testModelAdaptation() {
        assertNotNull(getModel());
    }

    @Test
    void testOwnerFormType() {
        ContactUsCSDModel model = getModel();
        assertTrue(model.isOwner());
        assertFalse(model.isProspect());
    }

    @Test
    void testProspectFormType() {
        context.create().resource("/content/test/prospect",
                "form-type", "no-timeshare-prospect-csd"
        );
        context.currentResource("/content/test/prospect");

        ContactUsCSDModel model =
                context.currentResource().adaptTo(ContactUsCSDModel.class);

        assertTrue(model.isProspect());
        assertFalse(model.isOwner());
    }

    @Test
    void testCopySectionBgColor() {
        assertEquals("gray", getModel().getCopySectionBgColor());
    }

    @Test
    void testDefaultCopySectionBgColor() {
        context.create().resource("/content/test/default");
        context.currentResource("/content/test/default");

        ContactUsCSDModel model =
                context.currentResource().adaptTo(ContactUsCSDModel.class);

        assertEquals("none", model.getCopySectionBgColor());
    }

    @Test
    void testOwnerFields() {
        ContactUsCSDModel model = getModel();

        assertEquals("Owner Headline", model.getOwnerHeadlineText());
        assertEquals("heading2", model.getHeadlineType());
        assertEquals("Owner short description", model.getOwnerShortDescription());
        assertEquals("Owner secondary description", model.getOwnerSecondaryText());
    }

    @Test
    void testDefaultHeadlineType() {
        context.create().resource("/content/test/noheadline",
                "headlineText", "Test"
        );
        context.currentResource("/content/test/noheadline");

        ContactUsCSDModel model =
                context.currentResource().adaptTo(ContactUsCSDModel.class);

        assertEquals("heading3", model.getHeadlineType());
    }

    @Test
    void testProspectFields() {
        ContactUsCSDModel model = getModel();

        assertEquals("Prospect Headline", model.getProspectHeadlineText());
        assertEquals("Prospect copy text", model.getProspectCopyText());
        assertEquals("Prospect secondary text", model.getProspectSecondaryText());
    }



    @Test
    void testOwnerHeadlineAlignment() {
        assertEquals("center", getModel().getOwnerHeadlineAlignment());
    }

    @Test
    void testProspectHeadlineAlignment() {
        assertEquals("right", getModel().getProspectHeadlineAlignment());
    }

    @Test
    void testDefaultOwnerHeadlineAlignment() {
        context.create().resource("/content/test/defaultOwner");
        context.currentResource("/content/test/defaultOwner");

        ContactUsCSDModel model =
                context.currentResource().adaptTo(ContactUsCSDModel.class);

        assertEquals("left", model.getOwnerHeadlineAlignment());
    }

    @Test
    void testDefaultProspectHeadlineAlignment() {
        context.create().resource("/content/test/defaultProspect");
        context.currentResource("/content/test/defaultProspect");

        ContactUsCSDModel model =
                context.currentResource().adaptTo(ContactUsCSDModel.class);

        assertEquals("left", model.getProspectHeadlineAlignment());
    }


    @Test
    void testCtasMultifield() {
        ContactUsCSDModel model = getModel();

        List<ContactUsCSDModel.CtaItem> ctas = model.getCtas();
        assertEquals(2, ctas.size());

        ContactUsCSDModel.CtaItem first = ctas.get(0);
        assertEquals("Visit Website", first.getCtaText());
        assertEquals("/content/page.html", first.getHref());
        assertEquals("_blank", first.getTarget());
        assertEquals("secondary", first.getCtaStyle());
        assertFalse(first.isPhone());

        ContactUsCSDModel.CtaItem second = ctas.get(1);
        assertTrue(second.isPhone());
        assertEquals("tel:1234567890", second.getHref());
    }

    @Test
    void testEmptyCtas() {
        context.create().resource("/content/test/noctas");
        context.currentResource("/content/test/noctas");

        ContactUsCSDModel model =
                context.currentResource().adaptTo(ContactUsCSDModel.class);

        assertTrue(model.getCtas().isEmpty());
    }

    @Test
    void testSpeakButtons() {
        ContactUsCSDModel model = getModel();

        List<ContactUsCSDModel.SpeakButtonItem> speakButtons =
                model.getSpeakButtons();

        assertEquals(1, speakButtons.size());
        assertEquals("Speak With Us", speakButtons.get(0).getCtaText());
        assertEquals("/contact.html", speakButtons.get(0).getHref());
    }

    @Test
    void testEmptySpeakButtons() {
        context.create().resource("/content/test/nospeak");
        context.currentResource("/content/test/nospeak");

        ContactUsCSDModel model =
                context.currentResource().adaptTo(ContactUsCSDModel.class);

        assertTrue(model.getSpeakButtons().isEmpty());
    }
}
