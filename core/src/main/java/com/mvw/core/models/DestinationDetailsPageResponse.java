package com.mvw.core.models;

import java.util.List;

import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.models.dto.Promotions;

public class DestinationDetailsPageResponse {

    
    private List<DestinationLandingDto> destinations;
    private List<Promotions> featuredResorts;
    public List<DestinationLandingDto> getDestinations() {
        return destinations;
    }
    public void setDestinations(List<DestinationLandingDto> destinations) {
        this.destinations = destinations;
    }
    public List<Promotions> getFeaturedResorts() {
        return featuredResorts;
    }
    public void setFeaturedResorts(List<Promotions> featuredResorts) {
        this.featuredResorts = featuredResorts;
    }

    
    
    
}
