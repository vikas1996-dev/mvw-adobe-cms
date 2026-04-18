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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortOffersModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortOffersModel {

    private static final Logger logger = LoggerFactory.getLogger(ResortOffersModel.class);
    private static final String REQUIRED_PROMO = "sf-promo-1";

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        baseImagePath = jahiaApiConfigServiceImpl.getApiImagePath();
    }

    /**
     * Returns the list of promotions, including default promotions if sf-promo-1 is
     * missing.
     */
    public List<Promotions> getPropertiesList() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        if (jsonResponse == null) {
            logger.warn("No JSON response found in request attribute 'jahiaResponse'");
            return List.of();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode nodesArray = jsonResponse.path("data").path("jcr").path("promotions").path("nodes");

        List<Promotions> promotionsList = mapper.readValue(
                nodesArray.toString(),
                new TypeReference<List<Promotions>>() {
                });

        logger.info("Current promotions count: {}", promotionsList.size());

        // Check if REQUIRED_PROMO exists in any of the placements
        boolean hasRequiredPromo = promotionsList.stream()
                .anyMatch(p -> p.getPlacementId() != null &&
                        p.getPlacementId().stream()
                                .anyMatch(tag -> REQUIRED_PROMO.equals(tag.getNodename())));

        // If sf-promo-1 is missing, fetch default promotions
        if (!hasRequiredPromo) {
            logger.info("{} not found, fetching default promotions", REQUIRED_PROMO);
            List<Promotions> defaultPromotions = getDefaultOffer().stream()
                    .filter(p -> p.getPlacementId() != null &&
                            p.getPlacementId().stream()
                                    .anyMatch(tag -> REQUIRED_PROMO.equals(tag.getNodename())))
                    .collect(Collectors.toList());

            logger.info("Adding default promotions count: {}", defaultPromotions.size());
            promotionsList.addAll(defaultPromotions);
        }

        // Sort promotions: sf-promo-1 first, then by priority ascending
        promotionsList.sort(
                Comparator.<Promotions, Integer>comparing(p -> {
                    if (p.getPlacementId() != null &&
                            p.getPlacementId().stream().anyMatch(tag -> REQUIRED_PROMO.equals(tag.getNodename()))) {
                        return 0; // sf-promo-1 comes first
                    }
                    return 1; // others
                })
                        .thenComparingInt(p -> {
                            try {
                                return Integer.parseInt(p.getPriority());
                            } catch (NumberFormatException | NullPointerException e) {
                                return Integer.MAX_VALUE; // invalid/missing priority at the end
                            }
                        }));
        appendImagePath(promotionsList);
        return promotionsList;
    }

    /**
     * Fetches default promotions from JSON response.
     */
    public List<Promotions> getDefaultOffer() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        if (jsonResponse == null) {
            logger.warn("No JSON response found in request attribute 'jahiaResponse'");
            return List.of();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode defaultNodesArray = jsonResponse.path("data").path("jcr").path("defaultPromotions").path("nodes");

        List<Promotions> defaultList = mapper.readValue(
                defaultNodesArray.toString(),
                new TypeReference<List<Promotions>>() {
                });

        logger.info("defaultPromotions count: {}", defaultList.size());
        appendImagePath(defaultList);
        return defaultList;
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
