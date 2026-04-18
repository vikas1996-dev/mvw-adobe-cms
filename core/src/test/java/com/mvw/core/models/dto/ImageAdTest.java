package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImageAdTest {

    private ImageAd imageAd;

    @BeforeEach
    void setUp() {
        imageAd = new ImageAd();
    }

    @Test
    void testGettersAndSetters() {
        imageAd.setName("Promotional Image");
        imageAd.setNodename("promo-image");
        imageAd.setAltText("Special promotion");

        assertEquals("Promotional Image", imageAd.getName());
        assertEquals("promo-image", imageAd.getNodename());
        assertEquals("Special promotion", imageAd.getAltText());
    }

    @Test
    void testSetAndGetReferenceId() {
        ImageAd.ReferenceId refId = new ImageAd.ReferenceId();
        refId.setName("main");
        
        imageAd.setReferenceId(refId);
        
        assertEquals(refId, imageAd.getReferenceId());
        assertEquals("main", imageAd.getReferenceId().getName());
    }

    @Test
    void testSetAndGetPhoto() {
        List<ImageAd.Photo> photos = new ArrayList<>();
        ImageAd.Photo photo = new ImageAd.Photo();
        photo.setName("Photo 1");
        photo.setPath("/content/dam/photo1.jpg");
        photos.add(photo);
        
        imageAd.setPhoto(photos);
        
        assertEquals(photos, imageAd.getPhoto());
        assertEquals(1, imageAd.getPhoto().size());
    }

    @Test
    void testReferenceIdInnerClass() {
        ImageAd.ReferenceId refId = new ImageAd.ReferenceId();
        refId.setName("Test Reference");
        
        assertEquals("Test Reference", refId.getName());
    }

    @Test
    void testPhotoInnerClass() {
        ImageAd.Photo photo = new ImageAd.Photo();
        photo.setName("Beach Photo");
        photo.setPath("/content/dam/beach.jpg");
        photo.setWidth("1920");
        photo.setHeight("1080");
        photo.setRatio("16:9");
        
        assertEquals("Beach Photo", photo.getName());
        assertEquals("/content/dam/beach.jpg", photo.getPath());
        assertEquals("1920", photo.getWidth());
        assertEquals("1080", photo.getHeight());
        assertEquals("16:9", photo.getRatio());
    }

    @Test
    void testNullValues() {
        assertNull(imageAd.getName());
        assertNull(imageAd.getNodename());
        assertNull(imageAd.getAltText());
        assertNull(imageAd.getReferenceId());
        assertNull(imageAd.getPhoto());
    }
}
