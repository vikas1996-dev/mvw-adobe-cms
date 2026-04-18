package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Promotions;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, adapters = DestinationDetailsPromotions.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationDetailsPromotions {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @SlingObject
    private SlingHttpServletRequest request;

     @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        // Get base image path from OSGi service
        baseImagePath = jahiaApiConfigServiceImpl.getApiImagePath();
    }

   public List<Promotions> getPropertiesList() throws IOException {

    JsonNode jsonResponse = (JsonNode) request.getAttribute("destinationResponse");
    ObjectMapper mapper = new ObjectMapper();

    // Destination promotions
    JsonNode promoNodes = jsonResponse.path("data")
            .path("jcr")
            .path("promotions")
            .path("promoDestinations");

    List<Promotions> propertiesList = mapper.readValue(
            promoNodes.toString(),
            new TypeReference<List<Promotions>>() {}
    );

    if (propertiesList != null && !propertiesList.isEmpty()) {
        propertiesList = filterEditorialPromotions(propertiesList);
    }

    // Default promotions
    JsonNode defaultNodes = jsonResponse.path("data")
            .path("jcr")
            .path("defaultPromotions")
            .path("nodes");

    List<Promotions> defaultList = mapper.readValue(
            defaultNodes.toString(),
            new TypeReference<List<Promotions>>() {}
    );

    // Create final list with default promos first
    List<Promotions> finalList = new java.util.ArrayList<>();

    if (defaultList != null && !defaultList.isEmpty()) {
        finalList.addAll(defaultList);   // default first
    }

    if (propertiesList != null && !propertiesList.isEmpty()) {
        finalList.addAll(propertiesList); // destination after
    }
appendImagePath(finalList);
    return finalList;
}


    public static List<Promotions> filterEditorialPromotions(
            List<Promotions> promotions) {

        if (promotions == null) {
            return List.of();
        }

        return promotions.stream()
                .filter(p -> p.getPath() != null)
                .filter(p -> p.getPath().toLowerCase().contains("destination-promo"))
                .collect(Collectors.toList());
    }

     private void appendImagePath(List<Promotions> promotionsList) {

        String basePath = jahiaApiConfigServiceImpl.getApiImagePath();

        if (promotionsList == null || basePath == null) {
            return;
        }

        promotionsList.forEach(promo -> {

            if (promo.getImagesAd() != null) {

                promo.getImagesAd().forEach(image -> {

                    if (image.getPhoto() != null) {

                        image.getPhoto().forEach(photo -> {

                            String path = photo.getPath();

                            if (path != null) {
                                photo.setPath(basePath + path);
                            }

                        });

                    }

                });

            }

        });
    }

}
