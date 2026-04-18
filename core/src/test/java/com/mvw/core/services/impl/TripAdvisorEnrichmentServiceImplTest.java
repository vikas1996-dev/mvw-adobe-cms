package com.mvw.core.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.models.dto.ResortDto;
import com.mvw.core.utils.ContentFragmentUtils;

@ExtendWith(MockitoExtension.class)
class TripAdvisorEnrichmentServiceImplTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TripAdvisorEnrichmentServiceImpl service;

    @Mock
    private ResourceResolver mockResourceResolver;

    @Mock
    private TripAdvisorDataConfigServiceImpl tripAdvisorDataConfigService;

    @BeforeEach
    void setUp() {
        service = new TripAdvisorEnrichmentServiceImpl();
//        service.tripAdvisorDataConfigService = tripAdvisorDataConfigService;

        // lenient stub avoids UnnecessaryStubbingException
        lenient().when(tripAdvisorDataConfigService.getCfStoredPath())
                 .thenReturn("/dummy/path");
    }

    // ---------------------------------------------------------
    // NULL / EMPTY
    // ---------------------------------------------------------

    @Test
    void testEnrichWithTripAdvisorParallel_NullResorts() {
        List<ResortDto> result =
            service.enrichWithTripAdvisorParallel(mockResourceResolver, null);
        assertNull(result);
    }

    @Test
    void testEnrichWithTripAdvisorParallel_EmptyResorts() {
        List<ResortDto> emptyList = Collections.emptyList();
        List<ResortDto> result =
            service.enrichWithTripAdvisorParallel(mockResourceResolver, emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // NO TRIPADVISOR ID
    // ---------------------------------------------------------

    @Test
    void testEnrichWithTripAdvisorParallel_ResortWithoutTripadvisorId() {

        try (MockedStatic<ContentFragmentUtils> mockedUtils = mockStatic(ContentFragmentUtils.class)) {

            mockedUtils.when(() ->
                    ContentFragmentUtils.getTripAdvisorContentFragmentData(
                        any(), anyString(), anyList()))
                .thenReturn(createEmptyTripAdvisorMap());

            ResortDto resort = new ResortDto();
            resort.setName("Test Resort");

            List<ResortDto> resorts = List.of(resort);

            List<ResortDto> result =
                service.enrichWithTripAdvisorParallel(mockResourceResolver, resorts);

            assertNull(result.get(0).getRating());
            assertNull(result.get(0).getWebUrl());
        }
    }

    // ---------------------------------------------------------
    // MATCH FOUND
    // ---------------------------------------------------------

    @Test
    void testEnrichWithTripAdvisorParallel_ResortWithMatchingTripadvisorId() {

        try (MockedStatic<ContentFragmentUtils> mockedUtils = mockStatic(ContentFragmentUtils.class)) {

            mockedUtils.when(() ->
                    ContentFragmentUtils.getTripAdvisorContentFragmentData(
                        any(), anyString(), anyList()))
                .thenReturn(createTripAdvisorMap(
                    "123", "4.5", "100",
                    "http://example.com",
                    "http://rating.png"));

            ResortDto resort = new ResortDto();
            resort.setTripadvisorId("123");

            List<ResortDto> result =
                service.enrichWithTripAdvisorParallel(
                    mockResourceResolver,
                    List.of(resort));

            assertEquals("4.5", result.get(0).getRating());
            assertEquals("100", result.get(0).getReviews());
            assertEquals("http://example.com", result.get(0).getWebUrl());
            assertEquals("http://rating.png", result.get(0).getRatingImage());
        }
    }

    // ---------------------------------------------------------
    // NO MATCH
    // ---------------------------------------------------------

    @Test
    void testEnrichWithTripAdvisorParallel_NoMatch() {

        try (MockedStatic<ContentFragmentUtils> mockedUtils = mockStatic(ContentFragmentUtils.class)) {

            mockedUtils.when(() ->
                    ContentFragmentUtils.getTripAdvisorContentFragmentData(
                        any(), anyString(), anyList()))
                .thenReturn(createTripAdvisorMap(
                    "999", "4.5", "100",
                    "http://example.com",
                    "http://rating.png"));

            ResortDto resort = new ResortDto();
            resort.setTripadvisorId("123");

            List<ResortDto> result =
                service.enrichWithTripAdvisorParallel(
                    mockResourceResolver,
                    List.of(resort));

            assertNull(result.get(0).getRating());
        }
    }

    // ---------------------------------------------------------
    // MULTIPLE RESORTS
    // ---------------------------------------------------------

    @Test
    void testEnrichWithTripAdvisorParallel_MultipleResorts() {

        try (MockedStatic<ContentFragmentUtils> mockedUtils = mockStatic(ContentFragmentUtils.class)) {

            mockedUtils.when(() ->
                    ContentFragmentUtils.getTripAdvisorContentFragmentData(
                        any(), anyString(), anyList()))
                .thenReturn(createMultipleTripAdvisorMap());

            ResortDto r1 = new ResortDto();
            r1.setTripadvisorId("111");

            ResortDto r2 = new ResortDto();
            r2.setTripadvisorId("222");

            ResortDto r3 = new ResortDto();

            List<ResortDto> result =
                service.enrichWithTripAdvisorParallel(
                    mockResourceResolver,
                    List.of(r1, r2, r3));

            assertEquals("4.0", result.get(0).getRating());
            assertEquals("4.5", result.get(1).getRating());
            assertNull(result.get(2).getRating());
        }
    }

    // ---------------------------------------------------------
    // HELPERS (Return Map<String,Object>)
    // ---------------------------------------------------------

    private Map<String, Object> createEmptyTripAdvisorMap() {
        ObjectNode node = MAPPER.createObjectNode();
        node.set("tripAdvisorData", MAPPER.createArrayNode());
        return convert(node);
    }

    private Map<String, Object> createTripAdvisorMap(
        String id, String rating, String reviews,
        String webUrl, String ratingImage) {

        ObjectNode main = MAPPER.createObjectNode();
        ArrayNode array = MAPPER.createArrayNode();

        ObjectNode data = MAPPER.createObjectNode();
        data.put("tripAdvisorId", id);
        data.put("rating", rating);
        data.put("reviews", reviews);
        data.put("webUrl", webUrl);
        data.put("ratingImage", ratingImage);

        array.add(data);
        main.set("tripAdvisorData", array);

        return convert(main);
    }

    private Map<String, Object> createMultipleTripAdvisorMap() {

        ObjectNode main = MAPPER.createObjectNode();
        ArrayNode array = MAPPER.createArrayNode();

        ObjectNode d1 = MAPPER.createObjectNode();
        d1.put("tripAdvisorId", "111");
        d1.put("rating", "4.0");

        ObjectNode d2 = MAPPER.createObjectNode();
        d2.put("tripAdvisorId", "222");
        d2.put("rating", "4.5");

        array.add(d1);
        array.add(d2);
        main.set("tripAdvisorData", array);

        return convert(main);
    }

    private Map<String, Object> convert(ObjectNode node) {
        return MAPPER.convertValue(
            node, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
    }
}