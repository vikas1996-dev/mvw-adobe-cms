package com.mvw.core.models.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnGroupDtoTest {

    @Test
    void testConstructorAndGetters() {
        List<ResortListDto> resorts = new ArrayList<>();
        ResortListDto resort = new ResortListDto();
        resort.setName("Beach Resort");
        resorts.add(resort);
        
        ColumnGroupDto columnGroup = new ColumnGroupDto("North America", "Florida", resorts);
        
        assertEquals("North America", columnGroup.getMainHeader());
        assertEquals("Florida", columnGroup.getSubHeader());
        assertEquals(resorts, columnGroup.getResorts());
        assertEquals(1, columnGroup.getResorts().size());
    }

    @Test
    void testConstructorWithNullValues() {
        ColumnGroupDto columnGroup = new ColumnGroupDto(null, null, null);
        
        assertNull(columnGroup.getMainHeader());
        assertNull(columnGroup.getSubHeader());
        assertNull(columnGroup.getResorts());
    }

    @Test
    void testConstructorWithEmptyResortsList() {
        ColumnGroupDto columnGroup = new ColumnGroupDto("Europe", "Spain", new ArrayList<>());
        
        assertEquals("Europe", columnGroup.getMainHeader());
        assertEquals("Spain", columnGroup.getSubHeader());
        assertTrue(columnGroup.getResorts().isEmpty());
    }
}
