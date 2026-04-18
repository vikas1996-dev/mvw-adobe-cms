package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.models.dto.ReferenceProperty;
import com.mvw.core.models.dto.ResortDto;
import com.mvw.core.services.TripAdvisorEnrichmentService;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.utils.ImagePathUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, adapters = DestinationDetails.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationDetails {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private TripAdvisorEnrichmentService tripAdvisorEnrichmentService;

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        // Get base image path from OSGi service (null-safe for unit tests)
        baseImagePath = jahiaApiConfigServiceImpl != null ? jahiaApiConfigServiceImpl.getApiImagePath() : "";
    }

    public List<DestinationLandingDto> getPropertiesList() throws IOException {
        logger.info("Started DestinationLandingDto {}",baseImagePath);

        JsonNode jsonResponse = (JsonNode) request.getAttribute("destinationResponse");
        if (jsonResponse == null) {
            logger.warn("destinationResponse attribute is null");
            return List.of();
        }

        ObjectMapper mapper = new ObjectMapper();

        // ----------------------------
        // Parse Destinations
        // ----------------------------
        JsonNode destinationsNode = jsonResponse.path("data")
                .path("jcr")
                .path("destinations")
                .path("nodes");

        if (!destinationsNode.isArray()) {
            logger.warn("destinations.nodes is missing or not an array");
            return List.of();
        }

        List<DestinationLandingDto> propertiesList = mapper.readValue(
                destinationsNode.toString(),
                new TypeReference<List<DestinationLandingDto>>() {
                });
        logger.info("Loaded {} destination(s)", propertiesList.size());

        // ----------------------------
        // Enrich Destination Names
        // ----------------------------
        JsonNode namesNode = jsonResponse.path("data")
                .path("jcr")
                .path("propertiesFolderNode")
                .path("names")
                .path("nodes");

        if (namesNode.isArray()) {
            List<DefaultModel> names = mapper.readValue(
                    namesNode.toString(),
                    new TypeReference<List<DefaultModel>>() {
                    });

            // Convert names list to a map for faster lookup
            Map<String, String> nameMap = names.stream()
                    .filter(n -> n.getNodename() != null)
                    .collect(Collectors.toMap(DefaultModel::getNodename, DefaultModel::getName));

            propertiesList.forEach(dto -> {
                if (dto.getNodename() != null && nameMap.containsKey(dto.getNodename())) {
                    dto.setDestination(nameMap.get(dto.getNodename()));
                }
            });
        }

        // ----------------------------
        // Collect All Resorts with Tripadvisor ID
        // ----------------------------
        List<ResortDto> resorts = propertiesList.stream()
                .filter(dto -> dto.getReferenceProperty() != null)
                .flatMap(dto -> dto.getReferenceProperty().stream())
                .filter(ref -> ref.getTripadvisorId() != null)
                .map(ref -> {
                    ResortDto rdto = new ResortDto();
                    rdto.setUniversalPropertyCode(ref.getUniversalPropertyCode());
                    rdto.setTripadvisorId(ref.getTripadvisorId());
                    return rdto;
                })
                .collect(Collectors.toList());

        logger.info("Found {} resorts for TripAdvisor enrichment", resorts.size());

        if (resorts.isEmpty()) {
            return propertiesList;
        }

        // ----------------------------
        // Enrich With TripAdvisor
        // ----------------------------
        resorts = tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(resourceResolver, resorts);
        logger.info("TripAdvisor enrichment completed for {} resorts", resorts.size());

        // ----------------------------
        // Create Lookup Map
        // ----------------------------
        Map<String, ResortDto> resortMap = resorts.stream()
                .filter(r -> r.getTripadvisorId() != null)
                .collect(Collectors.toMap(ResortDto::getTripadvisorId, r -> r, (a, b) -> a));

        // ----------------------------
        // Inject Enriched Data Back
        // ----------------------------
        propertiesList.forEach(dto -> {
            if (dto.getReferenceProperty() != null) {
                dto.getReferenceProperty().forEach(ref -> {
                    if (ref.getTripadvisorId() != null) {
                        ResortDto enriched = resortMap.get(ref.getTripadvisorId());
                        if (enriched != null) {
                            ref.setRating(enriched.getRating());
                            ref.setReviews(enriched.getReviews());
                            ref.setWebUrl(enriched.getWebUrl());
                            ref.setRatingImage(enriched.getRatingImage());
                        }
                    }
                });
            }
        });

        logger.info("Successfully enriched DestinationLandingDto with TripAdvisor data {}", propertiesList);
        appendImagePath(propertiesList);
        return propertiesList;
    }

    public void appendImagePath(List<DestinationLandingDto> destinations) {
        logger.info("adding base path");
        if (destinations == null || baseImagePath == null)
            return;

        // Use reusable utility class
        for (DestinationLandingDto destination : destinations) {
            ImagePathUtils.prependBasePath(destination.getImages(), baseImagePath);
            List<ReferenceProperty> refs = destination.getReferenceProperty();
            if (refs != null) {
                for (ReferenceProperty ref : refs) {
                    ImagePathUtils.prependBasePath(ref.getImages(), baseImagePath);
                }
            }
        }
    }
}