package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ActivitiesTest {

    private Activities activities;

    @BeforeEach
    void setUp() {
        activities = new Activities();
    }

    @Test
    void testGetters() {
        // Set fields using reflection since there are no setters
        setField(activities, "name", "Pool Activities");
        setField(activities, "nodename", "pool-activities");
        setField(activities, "description", "Enjoy pool activities");
        setField(activities, "longDescription", "Full description of pool activities");
        setField(activities, "shortDescription", "Short desc");
        setField(activities, "icon", "pool-icon.png");
        setField(activities, "priority", "1");
        setField(activities, "activityType", "water");
        setField(activities, "activityCategories", "outdoor");
        setField(activities, "phone", "555-POOL");
        setField(activities, "hours", "9:00 AM - 6:00 PM");

        assertEquals("Pool Activities", activities.getName());
        assertEquals("pool-activities", activities.getNodename());
        assertEquals("Enjoy pool activities", activities.getDescription());
        assertEquals("Full description of pool activities", activities.getLongDescription());
        assertEquals("Short desc", activities.getShortDescription());
        assertEquals("pool-icon.png", activities.getIcon());
        assertEquals("1", activities.getPriority());
        assertEquals("water", activities.getActivityType());
        assertEquals("outdoor", activities.getActivityCategories());
        assertEquals("555-POOL", activities.getPhone());
        assertEquals("9:00 AM - 6:00 PM", activities.getHours());
    }

    @Test
    void testGetImages() {
        List<Images> imagesList = new ArrayList<>();
        Images image = new Images();
        imagesList.add(image);
        
        setField(activities, "images", imagesList);
        assertEquals(imagesList, activities.getImages());
    }

    @Test
    void testNullValues() {
        assertNull(activities.getName());
        assertNull(activities.getNodename());
        assertNull(activities.getImages());
        assertNull(activities.getDescription());
        assertNull(activities.getLongDescription());
        assertNull(activities.getShortDescription());
        assertNull(activities.getIcon());
        assertNull(activities.getPriority());
        assertNull(activities.getActivityType());
        assertNull(activities.getActivityCategories());
        assertNull(activities.getPhone());
        assertNull(activities.getHours());
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
