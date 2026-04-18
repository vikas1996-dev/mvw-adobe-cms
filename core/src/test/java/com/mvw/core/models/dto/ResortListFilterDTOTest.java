package com.mvw.core.models.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResortListFilterDTOTest {

    private ResortListFilterDTO filterDto;

    @BeforeEach
    void setUp() {
        filterDto = new ResortListFilterDTO();
    }

    @Test
    void testSetAndGetRegions() {
        List<Tags> regions = new ArrayList<>();
        Tags region = new Tags();
        region.setName("Caribbean");
        regions.add(region);
        
        filterDto.setRegions(regions);
        
        assertEquals(regions, filterDto.getRegions());
    }

    @Test
    void testSetAndGetVacationTypes() {
        List<Tags> vacationTypes = new ArrayList<>();
        Tags type = new Tags();
        type.setName("Beach");
        vacationTypes.add(type);
        
        filterDto.setVacationTypes(vacationTypes);
        
        assertEquals(vacationTypes, filterDto.getVacationTypes());
    }

    @Test
    void testSetAndGetActivities() {
        List<Tags> activities = new ArrayList<>();
        Tags activity = new Tags();
        activity.setName("Golf");
        activities.add(activity);
        
        filterDto.setActivities(activities);
        
        assertEquals(activities, filterDto.getActivities());
    }

    @Test
    void testSetAndGetBrands() {
        List<Tags> brands = new ArrayList<>();
        Tags brand = new Tags();
        brand.setName("Marriott");
        brands.add(brand);
        
        filterDto.setBrands(brands);
        
        assertEquals(brands, filterDto.getBrands());
    }

    @Test
    void testNullValues() {
        assertNull(filterDto.getRegions());
        assertNull(filterDto.getVacationTypes());
        assertNull(filterDto.getActivities());
        assertNull(filterDto.getBrands());
    }
}
