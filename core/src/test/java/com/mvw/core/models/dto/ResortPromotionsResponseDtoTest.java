package com.mvw.core.models.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResortPromotionsResponseDtoTest {

    @Test
    void testDefaultConstructor() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        assertNull(dto.getCount());
        assertNull(dto.getResorts());
        assertNull(dto.getOfferCards());
    }

    @Test
    void testParameterizedConstructorWithCountResortsOffers() {
        List<ResortDto> resorts = new ArrayList<>();
        ResortDto resort = new ResortDto();
        resort.setName("Beach Resort");
        resorts.add(resort);
        
        List<Offer> offers = new ArrayList<>();
        Offer offer = new Offer();
        offer.setTitle("Summer Special");
        offers.add(offer);
        
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto(5, resorts, offers);
        
        assertEquals(Integer.valueOf(5), dto.getCount());
        assertEquals(resorts, dto.getResorts());
        assertEquals(offers, dto.getOfferCards());
    }

    @Test
    void testParameterizedConstructorWithList() {
        List<ResortListDto> list = new ArrayList<>();
        ResortListDto resortListDto = new ResortListDto();
        resortListDto.setName("Test Resort");
        list.add(resortListDto);
        
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto(list);
        
        assertEquals(list, dto.getList());
    }

    @Test
    void testSetAndGetCount() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        dto.setCount(10);
        assertEquals(Integer.valueOf(10), dto.getCount());
    }

    @Test
    void testSetAndGetResorts() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        List<ResortDto> resorts = new ArrayList<>();
        dto.setResorts(resorts);
        assertEquals(resorts, dto.getResorts());
    }

    @Test
    void testSetAndGetOfferCards() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        List<Offer> offers = new ArrayList<>();
        dto.setOfferCards(offers);
        assertEquals(offers, dto.getOfferCards());
    }

    @Test
    void testSetAndGetFilters() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        List<ResortListFilterDTO> filters = new ArrayList<>();
        dto.setFilters(filters);
        assertEquals(filters, dto.getFilters());
    }

    @Test
    void testSetAndGetList() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        List<ResortListDto> list = new ArrayList<>();
        dto.setList(list);
        assertEquals(list, dto.getList());
    }

    @Test
    void testSetAndGetLeftColumn() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        List<ColumnGroupDto> leftColumn = new ArrayList<>();
        dto.setLeftColumn(leftColumn);
        assertEquals(leftColumn, dto.getLeftColumn());
    }

    @Test
    void testSetAndGetRightColumn() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        List<ColumnGroupDto> rightColumn = new ArrayList<>();
        dto.setRightColumn(rightColumn);
        assertEquals(rightColumn, dto.getRightColumn());
    }

    @Test
    void testSetAndGetRegionFilter() {
        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto();
        List<RegionFilter> regionFilters = new ArrayList<>();
        dto.setRegionFilter(regionFilters);
        assertEquals(regionFilters, dto.getRegionFilter());
    }
}
