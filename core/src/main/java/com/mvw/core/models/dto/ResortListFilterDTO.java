package com.mvw.core.models.dto;

import java.util.List;

public class ResortListFilterDTO {

    private List<Tags> regions;
    private List<Tags> vacationTypes;
    private List<Tags> activities;
    private List<Tags> brands;
    public List<Tags> getRegions() {
        return regions;
    }
    public void setRegions(List<Tags> regions) {
        this.regions = regions;
    }
    public List<Tags> getVacationTypes() {
        return vacationTypes;
    }
    public void setVacationTypes(List<Tags> vacationTypes) {
        this.vacationTypes = vacationTypes;
    }
    public List<Tags> getActivities() {
        return activities;
    }
    public void setActivities(List<Tags> activities) {
        this.activities = activities;
    }
    public List<Tags> getBrands() {
        return brands;
    }
    public void setBrands(List<Tags> brands) {
        this.brands = brands;
    }

    
    
}
