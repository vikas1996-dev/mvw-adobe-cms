package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DestinationLandingDtoTest {

    private DestinationLandingDto dto;

    @BeforeEach
    void setUp() {
        dto = new DestinationLandingDto();
    }

    @Test
    void testGettersAndSetters() {
        dto.setName("Cancun");
        dto.setNodename("cancun");
        dto.setDescription("Beautiful destination");
        dto.setAltText("Cancun Beach");
        dto.setOrder("1");
        dto.setDestination("Mexico");

        assertEquals("Cancun", dto.getName());
        assertEquals("cancun", dto.getNodename());
        assertEquals("Beautiful destination", dto.getDescription());
        assertEquals("Cancun Beach", dto.getAltText());
        assertEquals("1", dto.getOrder());
        assertEquals("Mexico", dto.getDestination());
    }

    @Test
    void testSetAndGetImages() {
        List<Images> imagesList = new ArrayList<>();
        imagesList.add(new Images());
        dto.setImages(imagesList);
        assertEquals(imagesList, dto.getImages());
    }

    @Test
    void testSetAndGetRegion() {
        Region region = new Region();
        dto.setRegion(region);
        assertEquals(region, dto.getRegion());
    }

    @Test
    void testSetAndGetStructuredContent() {
        List<String> content = new ArrayList<>();
        content.add("Content 1");
        content.add("Content 2");
        dto.setStructuredContent(content);
        assertEquals(content, dto.getStructuredContent());
    }

    @Test
    void testSetReferencePropertySetsResortCount() {
        List<ReferenceProperty> properties = new ArrayList<>();
        properties.add(createReferenceProperty("Resort A"));
        properties.add(createReferenceProperty("Resort B"));
        properties.add(createReferenceProperty("Resort C"));
        
        dto.setReferenceProperty(properties);
        
        assertEquals(3, dto.getResortCount());
    }

    @Test
    void testSetReferencePropertyWithNullSetsZeroCount() {
        dto.setReferenceProperty(null);
        assertEquals(0, dto.getResortCount());
    }

    @Test
    void testSetReferencePropertyWithEmptyListSetsZeroCount() {
        dto.setReferenceProperty(new ArrayList<>());
        assertEquals(0, dto.getResortCount());
    }

    @Test
    void testGetReferencePropertySortsByNameCaseInsensitive() {
        List<ReferenceProperty> properties = new ArrayList<>();
        properties.add(createReferenceProperty("Zebra Resort"));
        properties.add(createReferenceProperty("alpha Resort"));
        properties.add(createReferenceProperty("Beta Resort"));
        
        dto.setReferenceProperty(properties);
        List<ReferenceProperty> sorted = dto.getReferenceProperty();
        
        assertEquals("alpha Resort", sorted.get(0).getName());
        assertEquals("Beta Resort", sorted.get(1).getName());
        assertEquals("Zebra Resort", sorted.get(2).getName());
    }

    @Test
    void testGetReferencePropertyHandlesNullNames() {
        List<ReferenceProperty> properties = new ArrayList<>();
        properties.add(createReferenceProperty("Resort A"));
        properties.add(createReferenceProperty(null));
        properties.add(createReferenceProperty("Resort B"));
        
        dto.setReferenceProperty(properties);
        List<ReferenceProperty> sorted = dto.getReferenceProperty();
        
        // Null names should be at the end
        assertEquals("Resort A", sorted.get(0).getName());
        assertEquals("Resort B", sorted.get(1).getName());
        assertNull(sorted.get(2).getName());
    }

    @Test
    void testGetReferencePropertyReturnsNullWhenNotSet() {
        assertNull(dto.getReferenceProperty());
    }

    @Test
    void testResortCountCanBeSetDirectly() {
        dto.setResortCount(10);
        assertEquals(10, dto.getResortCount());
    }

    @Test
    void testToString() {
        dto.setName("Test Destination");
        dto.setNodename("test-destination");
        String toString = dto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("DestinationLandingDto"));
        assertTrue(toString.contains("Test Destination"));
        assertTrue(toString.contains("test-destination"));
    }

    @Test
    void testNullValues() {
        assertNull(dto.getName());
        assertNull(dto.getNodename());
        assertNull(dto.getDescription());
        assertNull(dto.getImages());
        assertNull(dto.getRegion());
        assertNull(dto.getReferenceProperty());
        assertNull(dto.getStructuredContent());
        assertNull(dto.getAltText());
        assertNull(dto.getOrder());
        assertNull(dto.getDestination());
        assertEquals(0, dto.getResortCount());
    }

    // Helper method
    private ReferenceProperty createReferenceProperty(String name) {
        ReferenceProperty property = new ReferenceProperty();
        property.setName(name);
        return property;
    }
}
