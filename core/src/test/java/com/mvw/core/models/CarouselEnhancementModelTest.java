package com.mvw.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarouselEnhancementModelTest {

    private CarouselEnhancementModel carouselModel;

    @BeforeEach
    void setUp() {
        carouselModel = new CarouselEnhancementModel();

        setField(carouselModel, "style", "carousel-style-1");
        setField(carouselModel, "headlineText", "Featured Destinations");
        setField(carouselModel, "headlineType", "heading2");
        setField(carouselModel, "copySectionAlignment", "center");
        setField(carouselModel, "copyText", "Explore our amazing destinations");
        setField(carouselModel, "copySectionBgColor", "#F5F5F5");
        setField(carouselModel, "carouselBgColor", "#FFFFFF");
        setField(carouselModel, "secondaryCopyText", "Book your dream vacation today");
        setField(carouselModel, "brandIconImageBgcolor", "#000000");
        setField(carouselModel, "copyAlignment", "left");
        setField(carouselModel, "secondaryCopyAlignment", "right");
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
    void testGetStyle() {
        assertEquals("carousel-style-1", carouselModel.getStyle());
    }

    @Test
    void testGetHeadlineText() {
        assertEquals("Featured Destinations", carouselModel.getHeadlineText());
    }

    @Test
    void testGetHeadlineType() {
        assertEquals("heading2", carouselModel.getHeadlineType());
    }

    @Test
    void testGetCopySectionAlignment() {
        assertEquals("center", carouselModel.getCopySectionAlignment());
    }

    @Test
    void testGetCopyText() {
        assertEquals("Explore our amazing destinations", carouselModel.getCopyText());
    }

    @Test
    void testGetCopySectionBgColor() {
        assertEquals("#F5F5F5", carouselModel.getCopySectionBgColor());
    }

    @Test
    void testGetCarouselBgColor() {
        assertEquals("#FFFFFF", carouselModel.getCarouselBgColor());
    }

    @Test
    void testGetSecondaryCopyText() {
        assertEquals("Book your dream vacation today", carouselModel.getSecondaryCopyText());
    }

    @Test
    void testGetBrandIconImageBgcolor() {
        assertEquals("#000000", carouselModel.getBrandIconImageBgcolor());
    }

    @Test
    void testGetCopyAlignment() {
        assertEquals("left", carouselModel.getCopyAlignment());
    }

    @Test
    void testGetSecondaryCopyAlignment() {
        assertEquals("right", carouselModel.getSecondaryCopyAlignment());
    }

    @Test
    void testGetCarouselSlide() {
        CarouselSlidesPojo slide1 = new CarouselSlidesPojo();
        setField(slide1, "carouselHeadline", "Slide 1");

        CarouselSlidesPojo slide2 = new CarouselSlidesPojo();
        setField(slide2, "carouselHeadline", "Slide 2");

        setField(carouselModel, "carouselSlide", Arrays.asList(slide1, slide2));

        List<CarouselSlidesPojo> slides = carouselModel.getCarouselSlide();
        assertEquals(2, slides.size());
    }

    @Test
    void testGetHeadlineTagWithHeading1() {
        setField(carouselModel, "headlineType", "heading1");
        assertEquals("h1", carouselModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading2() {
        setField(carouselModel, "headlineType", "heading2");
        assertEquals("h2", carouselModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading3() {
        setField(carouselModel, "headlineType", "heading3");
        assertEquals("h3", carouselModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading4() {
        setField(carouselModel, "headlineType", "heading4");
        assertEquals("h4", carouselModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading5() {
        setField(carouselModel, "headlineType", "heading5");
        assertEquals("h5", carouselModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithHeading6() {
        setField(carouselModel, "headlineType", "heading6");
        assertEquals("h6", carouselModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithInvalidType() {
        setField(carouselModel, "headlineType", "invalid");
        assertEquals("h2", carouselModel.getHeadlineTag());
    }

    @Test
    void testGetHeadlineTagWithNullType() {
        setField(carouselModel, "headlineType", null);
        assertEquals("h2", carouselModel.getHeadlineTag());
    }

    @Test
    void testNullValues() {
        CarouselEnhancementModel emptyModel = new CarouselEnhancementModel();
        assertNull(emptyModel.getStyle());
        assertNull(emptyModel.getHeadlineText());
        assertNull(emptyModel.getCopyText());
        assertNull(emptyModel.getCarouselSlide());
    }
}
