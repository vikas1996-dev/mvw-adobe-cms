package com.mvw.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class NewClubVideoTest {

    private final AemContext context = new AemContext();

    private NewClubVideo model;

    @BeforeEach
    void setUp() {
        // Load JSON content into AEM mock context
        context.load().json("/NewClubVideo.json", "/content/newclub/us/en");

        // Get the resource from mock repository
        Resource resource = context.resourceResolver().getResource("/content/newclub/us/en/jcr:content/root/container/video");
        assertNotNull(resource, "Resource should not be null");

        // Adapt to model
        model = resource.adaptTo(NewClubVideo.class);
        assertNotNull(model, "Model adaptation should not return null");
    }


    @Test
    void getVideoId() {
        assertEquals("6371753913112", model.getVideoId());
    }

    @Test
    void getVideoUrl() {
        assertEquals("/content/dam/brightcove_assets/1441355349001/65c6970dac4c5d09737430f2/6371753913112.mp4",
            model.getVideoUrl());
    }

    @Test
    void getVideoAutoplay() {
        assertEquals("false", model.getVideoAutoplay());
    }

    @Test
    void getVideoControls() {
        assertEquals("true", model.getVideoControls());
    }
}