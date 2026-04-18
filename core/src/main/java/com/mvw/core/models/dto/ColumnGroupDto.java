package com.mvw.core.models.dto;

import java.util.List;

public class ColumnGroupDto {

    private String mainHeader;   // Continent
    private String subHeader;    // State (left) / Country (right)
    private List<ResortListDto> resorts;

    public ColumnGroupDto(String mainHeader, String subHeader, List<ResortListDto> resorts) {
        this.mainHeader = mainHeader;
        this.subHeader = subHeader;
        this.resorts = resorts;
    }

    public String getMainHeader() { return mainHeader; }
    public String getSubHeader() { return subHeader; }
    public List<ResortListDto> getResorts() { return resorts; }
}
