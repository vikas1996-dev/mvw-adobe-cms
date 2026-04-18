package com.mvw.core.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.mvw.core.services.TripAdvisorService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.dto.TripAdvisorDto;
import com.mvw.core.models.dto.TripAdvisorResponseDto;
import com.mvw.core.services.TripAdvisorListService;

@Component(service = TripAdvisorListService.class, immediate = true)
public class TripAdvisorListServiceImpl implements TripAdvisorListService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(TripAdvisorListServiceImpl.class);
    @Reference
    private TripAdvisorService tripAdvisorService;
    private static final int TRIPADVISOR_THREAD_POOL_SIZE = 10;

    @Reference
    JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @Override
    public TripAdvisorResponseDto getResponseList() {
        try {
            ObjectNode variables = mapper.createObjectNode();
            variables.put("propertiesFolderNode", AppConstants.PROPERTIES_FOLDER_NODE);
            ObjectNode requestBody = mapper.createObjectNode();

            // Need to check if this approach works in cloud.
            InputStream is = getClass().getResourceAsStream("/graphql/tripAdvisorListQuery.graphql");
            String query = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            logger.info("query {}", query);

            requestBody.put(AppConstants.QUERY_LITERAL, query);
            requestBody.set(AppConstants.VARIABLES_LITERAL, variables);

            String jsonPayload = mapper.writeValueAsString(requestBody);

            // Build the HTTP request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jahiaApiConfigServiceImpl.getApiEndPoint()))
                    .header(AppConstants.CONTENTTYPE_LITERAL, AppConstants.CONTENTTYPE_APPLICATION_JSON)
                    .header(AppConstants.AUTHORIZATION_LITERAL, AppConstants.BEARER_LITERAL +
                            AppConstants.SPACE_LITERAL + jahiaApiConfigServiceImpl.getApiAuthToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("propertiesFolderNode")
                    .path("propertiesRoot")
                    .path("nodes");

            List<TripAdvisorDto> list = mapper.readValue(
                    nodes.toString(),
                    new TypeReference<List<TripAdvisorDto>>() {
                    });
            enrichWithTripAdvisorParallel(list);

            return getTripadvisor(list);
        } catch (InterruptedException e) {
            // Restore interrupted status and log
            Thread.currentThread().interrupt();
            logger.error("Thread was interrupted during GraphQL request", e);

        } catch (IOException e) {
            logger.error("I/O error during GraphQL request", e);
        }
        return null;

    }

    private void fetchTripAdvisorData(TripAdvisorDto resort) {
        String tripadvisorId = resort.getTripadvisorId();
        if (tripadvisorId == null || tripadvisorId.isEmpty())
            return;

        try {
            JsonNode taJson = mapper.readTree(tripAdvisorService.getLocationDetails(tripadvisorId));
            logger.warn("TripAdvisor  {}", taJson);
            resort.setRatingImage(taJson.path("rating_image_url").asText(null));
            resort.setRating(taJson.path("rating").asText(null));
            resort.setReviews(taJson.path("num_reviews").asText(null));
            resort.setWebUrl(taJson.path("web_url").asText(null));
        } catch (Exception e) {
            logger.warn("TripAdvisor fetch failed for id {}: {}", tripadvisorId, e.getMessage(), e);
        }
    }

    private void enrichWithTripAdvisorParallel(List<TripAdvisorDto> resorts) {
        ExecutorService executor = Executors.newFixedThreadPool(TRIPADVISOR_THREAD_POOL_SIZE);

        try {
            List<CompletableFuture<Void>> futures = resorts.stream()
                    .map(resort -> CompletableFuture.runAsync(() -> fetchTripAdvisorData(resort), executor))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public TripAdvisorResponseDto getTripadvisor(List<TripAdvisorDto> list) {

        TripAdvisorResponseDto dto = new TripAdvisorResponseDto();
        dto.setCount(list.size());
        dto.setList(list);
        return dto;
    }

    @Override
    public TripAdvisorDto getById(String id) {
        logger.info("getById {}", id);
        TripAdvisorDto resort = new TripAdvisorDto();
        try {
            JsonNode taJson = mapper.readTree(tripAdvisorService.getLocationDetails(id));
            resort.setRatingImage(taJson.path("rating_image_url").asText(null));
            resort.setRating(taJson.path("rating").asText(null));
            resort.setReviews(taJson.path("num_reviews").asText(null));
            resort.setTripadvisorId(id);
            logger.info("TripAdvisorService {}", resort.getReviews());
        } catch (Exception e) {
            logger.warn("TripAdvisor fetch failed for id {}: {}", id, e.getMessage(), e);
        }
        return resort;
    }
}
