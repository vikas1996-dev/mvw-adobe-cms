package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
class NewClubResortListCarouselModelTest {

    private final AemContext context = new AemContext();

    private NewClubResortListCarouselModel newClubResortListCarouselModel;

    @BeforeEach
    void setUp() {
        // Load JSON content into AEM mock context
        context.load().json("/newclubresortlistcarousel.json", "/content");

        // Setting up the resource
        Resource resource = context.resourceResolver().getResource("/content/jcr:content/root/container/resortlistcarousel");
        newClubResortListCarouselModel = resource.adaptTo(NewClubResortListCarouselModel.class);
    }

    @Test
    void testCarouselType() {
        assertEquals("rightWrapped", newClubResortListCarouselModel.getCarouselType());
    }

}
