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

@Model(adaptables = SlingHttpServletRequest.class, adapters = DestinationEditorialPromotions.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationEditorialPromotions {

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
        JsonNode nodes = jsonResponse.path("data")
                .path("jcr")
                .path("promotions")
                .path("promoDestinations");
        logger.info("JSON received from JsonNode {}", nodes);
        List<Promotions> propertiesList = mapper.readValue(nodes.toString(), new TypeReference<List<Promotions>>() {
        });
        logger.info("DestinationEditorialPromotions propertiesList {}", propertiesList);
        if (propertiesList != null && !propertiesList.isEmpty())
            propertiesList = filterEditorialPromotions(propertiesList);
        appendImagePath(propertiesList);
        return propertiesList;
    }

    public static List<Promotions> filterEditorialPromotions(
            List<Promotions> promotions) {

        if (promotions == null) {
            return List.of();
        }

        return promotions.stream()
                .filter(p -> p.getPath() != null)
                .filter(p -> p.getPath().toLowerCase().contains("editorial-"))
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
