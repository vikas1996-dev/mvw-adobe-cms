package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TripAdvisorDtoTest {

    private TripAdvisorDto tripAdvisorDto;

    @BeforeEach
    void setUp() {
        tripAdvisorDto = new TripAdvisorDto();
    }

    @Test
    void testGettersAndSetters() {
        tripAdvisorDto.setTripadvisorId("TA123456");
        tripAdvisorDto.setUniversalPropertyCode("UPC789");
        tripAdvisorDto.setRating("4.5");
        tripAdvisorDto.setReviews("1500");
        tripAdvisorDto.setRatingImage("https://tripadvisor.com/rating/4-5.png");

        assertEquals("TA123456", tripAdvisorDto.getTripadvisorId());
        assertEquals("UPC789", tripAdvisorDto.getUniversalPropertyCode());
        assertEquals("4.5", tripAdvisorDto.getRating());
        assertEquals("1500", tripAdvisorDto.getReviews());
        assertEquals("https://tripadvisor.com/rating/4-5.png", tripAdvisorDto.getRatingImage());
    }

    @Test
    void testNullValues() {
        assertNull(tripAdvisorDto.getTripadvisorId());
        assertNull(tripAdvisorDto.getUniversalPropertyCode());
        assertNull(tripAdvisorDto.getRating());
        assertNull(tripAdvisorDto.getReviews());
        assertNull(tripAdvisorDto.getRatingImage());
    }
}
