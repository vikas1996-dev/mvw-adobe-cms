package com.mvw.core.models.dto;

import java.util.List;

public class RegionFilter {
     private String header;
    private List<String> subHeader;
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public List<String> getSubHeader() {
        return subHeader;
    }
    public void setSubHeader(List<String> subHeader) {
        this.subHeader = subHeader;
    }
    @Override
    public String toString() {
        return "RegionFilter [header=" + header + ", subHeader=" + subHeader + "]";
    }

    
    
}
