package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.models.dto.Promotions;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.utils.ImagePathUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import java.util.Comparator;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationLandingPagePromos {

    private static final Logger logger = LoggerFactory.getLogger(DestinationLandingPagePromos.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        // Get base image path from OSGi service (null-safe for unit tests)
        baseImagePath = jahiaApiConfigServiceImpl != null ? jahiaApiConfigServiceImpl.getApiImagePath() : "";
    }

    public List<Promotions> getPropertiesList() {

        JsonNode rootNode = (JsonNode) request.getAttribute("destinationLandingPageResponse");
        logger.info("getEditorials {}", rootNode);

        try {
            JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("promotions")
                    .path("promoDestinations");

            if (!nodes.isArray()) {
                logger.debug("No promo nodes found in GraphQL response");
                return Collections.emptyList();
            }

            List<Promotions> promotions = mapper.readValue(
                    nodes.toString(),
                    new TypeReference<List<Promotions>>() {
                    });

            // Sort so promotion containing placementId.nodename = "sf-promo-1" comes first
            promotions.sort(Comparator.comparing(p -> !hasSfPromo(p)));
            appendImagePath(promotions);
            return promotions;

        } catch (IOException e) {
            logger.error("Failed to parse promotions from GraphQL response", e);
            return Collections.emptyList();
        }
    }

    /**
     * Checks if promotion contains placementId with nodename = "sf-promo-1"
     */
    private boolean hasSfPromo(Promotions promotion) {

        if (promotion == null || promotion.getPlacementId() == null) {
            return false;
        }

        return promotion.getPlacementId().stream()
                .anyMatch(placement -> placement != null &&
                        placement.getNodename() != null &&
                        "sf-promo-1".equalsIgnoreCase(placement.getNodename().trim()));
    }

    private void appendImagePath(List<Promotions> promotionsList) {

        String basePath = jahiaApiConfigServiceImpl != null ? jahiaApiConfigServiceImpl.getApiImagePath() : baseImagePath;

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
