package com.mvw.core.models.dto;

import java.util.List;

public class ResortListResponseDTO {

    private List<Tags> brandsFilter;
    private List<ColumnGroupDto> leftColumn;
    private List<ColumnGroupDto> rightColumn;
    private List<RegionFilter> regionFilter;

    public List<Tags> getBrandsFilter() {
        return brandsFilter;
    }

    public void setBrandsFilter(List<Tags> brandsFilter) {
        this.brandsFilter = brandsFilter;
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

    public List<RegionFilter> getRegionFilter() {
        return regionFilter;
    }

    public void setRegionFilter(List<RegionFilter> regionFilter) {
        this.regionFilter = regionFilter;
    }

}
