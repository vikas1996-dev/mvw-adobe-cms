package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImagesTest {

    private Images images;

    @BeforeEach
    void setUp() {
        images = new Images();
    }

    @Test
    void testGettersAndSetters() {
        images.setName("Resort Image");
        images.setNodename("resort-image");
        images.setAltText("Beautiful resort view");
        images.setPriority("1");
        images.setReferenceId("ref-123");

        assertEquals("Resort Image", images.getName());
        assertEquals("resort-image", images.getNodename());
        assertEquals("Beautiful resort view", images.getAltText());
        assertEquals("1", images.getPriority());
        assertEquals("ref-123", images.getReferenceId());
    }

    @Test
    void testSetAndGetPhoto() {
        List<Photo> photoList = new ArrayList<>();
        Photo photo = new Photo();
        photo.setPath("/content/dam/photo.jpg");
        photoList.add(photo);
        
        images.setPhoto(photoList);
        
        assertEquals(photoList, images.getPhoto());
        assertEquals(1, images.getPhoto().size());
    }

    @Test
    void testSetAndGetReferenceID() {
        DefaultModel referenceID = new DefaultModel();
        referenceID.setName("main");
        
        images.setReferenceID(referenceID);
        
        assertEquals(referenceID, images.getReferenceID());
        assertEquals("main", images.getReferenceID().getName());
    }

    // @Test
    // void testAltTextAsObject() {
    //     // Test altText can be any object
    //     images.setAltText("123");
    //     assertEquals(123, images.getAltText());
        
    //     images.setAltText(new ArrayList<String>());
    //     assertTrue(images.getAltText() instanceof ArrayList);
    // }

    @Test
    void testToString() {
        images.setName("Test Image");
        images.setNodename("test-image");
        images.setPriority("5");
        
        String toString = images.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("Images"));
        assertTrue(toString.contains("Test Image"));
        assertTrue(toString.contains("test-image"));
    }

    @Test
    void testNullValues() {
        assertNull(images.getName());
        assertNull(images.getNodename());
        assertNull(images.getAltText());
        assertNull(images.getPriority());
        assertNull(images.getPhoto());
        assertNull(images.getReferenceId());
        assertNull(images.getReferenceID());
    }
}
