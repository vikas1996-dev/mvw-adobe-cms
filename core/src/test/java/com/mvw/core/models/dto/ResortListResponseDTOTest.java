package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResortListResponseDTOTest {

    private ResortListResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new ResortListResponseDTO();
    }

    @Test
    void testSetAndGetBrandsFilter() {
        List<Tags> brandsFilter = new ArrayList<>();
        Tags brand = new Tags();
        brand.setName("Marriott");
        brandsFilter.add(brand);
        
        responseDto.setBrandsFilter(brandsFilter);
        
        assertEquals(brandsFilter, responseDto.getBrandsFilter());
    }

    @Test
    void testSetAndGetLeftColumn() {
        List<ColumnGroupDto> leftColumn = new ArrayList<>();
        leftColumn.add(new ColumnGroupDto("North America", "USA", new ArrayList<>()));
        
        responseDto.setLeftColumn(leftColumn);
        
        assertEquals(leftColumn, responseDto.getLeftColumn());
    }

    @Test
    void testSetAndGetRightColumn() {
        List<ColumnGroupDto> rightColumn = new ArrayList<>();
        rightColumn.add(new ColumnGroupDto("Europe", "Spain", new ArrayList<>()));
        
        responseDto.setRightColumn(rightColumn);
        
        assertEquals(rightColumn, responseDto.getRightColumn());
    }

    @Test
    void testSetAndGetRegionFilter() {
        List<RegionFilter> regionFilters = new ArrayList<>();
        RegionFilter filter = new RegionFilter();
        filter.setHeader("Caribbean");
        regionFilters.add(filter);
        
        responseDto.setRegionFilter(regionFilters);
        
        assertEquals(regionFilters, responseDto.getRegionFilter());
    }

    @Test
    void testNullValues() {
        assertNull(responseDto.getBrandsFilter());
        assertNull(responseDto.getLeftColumn());
        assertNull(responseDto.getRightColumn());
        assertNull(responseDto.getRegionFilter());
    }
}
