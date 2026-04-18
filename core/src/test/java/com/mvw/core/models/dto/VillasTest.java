package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VillasTest {

    private Villas villas;

    @BeforeEach
    void setUp() {
        villas = new Villas();
    }

    @Test
    void testGettersAndSetters() {
        villas.setName("Two-Bedroom Villa");
        villas.setNodename("two-bedroom");
        villas.setSleeps("4");
        villas.setSquareFootage("1200");
        villas.setView("Ocean View");
        villas.setDescription("Spacious villa");
        villas.setCheckAvailabilityUrl("https://book.test.com");
        villas.setDisclaimer("Prices may vary");

        assertEquals("Two-Bedroom Villa", villas.getName());
        assertEquals("two-bedroom", villas.getNodename());
        assertEquals("4", villas.getSleeps());
        assertEquals("1200", villas.getSquareFootage());
        assertEquals("Ocean View", villas.getView());
        assertEquals("Spacious villa", villas.getDescription());
        assertEquals("https://book.test.com", villas.getCheckAvailabilityUrl());
        assertEquals("Prices may vary", villas.getDisclaimer());
    }

    @Test
    void testGetGallery() {
        List<Gallery> galleryList = new ArrayList<>();
        villas.setGallery(galleryList);
        assertEquals(galleryList, villas.getGallery());
    }

    @Test
    void testGetImages() {
        List<Images> imagesList = new ArrayList<>();
        villas.setImages(imagesList);
        assertEquals(imagesList, villas.getImages());
    }

    @Test
    void testGetAmenities() {
        List<Amenities> amenitiesList = new ArrayList<>();
        villas.setAmenities(amenitiesList);
        assertEquals(amenitiesList, villas.getAmenities());
    }

    @Test
    void testGetMapper() {
        assertNotNull(Villas.getMapper());
    }

    @Test
    void testGetFloorPlanUrlWithNullImages() {
        assertNull(villas.getFloorPlanUrl());
    }

    @Test
    void testGetFloorPlanUrlWithEmptyImages() {
        villas.setImages(new ArrayList<>());
        assertNull(villas.getFloorPlanUrl());
    }

    @Test
    void testGetFloorPlanUrlWithValidFloorplan() {
        List<Images> imagesList = new ArrayList<>();
        
        Images floorplanImage = new Images();
        setField(floorplanImage, "nodename", "floorplan-1br");
        
        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        setField(photo, "path", "/content/dam/floorplan.jpg");
        photos.add(photo);
        setField(floorplanImage, "photo", photos);
        
        imagesList.add(floorplanImage);
        villas.setImages(imagesList);
        
        assertEquals("/content/dam/floorplan.jpg", villas.getFloorPlanUrl());
    }

    @Test
    void testGetFloorPlanUrlWithNoFloorplanImage() {
        List<Images> imagesList = new ArrayList<>();
        
        Images otherImage = new Images();
        setField(otherImage, "nodename", "main-image");
        
        imagesList.add(otherImage);
        villas.setImages(imagesList);
        
        assertNull(villas.getFloorPlanUrl());
    }

    @Test
    void testGetGalleryJson() {
        List<Gallery> galleryList = new ArrayList<>();
        villas.setGallery(galleryList);
        
        String json = villas.getGalleryJson();
        assertNotNull(json);
        assertEquals("[]", json);
    }

    @Test
    void testGetGalleryJsonWithNull() {
        villas.setGallery(null);
        String json = villas.getGalleryJson();
        assertEquals("null", json);
    }

    @Test
    void testGetAmenitiesJson() {
        List<Amenities> amenitiesList = new ArrayList<>();
        villas.setAmenities(amenitiesList);
        
        String json = villas.getAmenitiesJson();
        assertNotNull(json);
        assertEquals("[]", json);
    }

    @Test
    void testGetAmenitiesJsonWithNull() {
        villas.setAmenities(null);
        String json = villas.getAmenitiesJson();
        assertEquals("null", json);
    }

    @Test
    void testNullValues() {
        assertNull(villas.getName());
        assertNull(villas.getNodename());
        assertNull(villas.getSleeps());
        assertNull(villas.getSquareFootage());
        assertNull(villas.getView());
        assertNull(villas.getGallery());
        assertNull(villas.getImages());
        assertNull(villas.getDescription());
        assertNull(villas.getAmenities());
        assertNull(villas.getCheckAvailabilityUrl());
        assertNull(villas.getDisclaimer());
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
}
