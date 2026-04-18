package com.mvw.core.models;

import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.models.dto.Promotions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DestinationDetailsPageResponseTest {

    private DestinationDetailsPageResponse response;

    @BeforeEach
    void setUp() {
        response = new DestinationDetailsPageResponse();
    }

    @Test
    void testGetAndSetDestinations() {
        List<DestinationLandingDto> destinations = new ArrayList<>();
        DestinationLandingDto dto1 = new DestinationLandingDto();
        DestinationLandingDto dto2 = new DestinationLandingDto();
        destinations.add(dto1);
        destinations.add(dto2);

        response.setDestinations(destinations);

        assertNotNull(response.getDestinations());
        assertEquals(2, response.getDestinations().size());
    }

    @Test
    void testGetAndSetFeaturedResorts() {
        List<Promotions> featuredResorts = new ArrayList<>();
        Promotions promo1 = new Promotions();
        Promotions promo2 = new Promotions();
        Promotions promo3 = new Promotions();
        featuredResorts.add(promo1);
        featuredResorts.add(promo2);
        featuredResorts.add(promo3);

        response.setFeaturedResorts(featuredResorts);

        assertNotNull(response.getFeaturedResorts());
        assertEquals(3, response.getFeaturedResorts().size());
    }

    @Test
    void testGetDestinationsWhenNull() {
        assertNull(response.getDestinations());
    }

    @Test
    void testGetFeaturedResortsWhenNull() {
        assertNull(response.getFeaturedResorts());
    }

    @Test
    void testSetEmptyDestinations() {
        List<DestinationLandingDto> emptyList = new ArrayList<>();
        response.setDestinations(emptyList);

        assertNotNull(response.getDestinations());
        assertTrue(response.getDestinations().isEmpty());
    }

    @Test
    void testSetEmptyFeaturedResorts() {
        List<Promotions> emptyList = new ArrayList<>();
        response.setFeaturedResorts(emptyList);

        assertNotNull(response.getFeaturedResorts());
        assertTrue(response.getFeaturedResorts().isEmpty());
    }

    @Test
    void testSetNullDestinations() {
        response.setDestinations(null);
        assertNull(response.getDestinations());
    }

    @Test
    void testSetNullFeaturedResorts() {
        response.setFeaturedResorts(null);
        assertNull(response.getFeaturedResorts());
    }
}
