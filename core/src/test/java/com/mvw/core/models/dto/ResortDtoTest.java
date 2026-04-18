package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResortDtoTest {

    private ResortDto resortDto;

    @BeforeEach
    void setUp() {
        resortDto = new ResortDto();
    }

    @Test
    void testBasicGettersAndSetters() {
        resortDto.setName("Marriott Resort");
        resortDto.setMarshaCode("MARSH001");
        resortDto.setDescription("A beautiful resort");
        resortDto.setUniversalPropertyCode("UPC123");
        resortDto.setCity("Miami");
        resortDto.setState("Florida");
        resortDto.setCountry("USA");
        resortDto.setContinent("North America");
        resortDto.setRegion("Southeast");
        resortDto.setTripadvisor("excellent");
        resortDto.setTripadvisorId("TA123");
        resortDto.setRating("4.5");
        resortDto.setReviews("500");
        resortDto.setRatingImage("https://tripadvisor.com/rating.png");
        resortDto.setSlug("marriott-miami");

        assertEquals("Marriott Resort", resortDto.getName());
        assertEquals("MARSH001", resortDto.getMarshaCode());
        assertEquals("A beautiful resort", resortDto.getDescription());
        assertEquals("UPC123", resortDto.getUniversalPropertyCode());
        assertEquals("Miami", resortDto.getCity());
        assertEquals("Florida", resortDto.getState());
        assertEquals("USA", resortDto.getCountry());
        assertEquals("North America", resortDto.getContinent());
        assertEquals("Southeast", resortDto.getRegion());
        assertEquals("excellent", resortDto.getTripadvisor());
        assertEquals("TA123", resortDto.getTripadvisorId());
        assertEquals("4.5", resortDto.getRating());
        assertEquals("500", resortDto.getReviews());
        assertEquals("https://tripadvisor.com/rating.png", resortDto.getRatingImage());
        assertEquals("marriott-miami", resortDto.getSlug());
    }

    @Test
    void testSetAndGetImages() {
        List<Images> imagesList = new ArrayList<>();
        imagesList.add(new Images());
        resortDto.setImages(imagesList);
        assertEquals(imagesList, resortDto.getImages());
    }

    @Test
    void testGetMainPropertyPhotoPathWithNullImages() {
        assertNull(resortDto.getMainPropertyPhotoPath());
    }

    @Test
    void testGetMainPropertyPhotoPathWithEmptyImages() {
        resortDto.setImages(new ArrayList<>());
        assertNull(resortDto.getMainPropertyPhotoPath());
    }

    @Test
    void testGetMainPropertyPhotoPathWithValidMainImage() {
        List<Images> imagesList = new ArrayList<>();
        Images img = createImageWithReferenceId("main", "/content/dam/main.jpg");
        imagesList.add(img);
        resortDto.setImages(imagesList);
        
        assertEquals("/content/dam/main.jpg", resortDto.getMainPropertyPhotoPath());
    }

    @Test
    void testGetMainPropertyPhotoPathWithNoMainImage() {
        List<Images> imagesList = new ArrayList<>();
        Images img = createImageWithReferenceId("map", "/content/dam/map.jpg");
        imagesList.add(img);
        resortDto.setImages(imagesList);
        
        assertNull(resortDto.getMainPropertyPhotoPath());
    }

    @Test
    void testGetMapPhotoPathWithNullImages() {
        assertNull(resortDto.getMapPhotoPath());
    }

    @Test
    void testGetMapPhotoPathWithEmptyImages() {
        resortDto.setImages(new ArrayList<>());
        assertNull(resortDto.getMapPhotoPath());
    }

    @Test
    void testGetMapPhotoPathWithValidMapImage() {
        List<Images> imagesList = new ArrayList<>();
        Images img = createImageWithReferenceId("map", "/content/dam/map.jpg");
        imagesList.add(img);
        resortDto.setImages(imagesList);
        
        assertEquals("/content/dam/map.jpg", resortDto.getMapPhotoPath());
    }

    @Test
    void testSetMainPropertyPhotoPathDirectly() {
        resortDto.setMainPropertyPhotoPath("/content/dam/direct-main.jpg");
        // This sets the field directly, but getMainPropertyPhotoPath() uses images if set
        List<Images> imagesList = new ArrayList<>();
        Images img = createImageWithReferenceId("main", "/content/dam/main.jpg");
        imagesList.add(img);
        resortDto.setImages(imagesList);
        assertEquals("/content/dam/main.jpg", resortDto.getMainPropertyPhotoPath());
    }

    @Test
    void testSetMapPhotoPathDirectly() {
        resortDto.setMapPhotoPath("/content/dam/direct-map.jpg");
        // Similar to main property photo path
    }

    @Test
    void testGetActivitiesWithNullActivityTags() {
        List<String> activities = resortDto.getActivities();
        assertNotNull(activities);
        assertTrue(activities.isEmpty());
    }

    @Test
    void testGetActivitiesWithEmptyActivityTags() {
        resortDto.setActivityTags(new ArrayList<>());
        List<String> activities = resortDto.getActivities();
        assertNotNull(activities);
        assertTrue(activities.isEmpty());
    }

    @Test
    void testGetActivitiesWithValidActivityTags() {
        List<Tags> tagsList = new ArrayList<>();
        tagsList.add(createTag("Swimming"));
        tagsList.add(createTag("Golf"));
        resortDto.setActivityTags(tagsList);
        
        List<String> activities = resortDto.getActivities();
        assertEquals(2, activities.size());
        assertTrue(activities.contains("Swimming"));
        assertTrue(activities.contains("Golf"));
    }

    @Test
    void testGetActivitiesFiltersNullNames() {
        List<Tags> tagsList = new ArrayList<>();
        tagsList.add(createTag("Swimming"));
        tagsList.add(createTag(null));
        resortDto.setActivityTags(tagsList);
        
        List<String> activities = resortDto.getActivities();
        assertEquals(1, activities.size());
        assertEquals("Swimming", activities.get(0));
    }

    @Test
    void testGetVacationTypeWithNull() {
        List<String> vacationType = resortDto.getVacationType();
        assertNotNull(vacationType);
        assertTrue(vacationType.isEmpty());
    }

    @Test
    void testGetVacationTypeWithValidTypes() {
        List<Tags> tagsList = new ArrayList<>();
        tagsList.add(createTag("Beach"));
        tagsList.add(createTag("Ski"));
        resortDto.setVacationTypes(tagsList);
        
        List<String> vacationType = resortDto.getVacationType();
        assertEquals(2, vacationType.size());
        assertTrue(vacationType.contains("Beach"));
        assertTrue(vacationType.contains("Ski"));
    }

    @Test
    void testGetImageAltWithNullImages() {
        assertNull(resortDto.getImageAlt());
    }

    @Test
    void testGetImageAltWithEmptyImages() {
        resortDto.setImages(new ArrayList<>());
        assertNull(resortDto.getImageAlt());
    }

    @Test
    void testGetImageAltWithPropertyMainImageAndAltText() {
        List<Images> imagesList = new ArrayList<>();
        Images img = new Images();
        setField(img, "name", "Property, Main Image");
        setField(img, "altText", "Beautiful Resort View");
        imagesList.add(img);
        resortDto.setImages(imagesList);
        
        assertEquals("Beautiful Resort View", resortDto.getImageAlt());
    }

    @Test
    void testGetImageAltWithPropertyMainImageNoAltText() {
        resortDto.setName("Test Resort");
        List<Images> imagesList = new ArrayList<>();
        Images img = new Images();
        setField(img, "name", "Property, Main Image");
        imagesList.add(img);
        resortDto.setImages(imagesList);
        
        assertEquals("Test Resort", resortDto.getImageAlt());
    }

    @Test
    void testGetImageAltFallsBackToResortName() {
        resortDto.setName("Fallback Resort");
        List<Images> imagesList = new ArrayList<>();
        Images img = new Images();
        setField(img, "name", "Other Image");
        imagesList.add(img);
        resortDto.setImages(imagesList);
        
        assertEquals("Fallback Resort", resortDto.getImageAlt());
    }

    @Test
    void testGetCollectionWithNullFlags() {
        resortDto.setName("Default Resort");
        assertEquals("Default Resort", resortDto.getCollection());
    }

    @Test
    void testGetCollectionWithEmptyFlags() {
        resortDto.setName("Default Resort");
        resortDto.setFlags(new ArrayList<>());
        assertEquals("Default Resort", resortDto.getCollection());
    }

    @Test
    void testGetCollectionWithCityCollectionFlag() {
        List<Tags> flagsList = new ArrayList<>();
        Tags cityFlag = createTagWithNodename("City Collection Name", "city-collection");
        flagsList.add(cityFlag);
        resortDto.setFlags(flagsList);
        
        assertEquals("City Collection Name", resortDto.getCollection());
    }

    @Test
    void testGetCollectionWithNoCityCollectionFlag() {
        resortDto.setName("Default Resort");
        List<Tags> flagsList = new ArrayList<>();
        Tags otherFlag = createTagWithNodename("Other", "other-flag");
        flagsList.add(otherFlag);
        resortDto.setFlags(flagsList);
        
        assertNull(resortDto.getCollection());
    }

    @Test
    void testGetFeaturesWithNullFlags() {
        resortDto.setName("Default Resort");
        assertEquals("Default Resort", resortDto.getFeatures());
    }

    @Test
    void testGetFeaturesWithFeaturedFlag() {
        List<Tags> flagsList = new ArrayList<>();
        Tags featuredFlag = createTagWithNodename("Featured Resort", "featured");
        flagsList.add(featuredFlag);
        resortDto.setFlags(flagsList);
        
        assertEquals("Featured Resort", resortDto.getFeatures());
    }

    @Test
    void testSetAndGetDcmBrand() {
        Tags dcmBrand = createTag("Marriott");
        resortDto.setDcmBrand(dcmBrand);
        assertEquals(dcmBrand, resortDto.getDcmBrand());
    }

    @Test
    void testSetAndGetLocale() {
        ResortDto.LocaleInfo localeInfo = new ResortDto.LocaleInfo();
        localeInfo.setName("en-US");
        localeInfo.setPriority("1");
        resortDto.setLocale(localeInfo);
        
        assertEquals("en-US", resortDto.getLocale().getName());
        assertEquals("1", resortDto.getLocale().getPriority());
    }

    @Test
    void testSetAndGetSubLocale() {
        ResortDto.LocaleInfo subLocaleInfo = new ResortDto.LocaleInfo();
        subLocaleInfo.setName("en-GB");
        subLocaleInfo.setPriority("2");
        resortDto.setSubLocale(subLocaleInfo);
        
        assertEquals("en-GB", resortDto.getSubLocale().getName());
        assertEquals("2", resortDto.getSubLocale().getPriority());
    }

    @Test
    void testLocaleInfoClass() {
        ResortDto.LocaleInfo localeInfo = new ResortDto.LocaleInfo();
        
        localeInfo.setName("Test Locale");
        localeInfo.setPriority("5");
        
        assertEquals("Test Locale", localeInfo.getName());
        assertEquals("5", localeInfo.getPriority());
    }

    // Helper methods
    private Images createImageWithReferenceId(String refIdName, String photoPath) {
        Images img = new Images();
        DefaultModel refId = new DefaultModel();
        refId.setName(refIdName);
        setField(img, "referenceID", refId);
        
        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        setField(photo, "path", photoPath);
        photos.add(photo);
        setField(img, "photo", photos);
        
        return img;
    }

    private Tags createTag(String name) {
        Tags tag = new Tags();
        setField(tag, "name", name);
        return tag;
    }

    private Tags createTagWithNodename(String name, String nodename) {
        Tags tag = new Tags();
        setField(tag, "name", name);
        setField(tag, "nodename", nodename);
        return tag;
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
