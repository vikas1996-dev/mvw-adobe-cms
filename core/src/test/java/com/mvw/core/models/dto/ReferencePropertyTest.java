package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReferencePropertyTest {

    private ReferenceProperty referenceProperty;

    @BeforeEach
    void setUp() {
        referenceProperty = new ReferenceProperty();
    }

    @Test
    void testGettersAndSetters() {
        referenceProperty.setName("Beach Resort");
        referenceProperty.setNodename("beach-resort");
        referenceProperty.setFeatured("true");
        referenceProperty.setMarshaCode("ABC123");
        referenceProperty.setUniversalPropertyCode("UPC001");
        referenceProperty.setCity("Miami");
        referenceProperty.setState("FL");
        referenceProperty.setCountry("USA");
        referenceProperty.setContinent("North America");
        referenceProperty.setRegion("Southeast");

        assertEquals("Beach Resort", referenceProperty.getName());
        assertEquals("beach-resort", referenceProperty.getNodename());
        assertEquals("true", referenceProperty.getFeatured());
        assertEquals("ABC123", referenceProperty.getMarshaCode());
        assertEquals("UPC001", referenceProperty.getUniversalPropertyCode());
        assertEquals("Miami", referenceProperty.getCity());
        assertEquals("FL", referenceProperty.getState());
        assertEquals("USA", referenceProperty.getCountry());
        assertEquals("North America", referenceProperty.getContinent());
        assertEquals("Southeast", referenceProperty.getRegion());
    }

    @Test
    void testGetSlug() {
        setField(referenceProperty, "slug", "beach-resort-miami");
        assertEquals("beach-resort-miami", referenceProperty.getSlug());
    }

    @Test
    void testSetAndGetFlags() {
        List<Tags> tagsList = new ArrayList<>();
        Tags tag = new Tags();
        setField(tag, "nodename", "test-tag");
        tagsList.add(tag);
        
        referenceProperty.setFlags(tagsList);
        assertEquals(tagsList, referenceProperty.getFlags());
    }

    @Test
    void testSetAndGetImages() {
        List<Images> imagesList = new ArrayList<>();
        referenceProperty.setImages(imagesList);
        assertEquals(imagesList, referenceProperty.getImages());
    }

    @Test
    void testGetCollectionWithNullFlags() {
        assertNull(referenceProperty.getCollection());
    }

    @Test
    void testGetCollectionWithEmptyFlags() {
        referenceProperty.setFlags(new ArrayList<>());
        assertNull(referenceProperty.getCollection());
    }

    @Test
    void testGetCollectionWithValidCityCollectionFlag() {
        List<Tags> tagsList = new ArrayList<>();
        
        Tags cityCollectionTag = new Tags();
        setField(cityCollectionTag, "nodename", "city-collection");
        setField(cityCollectionTag, "name", "Miami Beach Collection");
        
        tagsList.add(cityCollectionTag);
        referenceProperty.setFlags(tagsList);
        
        assertEquals("Miami Beach Collection", referenceProperty.getCollection());
    }

    @Test
    void testGetCollectionWithNoCityCollectionFlag() {
        List<Tags> tagsList = new ArrayList<>();
        
        Tags otherTag = new Tags();
        setField(otherTag, "nodename", "other-tag");
        setField(otherTag, "name", "Other");
        
        tagsList.add(otherTag);
        referenceProperty.setFlags(tagsList);
        
        assertNull(referenceProperty.getCollection());
    }

    @Test
    void testGetMainPropertyPhotoPathWithNullImages() {
        assertNull(referenceProperty.getMainPropertyPhotoPath());
    }

    @Test
    void testGetMainPropertyPhotoPathWithEmptyImages() {
        referenceProperty.setImages(new ArrayList<>());
        assertNull(referenceProperty.getMainPropertyPhotoPath());
    }

    @Test
    void testGetMainPropertyPhotoPathWithValidImage() {
        List<Images> imagesList = new ArrayList<>();
        
        Images mainImage = new Images();
        setField(mainImage, "name", "Property, Main Photo");
        
        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        setField(photo, "path", "/content/dam/main-property.jpg");
        photos.add(photo);
        setField(mainImage, "photo", photos);
        
        imagesList.add(mainImage);
        referenceProperty.setImages(imagesList);
        
        assertEquals("/content/dam/main-property.jpg", referenceProperty.getMainPropertyPhotoPath());
    }

    @Test
    void testGetMainPropertyPhotoPathWithNoMainImage() {
        List<Images> imagesList = new ArrayList<>();
        
        Images otherImage = new Images();
        setField(otherImage, "name", "Pool Photo");
        
        imagesList.add(otherImage);
        referenceProperty.setImages(imagesList);
        
        assertNull(referenceProperty.getMainPropertyPhotoPath());
    }

    @Test
    void testToString() {
        referenceProperty.setName("Test Resort");
        String toString = referenceProperty.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ReferenceProperty"));
        assertTrue(toString.contains("Test Resort"));
    }

    @Test
    void testNullValues() {
        assertNull(referenceProperty.getName());
        assertNull(referenceProperty.getNodename());
        assertNull(referenceProperty.getFeatured());
        assertNull(referenceProperty.getMarshaCode());
        assertNull(referenceProperty.getUniversalPropertyCode());
        assertNull(referenceProperty.getCity());
        assertNull(referenceProperty.getState());
        assertNull(referenceProperty.getCountry());
        assertNull(referenceProperty.getContinent());
        assertNull(referenceProperty.getRegion());
        assertNull(referenceProperty.getImages());
        assertNull(referenceProperty.getMainPropertyPhotoPath());
        assertNull(referenceProperty.getFlags());
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
