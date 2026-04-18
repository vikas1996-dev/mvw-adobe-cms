package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleDtoTest {

    private ArticleDto articleDto;

    @BeforeEach
    void setUp() {
        articleDto = new ArticleDto();
    }

    @Test
    void testGettersAndSetters() {
        articleDto.setName("Beach Vacation Guide");
        articleDto.setNodename("beach-vacation-guide");
        articleDto.setShortDescription("Your guide to beach vacations");
        articleDto.setUrl("https://example.com/article");
        articleDto.setCreated("2024-01-15");

        assertEquals("Beach Vacation Guide", articleDto.getName());
        assertEquals("beach-vacation-guide", articleDto.getNodename());
        assertEquals("Your guide to beach vacations", articleDto.getShortDescription());
        assertEquals("https://example.com/article", articleDto.getUrl());
        assertEquals("2024-01-15", articleDto.getCreated());
    }

    @Test
    void testSetAndGetDcmTags() {
        List<Tags> tagsList = new ArrayList<>();
        Tags tag = new Tags();
        tagsList.add(tag);
        
        articleDto.setDcmTags(tagsList);
        assertEquals(tagsList, articleDto.getDcmTags());
    }

    @Test
    void testSetAndGetDestinations() {
        List<Destination> destinationsList = new ArrayList<>();
        Destination destination = new Destination();
        destinationsList.add(destination);
        
        articleDto.setDestinations(destinationsList);
        assertEquals(destinationsList, articleDto.getDestinations());
    }

    @Test
    void testSetAndGetImages() {
        Images images = new Images();
        articleDto.setImages(images);
        assertEquals(images, articleDto.getImages());
    }

    @Test
    void testSetAndGetVacationIdeas() {
        List<String> vacationIdeas = new ArrayList<>();
        vacationIdeas.add("Beach");
        vacationIdeas.add("Golf");
        
        articleDto.setVacationIdeas(vacationIdeas);
        assertEquals(vacationIdeas, articleDto.getVacationIdeas());
        assertEquals(2, articleDto.getVacationIdeas().size());
    }

    @Test
    void testToString() {
        articleDto.setName("Test Article");
        String toString = articleDto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ArticleDto"));
    }

    @Test
    void testNullValues() {
        assertNull(articleDto.getName());
        assertNull(articleDto.getNodename());
        assertNull(articleDto.getShortDescription());
        assertNull(articleDto.getUrl());
        assertNull(articleDto.getDcmTags());
        assertNull(articleDto.getDestinations());
        assertNull(articleDto.getImages());
        assertNull(articleDto.getVacationIdeas());
        assertNull(articleDto.getCreated());
    }
}
