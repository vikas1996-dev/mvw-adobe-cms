package com.mvw.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class NewClubResortPromotionModelTest {

    private final AemContext context = new AemContext();

    private NewClubResortPromotionModel newClubButtonModel;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(NewClubResortPromotionModel.class);
        context.load().json("/newclubresortpromotion.json",
                "/resortpromotion");
        context.currentResource("/resortpromotion/resortpromotion");
        newClubButtonModel =  context.currentResource().adaptTo(NewClubResortPromotionModel.class);
    }

    @Test
    void testImageProperties(){
        assertNotNull(newClubButtonModel);
        assertEquals("/content/dam/new-club-brand-awareness/brand-awareness/uphoria.png", newClubButtonModel.getResortImage());
        assertEquals("alt ", newClubButtonModel.getAltText());
        assertEquals("Hideaway At Royalton Punta Cana, An Autograph Collection All-Inclusive Resort &             Casino – Adults Only", newClubButtonModel.getImageCaption());
    }

    @Test
    void testModel() {
        assertNotNull(newClubButtonModel);

        // ---- Validating simple value ----
        assertEquals("India", newClubButtonModel.getCountryName());

        // ---- Validating First Column List ----
        assertNotNull(newClubButtonModel.getResortListFirstColumn());
        assertEquals(3, newClubButtonModel.getResortListFirstColumn().size());
        assertEquals("Royalton CHIC Punta Cana",
                newClubButtonModel.getResortListFirstColumn().get(0).getNavLinkText());
        assertEquals("_blank",
                newClubButtonModel.getResortListFirstColumn().get(0).getNavLinkTab());
        assertEquals("/content/newclub/us/en/test.html",
                newClubButtonModel.getResortListFirstColumn().get(0).getNavLinkUrl());

        // ---- Validating Second Column List ----
        assertNotNull(newClubButtonModel.getResortListSecondColumn());
        assertEquals(3, newClubButtonModel.getResortListSecondColumn().size());
        assertEquals("Royalton CHIC Punta Cana",
                newClubButtonModel.getResortListSecondColumn().get(0).getNavLinkText());
        assertEquals("_blank",
                newClubButtonModel.getResortListSecondColumn().get(0).getNavLinkTab());
        assertEquals("/content/newclub/us/en/test.html",
                newClubButtonModel.getResortListSecondColumn().get(0).getNavLinkUrl());
    }
}
 