package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VideoTest {

    private Video video;

    @BeforeEach
    void setUp() {
        video = new Video();
    }

    @Test
    void testGettersAndSetters() {
        video.setName("Resort Tour");
        video.setNodename("resort-tour");
        video.setAltText("A tour of the resort");
        video.setExternalRefId("ext-123");
        video.setReferenceId("ref-456");

        assertEquals("Resort Tour", video.getName());
        assertEquals("resort-tour", video.getNodename());
        assertEquals("A tour of the resort", video.getAltText());
        assertEquals("ext-123", video.getExternalRefId());
        assertEquals("ref-456", video.getReferenceId());
    }

    @Test
    void testSetAndGetThumbnail() {
        Thumbnail thumbnail = new Thumbnail();
        video.setThumbnail(thumbnail);
        assertEquals(thumbnail, video.getThumbnail());
    }

    @Test
    void testNullValues() {
        assertNull(video.getName());
        assertNull(video.getNodename());
        assertNull(video.getAltText());
        assertNull(video.getExternalRefId());
        assertNull(video.getReferenceId());
        assertNull(video.getThumbnail());
    }
}
