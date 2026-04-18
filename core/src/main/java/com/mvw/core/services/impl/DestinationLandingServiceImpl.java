package com.mvw.core.services.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.DestinationLandingPageResponse;
import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.models.dto.FeaturedResort;
import com.mvw.core.models.dto.ReferenceProperty;
import com.mvw.core.models.dto.Region;
import com.mvw.core.services.DestinationLandingService;

@Component(service = DestinationLandingService.class, immediate = true)
public class DestinationLandingServiceImpl implements DestinationLandingService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DestinationLandingServiceImpl.class);

    @Reference
    JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @Override
    public DestinationLandingPageResponse getDestinationResponse() {

        DestinationLandingPageResponse response = new DestinationLandingPageResponse();

        try {
            // EXECUTE GRAPHQL ONLY ONCE
            JsonNode rootNode = executeGraphQlRequest();
            List<DestinationLandingDto> destinations = extractResorts(rootNode);

            destinations.sort(
                    Comparator.comparingInt(d -> Optional.ofNullable(d.getRegion())
                            .map(Region::getOrder)
                            .map(Integer::parseInt)
                            .orElse(Integer.MAX_VALUE)));

            response.setDestinations(destinations);
            response.setRegions(getRegions(destinations));
            response.setVacations(getVacationTypes(rootNode));
            response.setFeaturedResorts(groupFeaturedResortsByRegionUnique(destinations));

        } catch (IOException | InterruptedException e) {
            logger.error("Error building destination response", e);
            Thread.currentThread().interrupt();
        }

        return response;
    }

    // ============================================================
    // GRAPHQL EXECUTION
    // ============================================================

    private JsonNode executeGraphQlRequest() throws IOException, InterruptedException {

        ObjectNode body = buildGraphQlRequestBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jahiaApiConfigServiceImpl.getApiEndPoint()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jahiaApiConfigServiceImpl.getApiAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readTree(response.body());
    }

    private ObjectNode buildGraphQlRequestBody() throws IOException {

        ObjectNode variables = mapper.createObjectNode();
        variables.put("path", AppConstants.DESTINATIONS);
        variables.put("vacationsPath", AppConstants.VACATIONS_PATH);
        ObjectNode body = mapper.createObjectNode();
        body.put("query", readGraphQlQuery());
        body.set("variables", variables);

        return body;
    }

    private String readGraphQlQuery() throws IOException {
        try (InputStream is = getClass()
                .getResourceAsStream("/graphql/destinationLandingPage.graphql")) {

            if (is == null) {
                throw new FileNotFoundException("GraphQL query not found");
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // ============================================================
    // EXTRACTION METHODS
    // ============================================================

    private List<DestinationLandingDto> extractResorts(JsonNode rootNode) {

        try {
            JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("propertiesFolderNode")
                    .path("propertiesRoot")
                    .path("nodes");

            if (!nodes.isArray()) {
                return Collections.emptyList();
            }

            return mapper.readValue(
                    nodes.toString(),
                    new TypeReference<List<DestinationLandingDto>>() {
                    });
        } catch (IOException e) {
            logger.error("Failed to parse resorts", e);
            return Collections.emptyList();
        }
    }

    private List<DefaultModel> getVacationTypes(JsonNode rootNode) {

        try {
            JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("vacationTypes")
                    .path("children")
                    .path("nodes");
            logger.info("vacations nodes{}", nodes);
            if (!nodes.isArray()) {
                return Collections.emptyList();
            }
            List<DefaultModel> list = mapper.readValue(
                    nodes.toString(),
                    new TypeReference<List<DefaultModel>>() {
                    });
            logger.info("vacations list{}", list);
            return list;
        } catch (IOException e) {
            logger.error("Failed to parse vacation types", e);
            return Collections.emptyList();
        }
    }

    // ============================================================
    // BUSINESS LOGIC METHODS
    // ============================================================

    public List<String> getRegions(List<DestinationLandingDto> destinations) throws IOException {
        logger.info("regions");

        if (destinations == null || destinations.isEmpty()) {
            return Collections.emptyList();
        }
        return destinations.stream()
                .map(DestinationLandingDto::getRegion)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Region::getName,
                                r -> r,
                                (r1, r2) -> r1),
                        map -> map.values().stream()
                                .sorted(Comparator.comparingInt(r -> Optional.ofNullable(r.getOrder())
                                        .map(Integer::parseInt)
                                        .orElse(Integer.MAX_VALUE)))
                                .map(Region::getName)
                                .collect(Collectors.toList())));
    }

    public static List<FeaturedResort> groupFeaturedResortsByRegionUnique(
            List<DestinationLandingDto> destinations) {

        if (destinations == null) {
            return List.of();
        }

        return destinations.stream()
                .filter(d -> d.getRegion() != null)
                .filter(d -> d.getReferenceProperty() != null)
                .collect(Collectors.groupingBy(d -> d.getRegion().getNodename()))
                .values()
                .stream()
                .map(group -> {
                    FeaturedResort fr = new FeaturedResort();
                    fr.setRegion(group.get(0).getRegion());

                    Map<String, ReferenceProperty> uniqueResorts = group.stream()
                            .flatMap(d -> d.getReferenceProperty().stream())
                            .filter(rp -> "true".equalsIgnoreCase(rp.getFeatured()))
                            .collect(Collectors.toMap(
                                    ReferenceProperty::getMarshaCode,
                                    rp -> rp,
                                    (existing, duplicate) -> existing,
                                    LinkedHashMap::new));

                    fr.setReferenceProperty(new ArrayList<>(uniqueResorts.values()));
                    return fr;
                })
                .filter(fr -> fr.getReferenceProperty() != null && !fr.getReferenceProperty().isEmpty())
                .collect(Collectors.toList());
    }

}
