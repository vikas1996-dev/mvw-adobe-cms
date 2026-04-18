package com.mvw.core.models.dto;

import java.util.List;

public class ResortPromotionsResponseDto {

    private Integer count;
    private List<ResortDto> resorts;
    private List<Offer> offerCards;
    private List<ResortListFilterDTO> filters;
private List<ResortListDto> list;
private List<ColumnGroupDto> leftColumn;
    private List<ColumnGroupDto> rightColumn;
    private List<RegionFilter> regionFilter;
    public ResortPromotionsResponseDto(Integer count,
            List<ResortDto> resorts,
            List<Offer> offerCards) {
                this.count=count;
        this.resorts = resorts;
        this.offerCards = offerCards;
        
    }

    public ResortPromotionsResponseDto() {
    }

    public ResortPromotionsResponseDto(List<ResortListDto> list) {
        this.list = list;
    }

    public List<ColumnGroupDto> getLeftColumn() {
        return leftColumn;
    }

    public void setLeftColumn(List<ColumnGroupDto> leftColumn) {
        this.leftColumn = leftColumn;
    }

    public List<ColumnGroupDto> getRightColumn() {
        return rightColumn;
    }

    public void setRightColumn(List<ColumnGroupDto> rightColumn) {
        this.rightColumn = rightColumn;
    }

    public List<ResortDto> getResorts() {
        return resorts;
    }

    public List<Offer> getOfferCards() {
        return offerCards;
    }

    public Integer getCount() {
        return count;
    }

    public List<ResortListFilterDTO> getFilters() {
        return filters;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setResorts(List<ResortDto> resorts) {
        this.resorts = resorts;
    }

    public void setOfferCards(List<Offer> offerCards) {
        this.offerCards = offerCards;
    }

    public void setFilters(List<ResortListFilterDTO> filters) {
        this.filters = filters;
    }

    public List<ResortListDto> getList() {
        return list;
    }

    public void setList(List<ResortListDto> list) {
        this.list = list;
    }

    public List<RegionFilter> getRegionFilter() {
        return regionFilter;
    }

    public void setRegionFilter(List<RegionFilter> regionFilter) {
        this.regionFilter = regionFilter;
    }

    
}
