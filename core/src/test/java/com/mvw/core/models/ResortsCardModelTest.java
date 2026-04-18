package com.mvw.core.models;

import com.mvw.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ResortsCardModelTest {

    private final AemContext context = AppAemContext.newAemContext();
    private ResortsCardModel resortsCardModel;

    @BeforeEach
    void setUp() {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

        context.addModelsForClasses(ResortsCardModel.class, NewClubResortCardsItemModal.class);
        context.load().json("/NewClubResortsCard.json", "/content");

        // Get the resource from mock repository
        Resource currentResource = context.currentResource("/content/jcr:content/root/container/resorts_card");
        resortsCardModel = currentResource.adaptTo(ResortsCardModel.class); // Adapted from resource object
    }

    @Test
    void testHeaderProperties() {
        assertNotNull(resortsCardModel);
        assertEquals("One Happiness-Inducing Destination After Another", resortsCardModel.getResortCardHeading());
        assertEquals("Whether you’re staying for a family vacation or romantic getaway, each all-in resort is completely distinctive. Because like you, we believe that paradise isn’t limited to a single destination.",
                resortsCardModel.getResortCardDescription());
    }
    @Test
    void testCardDetails(){
        java.util.List<NewClubResortCardsItemModal> newClubResortCardsItemModals = resortsCardModel.getCardsItem();
        assertNotNull(newClubResortCardsItemModals);
        assertEquals("Family Vacation",
                newClubResortCardsItemModals.get(0).getOverlayText());
        assertEquals("/content/dam/new-club-brand-awareness/brand-awareness/resortcard-1.jpg",
                newClubResortCardsItemModals.get(0).getImage());
        assertEquals("Family Vacation",
                newClubResortCardsItemModals.get(0).getAltText());
        assertEquals("false",
                newClubResortCardsItemModals.get(0).getDisableLazyLoading());
    }
}
