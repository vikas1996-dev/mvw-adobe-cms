package com.mvw.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FormPageHeroModelTest {

    private FormPageHeroModel formPageHeroModel;
    private SlingHttpServletRequest mockRequest;

    private static final String PHONE_NUMBER = "1-800-555-1234";

    @BeforeEach
    void setUp() {
        formPageHeroModel = new FormPageHeroModel();
        mockRequest = mock(SlingHttpServletRequest.class);

        setField(formPageHeroModel, "bgImage", "/content/dam/mvw/hero-bg.jpg");
        setField(formPageHeroModel, "mobileBgImage", "/content/dam/mvw/mobile-hero-bg.jpg");
        setField(formPageHeroModel, "formPosition", "right");
        setField(formPageHeroModel, "headlineText", "Plan Your Vacation");
        setField(formPageHeroModel, "headlineAlignment", "center");
        setField(formPageHeroModel, "shortDescription", "<p>Start your dream vacation today</p>");
        setField(formPageHeroModel, "shortDescriptionAlignment", "center");
        setField(formPageHeroModel, "copySectionBgColor", "#FFFFFF");
        setField(formPageHeroModel, "BgColorDesktop", "#F5F5F5");
        setField(formPageHeroModel, "copySectionBgColorMobile", "#EEEEEE");
        setField(formPageHeroModel, "BgColorMobile", "#E0E0E0");
        setField(formPageHeroModel, "specialOfferPoints", "5000");
        setField(formPageHeroModel, "specialOfferCopy", "Earn bonus points");
        setField(formPageHeroModel, "specialOfferAlignment", "left");
        setField(formPageHeroModel, "secondaryCopyText", "Call us at {{phoneNumber}}");
        setField(formPageHeroModel, "formType", "contact-form");
        setField(formPageHeroModel, "copyBlockType", "standard");
        setField(formPageHeroModel, "participationXfPath", "/content/experience-fragments/mvw/participation");
        setField(formPageHeroModel, "participationHeadline", "Join Now");
        setField(formPageHeroModel, "request", mockRequest);
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
    void testGetBgImage() {
        assertEquals("/content/dam/mvw/hero-bg.jpg", formPageHeroModel.getBgImage());
    }

    @Test
    void testGetMobileBgImage() {
        assertEquals("/content/dam/mvw/mobile-hero-bg.jpg", formPageHeroModel.getMobileBgImage());
    }
    
    @Test
    void testGetFormPosition() {
        assertEquals("right", formPageHeroModel.getFormPosition());
    }

    @Test
    void testGetHeadlineText() {
        assertEquals("Plan Your Vacation", formPageHeroModel.getHeadlineText());
    }


    @Test
    void testGetHeadlineAlignment() {
        assertEquals("center", formPageHeroModel.getHeadlineAlignment());
    }

    @Test
    void testGetShortDescriptionStripsHtmlTags() {
        assertEquals("Start your dream vacation today", formPageHeroModel.getShortDescription());
    }

    @Test
    void testGetShortDescriptionWithNullValue() {
        setField(formPageHeroModel, "shortDescription", null);
        assertNull(formPageHeroModel.getShortDescription());
    }

    @Test
    void testGetShortDescriptionAlignment() {
        assertEquals("center", formPageHeroModel.getShortDescriptionAlignment());
    }

    @Test
    void testGetCopySectionBgColor() {
        assertEquals("#FFFFFF", formPageHeroModel.getCopySectionBgColor());
    }

    @Test
    void testGetBgColorDesktop() {
        assertEquals("#F5F5F5", formPageHeroModel.getBgColorDesktop());
    }

    @Test
    void testGetCopySectionBgColorMobile() {
        assertEquals("#EEEEEE", formPageHeroModel.getCopySectionBgColorMobile());
    }

    @Test
    void testGetBgColorMobile() {
        assertEquals("#E0E0E0", formPageHeroModel.getBgColorMobile());
    }

    @Test
    void testGetSpecialOfferPoints() {
        assertEquals("5000", formPageHeroModel.getSpecialOfferPoints());
    }

    @Test
    void testGetSpecialOfferCopy() {
        assertEquals("Earn bonus points", formPageHeroModel.getSpecialOfferCopy());
    }

    @Test
    void testGetSpecialOfferAlignment() {
        assertEquals("left", formPageHeroModel.getSpecialOfferAlignment());
    }

    @Test
    void testGetSecondaryCopyTextWithPhonePlaceholder() {
        when(mockRequest.getAttribute("phoneNumber")).thenReturn(PHONE_NUMBER);
        assertEquals("Call us at " + PHONE_NUMBER, formPageHeroModel.getSecondaryCopyText());
    }

    @Test
    void testGetSecondaryCopyTextWithNullPhoneAttribute() {
        when(mockRequest.getAttribute("phoneNumber")).thenReturn(null);
        assertEquals("Call us at {{phoneNumber}}", formPageHeroModel.getSecondaryCopyText());
    }

    @Test
    void testGetSecondaryCopyTextWithNullRequest() {
        setField(formPageHeroModel, "request", null);
        assertEquals("Call us at {{phoneNumber}}", formPageHeroModel.getSecondaryCopyText());
    }

    @Test
    void testGetSecondaryCopyTextWithoutPlaceholder() {
        setField(formPageHeroModel, "secondaryCopyText", "Contact us today");
        when(mockRequest.getAttribute("phoneNumber")).thenReturn(PHONE_NUMBER);
        assertEquals("Contact us today", formPageHeroModel.getSecondaryCopyText());
    }

    @Test
    void testGetSecondaryCopyTextWhenNull() {
        setField(formPageHeroModel, "secondaryCopyText", null);
        assertNull(formPageHeroModel.getSecondaryCopyText());
    }

    @Test
    void testGetFormType() {
        assertEquals("contact-form", formPageHeroModel.getFormType());
    }

    @Test
    void testGetCopyBlockType() {
        assertEquals("standard", formPageHeroModel.getCopyBlockType());
    }

    @Test
    void testGetParticipationXfPath() {
        assertEquals("/content/experience-fragments/mvw/participation", formPageHeroModel.getParticipationXfPath());
    }

    @Test
    void testGetParticipationXfRootPath() {
        assertEquals("/content/experience-fragments/mvw/participation/jcr:content/root", 
                     formPageHeroModel.getParticipationXfRootPath());
    }

    @Test
    void testGetParticipationXfRootPathWithExistingJcrContent() {
        setField(formPageHeroModel, "participationXfPath", "/content/experience-fragments/mvw/participation/jcr:content/root");
        assertEquals("/content/experience-fragments/mvw/participation/jcr:content/root", 
                     formPageHeroModel.getParticipationXfRootPath());
    }

    @Test
    void testGetParticipationXfRootPathWithNullPath() {
        setField(formPageHeroModel, "participationXfPath", null);
        assertNull(formPageHeroModel.getParticipationXfRootPath());
    }

    @Test
    void testGetParticipationXfRootPathWithEmptyPath() {
        setField(formPageHeroModel, "participationXfPath", "   ");
        assertNull(formPageHeroModel.getParticipationXfRootPath());
    }

    @Test
    void testGetParticipationHeadline() {
        assertEquals("Join Now", formPageHeroModel.getParticipationHeadline());
    }



    @Test
    void testNullValues() {
        FormPageHeroModel emptyModel = new FormPageHeroModel();
        assertNull(emptyModel.getBgImage());
        assertNull(emptyModel.getFormPosition());
        assertNull(emptyModel.getHeadlineText());
        assertNull(emptyModel.getFormType());
    }
}
