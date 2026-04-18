package com.mvw.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class NewClubModelsTest {

    // Create AEM mock context
    private final AemContext context = new AemContext();

    private NewClubThirdPartyLinkModel model;

    @BeforeEach
    void setUp() {

        // Load JSON content into AEM mock context
        context.load().json(
                "/newclubthirdpartymodal.json",
                "/content/experience-fragments/newclub/us/en/site/third-party-modal/master/jcr:content/root/thirdparty");

        // Get the resource from mock repository
        Resource resource = context.resourceResolver()
                .getResource("/content/experience-fragments/newclub/us/en/site/third-party-modal/master/jcr:content/root/thirdparty");
        assertNotNull(resource, "Resource should not be null");

        // Adapt to model
        model = resource.adaptTo(NewClubThirdPartyLinkModel.class);
        assertNotNull(model, "Model adaptation should not return null");
    }

    @Test
    void testModelProperties() {

        assertEquals("You are Leaving Site", model.getModalTitle());
        assertEquals("<p>checking the modal</p>", model.getModalDescription());
        NewClubButtonModel buttonModel = model.getNewClubButtonModel();
        assertNotNull(buttonModel, "Button model should not be null");
        assertEquals("I acknowledge", buttonModel.getButtonText());
        assertEquals("primary", buttonModel.getButtonVariation());
    }


}