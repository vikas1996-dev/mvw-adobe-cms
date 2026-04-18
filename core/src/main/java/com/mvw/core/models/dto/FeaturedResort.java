package com.mvw.core.models.dto;

import java.util.List;

public class FeaturedResort {
    private Region region;
    private List<ReferenceProperty> referenceProperty;
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public List<ReferenceProperty> getReferenceProperty() {
        return referenceProperty;
    }
    public void setReferenceProperty(List<ReferenceProperty> referenceProperty) {
        this.referenceProperty = referenceProperty;
    }
    
}
