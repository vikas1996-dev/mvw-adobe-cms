package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClubTest {

    private Club club;

    @BeforeEach
    void setUp() {
        club = new Club();
    }

    @Test
    void testGetters() {
        // Club only has getters, no setters - use reflection
        setField(club, "name", "Marriott Vacation Club");
        setField(club, "clubDescription", "Premium vacation membership");
        setField(club, "code", "MVC");

        assertEquals("Marriott Vacation Club", club.getName());
        assertEquals("Premium vacation membership", club.getClubDescription());
        assertEquals("MVC", club.getCode());
    }

    @Test
    void testNullValues() {
        assertNull(club.getName());
        assertNull(club.getClubDescription());
        assertNull(club.getCode());
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
