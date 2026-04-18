package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultModelTest {

    private DefaultModel defaultModel;

    @BeforeEach
    void setUp() {
        defaultModel = new DefaultModel();
    }

    @Test
    void testGettersAndSetters() {
        defaultModel.setName("Test Model");
        defaultModel.setNodename("test-model");
        defaultModel.setPath("/content/test");
        defaultModel.setDescription("Test description");
        defaultModel.setProperties("test-property");

        assertEquals("Test Model", defaultModel.getName());
        assertEquals("test-model", defaultModel.getNodename());
        assertEquals("/content/test", defaultModel.getPath());
        assertEquals("Test description", defaultModel.getDescription());
        assertEquals("test-property", defaultModel.getProperties());
    }

    @Test
    void testSetAndGetImages() {
        List<Images> imagesList = new ArrayList<>();
        imagesList.add(new Images());
        defaultModel.setImages(imagesList);
        assertEquals(imagesList, defaultModel.getImages());
    }

    @Test
    void testGetImagePathWithNullImages() {
        assertNull(defaultModel.getImagePath());
    }

    @Test
    void testGetImagePathWithEmptyImages() {
        defaultModel.setImages(new ArrayList<>());
        assertNull(defaultModel.getImagePath());
    }

    @Test
    void testGetImagePathWithValidImage() {
        List<Images> imagesList = new ArrayList<>();
        Images img = createImageWithPhoto("/content/dam/test.jpg");
        imagesList.add(img);
        defaultModel.setImages(imagesList);
        
        assertEquals("/content/dam/test.jpg", defaultModel.getImagePath());
    }

    @Test
    void testGetImagePathWithNullPhotoInImage() {
        List<Images> imagesList = new ArrayList<>();
        Images img = new Images(); // No photo set
        imagesList.add(img);
        defaultModel.setImages(imagesList);
        
        assertNull(defaultModel.getImagePath());
    }

    @Test
    void testGetImagePathWithEmptyPhotoList() {
        List<Images> imagesList = new ArrayList<>();
        Images img = new Images();
        setField(img, "photo", new ArrayList<>());
        imagesList.add(img);
        defaultModel.setImages(imagesList);
        
        assertNull(defaultModel.getImagePath());
    }

    @Test
    void testGetImagePathReturnsFirstValidPath() {
        List<Images> imagesList = new ArrayList<>();
        Images imgNoPhoto = new Images(); // No photo
        Images imgWithPhoto = createImageWithPhoto("/content/dam/first-valid.jpg");
        imagesList.add(imgNoPhoto);
        imagesList.add(imgWithPhoto);
        defaultModel.setImages(imagesList);
        
        assertEquals("/content/dam/first-valid.jpg", defaultModel.getImagePath());
    }

    @Test
    void testGetImagePathWithMultiplePhotos() {
        List<Images> imagesList = new ArrayList<>();
        Images img = createImageWithMultiplePhotos("/content/dam/first.jpg", "/content/dam/second.jpg");
        imagesList.add(img);
        defaultModel.setImages(imagesList);
        
        // Should return first photo path
        assertEquals("/content/dam/first.jpg", defaultModel.getImagePath());
    }

    @Test
    void testNullValues() {
        assertNull(defaultModel.getName());
        assertNull(defaultModel.getNodename());
        assertNull(defaultModel.getPath());
        assertNull(defaultModel.getDescription());
        assertNull(defaultModel.getProperties());
        assertNull(defaultModel.getImages());
    }

    // Helper methods
    private Images createImageWithPhoto(String photoPath) {
        Images img = new Images();
        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        setField(photo, "path", photoPath);
        photos.add(photo);
        setField(img, "photo", photos);
        return img;
    }

    private Images createImageWithMultiplePhotos(String... paths) {
        Images img = new Images();
        List<Photo> photos = new ArrayList<>();
        for (String path : paths) {
            Photo photo = new Photo();
            setField(photo, "path", path);
            photos.add(photo);
        }
        setField(img, "photo", photos);
        return img;
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
