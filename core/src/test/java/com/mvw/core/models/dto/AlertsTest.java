package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertsTest {

    private Alerts alerts;

    @BeforeEach
    void setUp() {
        alerts = new Alerts();
    }

    @Test
    void testGetters() {
        // Alerts only has getters, no setters - use reflection
        setField(alerts, "name", "Weather Alert");
        setField(alerts, "nodename", "weather-alert");
        setField(alerts, "description", "Hurricane warning in effect");
        setField(alerts, "priority", "1");

        assertEquals("Weather Alert", alerts.getName());
        assertEquals("weather-alert", alerts.getNodename());
        assertEquals("Hurricane warning in effect", alerts.getDescription());
        assertEquals("1", alerts.getPriority());
    }

    @Test
    void testNullValues() {
        assertNull(alerts.getName());
        assertNull(alerts.getNodename());
        assertNull(alerts.getDescription());
        assertNull(alerts.getPriority());
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
