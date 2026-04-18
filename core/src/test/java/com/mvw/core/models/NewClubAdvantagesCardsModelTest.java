package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class NewClubAdvantagesCardsModelTest {

    private final AemContext context = new AemContext();
    private NewClubAdvantagesCardModel cardsModel;

    @BeforeEach
    void setUp() {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        context.addModelsForClasses(NewClubAdvantagesCardModel.class, NewClubCardListPojo.class);
        context.load().json("/newclubadvantagescards.json", "/cards");
        context.currentResource("/cards/cards");
        cardsModel = context.currentResource().adaptTo(NewClubAdvantagesCardModel.class);
    }

    @Test
    void testCardListIsNotNull() {
        assertNotNull(cardsModel);
        assertNotNull(cardsModel.getCardList(), "Card list should not be null");
    }

    @Test
    void testCardListSize() {
        List<NewClubCardListPojo> cardList = cardsModel.getCardList();
        assertEquals(3, cardList.size(), "Card list should have 3 items");
    }

    @Test
    void testCardDetails() {
        List<NewClubCardListPojo> cardList = cardsModel.getCardList();

        NewClubCardListPojo firstCard = cardList.get(0);
        assertEquals("Unlock you inner uropiacde", firstCard.getCardTitle());
        assertTrue(firstCard.getCardDescription().contains("Commit to making once-in-a-lifetime"));
        assertEquals("vacation__icon--beach", firstCard.getIconClass());

        NewClubCardListPojo secondCard = cardList.get(1);
        assertEquals("Get the Inside Advantage", secondCard.getCardTitle());
        assertEquals("vacation__icon--advantage", secondCard.getIconClass());

        NewClubCardListPojo thirdCard = cardList.get(2);
       // assertTrue(thirdCard.getCardTitle().contains("Happiness-Inducing"));
        assertEquals("vacation__icon--map", thirdCard.getIconClass());
        assertEquals("First", thirdCard.getName());
    }
    @Test
    void testBackgroundProperties(){
        assertNotNull(cardsModel);
        assertEquals("/content/dam/mvw/Logo.svg", cardsModel.getBackgroundImage());
        assertEquals("logo alt", cardsModel.getAltText());
        assertEquals("false", cardsModel.getDisableLazyLoading());
        assertEquals("true", cardsModel.getHideBackgroundInMobile());
    }
}
 