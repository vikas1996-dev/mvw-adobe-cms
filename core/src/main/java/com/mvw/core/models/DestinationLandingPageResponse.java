package com.mvw.core.models;

import java.util.List;

import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.models.dto.FeaturedResort;
import com.mvw.core.models.dto.Promotions;
import com.mvw.core.models.dto.ReferenceProperty;
import com.mvw.core.utils.ImagePathUtils;

public class DestinationLandingPageResponse {

    private List<String> regions;
    private List<DestinationLandingDto> destinations;
    private List<FeaturedResort> featuredResorts;
    private List<DefaultModel> vacations;
    private List<Promotions> defaultPromotions;

    public List<DefaultModel> getVacations() {
        return vacations;
    }

    public void setVacations(List<DefaultModel> vacations) {
        this.vacations = vacations;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<DestinationLandingDto> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationLandingDto> destinations) {
        this.destinations = destinations;
    }

    public List<FeaturedResort> getFeaturedResorts() {
        return featuredResorts;
    }

    public void setFeaturedResorts(List<FeaturedResort> featuredResorts) {
        this.featuredResorts = featuredResorts;
    }

    public List<Promotions> getDefaultPromotions() {
        return defaultPromotions;
    }

    public void setDefaultPromotions(List<Promotions> defaultPromotions) {
        this.defaultPromotions = defaultPromotions;
    }

    /**
     * Apply base path to all nested image paths.
     */
    public void applyBaseImagePath(String basePath) {

        // Destinations
        if (destinations != null) {

            for (DestinationLandingDto destination : destinations) {

                ImagePathUtils.prependBasePath(destination.getImages(), basePath);

                if (destination.getReferenceProperty() != null) {
                    for (ReferenceProperty ref : destination.getReferenceProperty()) {

                        ImagePathUtils.prependBasePath(ref.getImages(), basePath);

                        // Handle brand logo images
                        if (ref.getDcmBrand() != null) {
                            ImagePathUtils.prependBasePath(
                                    ref.getDcmBrand().getImages(),
                                    basePath
                            );
                        }
                    }
                }
            }
        }

        // Featured Resorts
        if (featuredResorts != null) {

            for (FeaturedResort resort : featuredResorts) {

                if (resort.getReferenceProperty() != null) {
                    for (ReferenceProperty ref : resort.getReferenceProperty()) {

                        ImagePathUtils.prependBasePath(ref.getImages(), basePath);

                        if (ref.getDcmBrand() != null) {
                            ImagePathUtils.prependBasePath(
                                    ref.getDcmBrand().getImages(),
                                    basePath
                            );
                        }
                    }
                }
            }
        }

         if (vacations != null) {
            for (DefaultModel vacation : vacations) {
                ImagePathUtils.prependBasePath(vacation.getImages(), basePath);
            }
        }
    }
}