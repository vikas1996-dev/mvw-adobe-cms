package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class NewclubTestimonialsModelTest {

    private final AemContext context = new AemContext();
    private NewclubTestimonialsModel testimonials;

    @BeforeEach
    void setUp() {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        context.addModelsForClasses(NewclubTestimonialsModel.class, NewClubCardListPojo.class);
        context.load().json("/newclubtestimonials.json", "/testimonials");
        context.currentResource("/testimonials/testimonials");
        testimonials = context.currentResource().adaptTo(NewclubTestimonialsModel.class);
    }

    @Test
    void testTestimonialsModel() {

        assertNotNull(testimonials);
        assertNotNull(testimonials.getCardList());
        assertEquals(3, testimonials.getCardList().size());

        // Validate first card
        NewClubCardListPojo firstCard = testimonials.getCardList().get(0);
        assertEquals("Title 1", firstCard.getCardTitle());
        assertEquals("First last", firstCard.getName());
        assertEquals("alt text", firstCard.getAltText());
        assertEquals("/content/dam/new-club-brand-awareness/Aspect ratio.png",
                firstCard.getThumbnailImage());
    }
}
 