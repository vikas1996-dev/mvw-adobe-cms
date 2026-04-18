package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PropertiesTest {

    private Properties properties;

    @BeforeEach
    void setUp() {
        properties = new Properties();
    }

    @Test
    void testAllGetters() {
        assertNull(properties.getCoordinateLatitude());
        assertNull(properties.getCoordinateLongitude());
        assertNull(properties.getCheckIn());
        assertNull(properties.getCheckOut());
        assertNull(properties.getClub());
        assertNull(properties.getName());
        assertNull(properties.getNodename());
        assertNull(properties.getDescription());
        assertNull(properties.getLongDescription());
        assertNull(properties.getSlug());
        assertNull(properties.getUniversalPropertyCode());
        assertNull(properties.getFlags());
        assertNull(properties.getDcmBrand());
        assertNull(properties.getPath());
        assertNull(properties.getAddress1());
        assertNull(properties.getAddress2());
        assertNull(properties.getCity());
        assertNull(properties.getState());
        assertNull(properties.getCountry());
        assertNull(properties.getContinent());
        assertNull(properties.getRegion());
        assertNull(properties.getPhoneMain());
        assertNull(properties.getAccessibility());
        assertNull(properties.getAccessibilityOverride());
        assertNull(properties.getAnnouncements());
        assertNull(properties.getPolicies());
        assertNull(properties.getTwitter());
        assertNull(properties.getFacebook());
        assertNull(properties.getInstagram());
        assertNull(properties.getPinterest());
        assertNull(properties.getYoutube());
        assertNull(properties.getTripadvisor());
        assertNull(properties.getTripAdvisorId());
        assertNull(properties.getVideos());
        assertNull(properties.getImages());
        assertNull(properties.getDocuments());
        assertNull(properties.getGallery());
        assertNull(properties.getGalleryCtaImage());
        assertNull(properties.getTripAdvisorDto());
        assertNull(properties.getZip());
        assertNull(properties.getStructuredContent());
        assertNull(properties.getRating());
        assertNull(properties.getReviews());
        assertNull(properties.getRatingImage());
        assertNull(properties.getWebUrl());
        assertFalse(properties.isVideo());
    }

    @Test
    void testSettersAndGetters() {
        TripAdvisorDto tripAdvisorDto = new TripAdvisorDto();
        tripAdvisorDto.setRating("4.5");

        properties.setTripAdvisorDto(tripAdvisorDto);
        properties.setGalleryCtaImage("/content/dam/gallery.jpg");

        assertEquals(tripAdvisorDto, properties.getTripAdvisorDto());
        assertEquals("/content/dam/gallery.jpg", properties.getGalleryCtaImage());
    }

    @Test
    void testSetMapPhotoPath() {
        // Note: setMapPhotoPath sets the field but getMapPhotoPath computes from images
        // so we need to test it doesn't throw and the setter works
        properties.setMapPhotoPath("/content/dam/map.jpg");
        // Since images is null, getMapPhotoPath returns null (computed value)
        assertNull(properties.getMapPhotoPath());
    }

    @Test
    void testGetMapPhotoPathWithNullImages() {
        assertNull(properties.getMapPhotoPath());
    }

    @Test
    void testGetMapPhotoPathWithEmptyImages() {
        setField(properties, "images", new ArrayList<>());
        assertNull(properties.getMapPhotoPath());
    }

    @Test
    void testGetMapPhotoPathWithValidMapImage() {
        List<Images> imagesList = new ArrayList<>();
        
        Images mapImage = new Images();
        DefaultModel referenceId = new DefaultModel();
        referenceId.setName("map");
        mapImage.setReferenceID(referenceId);
        
        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        photo.setPath("/content/dam/map.jpg");
        photos.add(photo);
        mapImage.setPhoto(photos);
        
        imagesList.add(mapImage);
        setField(properties, "images", imagesList);
        
        // getMapPhotoPath returns the raw path from the first "map" image's photo (no base URL prepending in this DTO)
        assertEquals("/content/dam/map.jpg", properties.getMapPhotoPath());
    }

    @Test
    void testGetMapPhotoPathWithNoMapImage() {
        List<Images> imagesList = new ArrayList<>();
        
        Images otherImage = new Images();
        DefaultModel referenceId = new DefaultModel();
        referenceId.setName("property");
        otherImage.setReferenceID(referenceId);
        
        imagesList.add(otherImage);
        setField(properties, "images", imagesList);
        
        assertNull(properties.getMapPhotoPath());
    }

    @Test
    void testGetCityCollectionWithNullFlags() {
        assertNull(properties.getCityCOllection());
    }

    @Test
    void testGetCityCollectionWithEmptyFlags() {
        setField(properties, "flags", new ArrayList<>());
        assertNull(properties.getCityCOllection());
    }

    @Test
    void testGetCityCollectionWithValidCityFlag() {
        List<Flags> flagsList = new ArrayList<>();
        
        Flags cityFlag = new Flags();
        setField(cityFlag, "nodename", "city-collection");
        setField(cityFlag, "name", "Miami Beach");
        
        flagsList.add(cityFlag);
        setField(properties, "flags", flagsList);
        
        assertEquals("Miami Beach", properties.getCityCOllection());
    }

    @Test
    void testGetCityCollectionWithUnderscoreInNodename() {
        List<Flags> flagsList = new ArrayList<>();
        
        Flags cityFlag = new Flags();
        setField(cityFlag, "nodename", "city_collection");
        setField(cityFlag, "name", "Beach City");
        
        flagsList.add(cityFlag);
        setField(properties, "flags", flagsList);
        
        assertEquals("Beach City", properties.getCityCOllection());
    }

    @Test
    void testGetCityCollectionWithNoCityFlag() {
        List<Flags> flagsList = new ArrayList<>();
        
        Flags otherFlag = new Flags();
        setField(otherFlag, "nodename", "other-flag");
        setField(otherFlag, "name", "Other");
        
        flagsList.add(otherFlag);
        setField(properties, "flags", flagsList);
        
        assertNull(properties.getCityCOllection());
    }

    @Test
    void testToString() {
        String toString = properties.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Properties"));
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
