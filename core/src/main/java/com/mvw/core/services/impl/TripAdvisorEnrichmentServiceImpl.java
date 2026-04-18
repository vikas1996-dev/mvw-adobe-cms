package com.mvw.core.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.dto.ResortDto;
import com.mvw.core.services.TripAdvisorEnrichmentService;
import com.mvw.core.utils.ContentFragmentUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = TripAdvisorEnrichmentService.class)
public class TripAdvisorEnrichmentServiceImpl implements TripAdvisorEnrichmentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final List<String> tripAdvisorCfKeys = List.of(
            AppConstants.TRIPADVISOR_CF_MODEL_KEY_TRIP_ADVISOR_ID,
            AppConstants.TRIPADVISOR_CF_MODEL_KEY_UNIVERSAL_PROPERTY_CODE,
            AppConstants.TRIPADVISOR_CF_MODEL_KEY_RATING_IMAGE,
            AppConstants.TRIPADVISOR_CF_MODEL_KEY_RATING,
            AppConstants.TRIPADVISOR_CF_MODEL_KEY_REVIEWS,
            AppConstants.TRIPADVISOR_CF_MODEL_KEY_WEB_URL
    );

    @Override
    public List<ResortDto> enrichWithTripAdvisorParallel(
            ResourceResolver resolver,
            List<ResortDto> resorts) {

        if (resorts == null || resorts.isEmpty()) {
            return resorts;
        }

        Map<String, Object> mainObjForTripAdvisorData =
                ContentFragmentUtils.getTripAdvisorContentFragmentData(
                        resolver,
                        "/content/dam/mvw/contents/references/tripadvisor",
                        tripAdvisorCfKeys
                );

        Object tripAdvisorDataObj = mainObjForTripAdvisorData.get("tripAdvisorData");

        if (tripAdvisorDataObj == null) {
            logger.warn("TripAdvisor CF data is null");
            return resorts;
        }

        // 🔥 Convert ONCE (important performance fix)
        JsonNode tripAdvisorDataNode = MAPPER.valueToTree(tripAdvisorDataObj);

        resorts.forEach(resort ->
                fetchTripAdvisorData(tripAdvisorDataNode, resort)
        );

        return resorts;
    }

    private void fetchTripAdvisorData(JsonNode tripAdvisorDataNode, ResortDto resort) {

        String tripadvisorId = resort.getTripadvisorId();

        if (StringUtils.isBlank(tripadvisorId) || tripAdvisorDataNode == null) {
            return;
        }

        Optional<JsonNode> matchingNode =
                StreamSupport.stream(tripAdvisorDataNode.spliterator(), false)
                        .filter(node -> tripadvisorId.equals(node.path("tripAdvisorId").asText()))
                        .findFirst();

        if (matchingNode.isPresent()) {

            JsonNode taJson = matchingNode.get();

            resort.setWebUrl(taJson.path("webUrl").asText(null));
            resort.setRatingImage(taJson.path("ratingImage").asText(null));
            resort.setRating(taJson.path("rating").asText(null));
            resort.setReviews(taJson.path("reviews").asText(null));

            logger.debug("TripAdvisor enriched for id {}", tripadvisorId);

        } else {
            logger.info("TripAdvisorId {} not found in CF data", tripadvisorId);
        }
    }
}