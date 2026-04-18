package com.mvw.core.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.ResortComparator;
import com.mvw.core.models.ResortListFilterModel;
import com.mvw.core.models.dto.*;
import com.mvw.core.services.TripAdvisorEnrichmentService;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.utils.ImagePathUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = {
                "mvw/components/tmvc/components/resortListingPage",
                "mvw/components/tmvc/components/standardPage"
        },
        methods = HttpConstants.METHOD_GET,
        selectors = "data",
        extensions = "json")
public class ResortListServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResortListServlet.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Reference
    private TripAdvisorEnrichmentService tripAdvisorEnrichmentService;

    @Reference
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            String collectionFilter = request.getParameter("collection");

            JsonNode rootNode = executeGraphQlRequest();
            List<ResortDto> resorts = extractResorts(rootNode, collectionFilter);

            tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(
                    request.getResourceResolver(),
                    resorts);

            ResortListFilterDTO filters = buildFilters(resorts);
            List<Offer> offers = extractOffers(rootNode);

            writeResponse(response, resorts, filters, offers, collectionFilter);

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // ----------------------------------------------------------
    // GraphQL
    // ----------------------------------------------------------

    private JsonNode executeGraphQlRequest() {

        ObjectNode requestBody = buildGraphQlRequestBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jahiaApiConfigServiceImpl.getApiEndPoint()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jahiaApiConfigServiceImpl.getApiAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return MAPPER.readTree(response.body());

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            LOGGER.error("Thread interrupted during GraphQL request", e);

        } catch (IOException e) {

            LOGGER.error("I/O error during GraphQL request", e);

        }

        return null;
    }

    private ObjectNode buildGraphQlRequestBody() {

        ObjectNode requestBody = null;

        try {

            ObjectNode variables = MAPPER.createObjectNode();
            variables.put("propertiesFolderNode", AppConstants.PROPERTIES_FOLDER_NODE);
            variables.put("query1", buildPromotionQuery());

            requestBody = MAPPER.createObjectNode();
            requestBody.put("query", readGraphQlQuery());
            requestBody.set("variables", variables);

        } catch (IOException e) {

            LOGGER.error("Failed to read GraphQL query", e);

        }

        return requestBody;
    }

    private String buildPromotionQuery() {

        return "SELECT promo.* FROM [jnt:content] AS promo " +
                "INNER JOIN [nt:base] AS placement ON promo.[placementId] = placement.[jcr:uuid] " +
                "WHERE ISDESCENDANTNODE(promo, '/sites/vistana-digital-content-manager/contents/ads/mvcs') " +
                "AND (NAME(placement) = 'sf-promo-1' OR NAME(placement) = 'sf-promo-2' OR NAME(placement) = 'sf-promo-3')";
    }

    private String readGraphQlQuery() throws IOException {

        try (InputStream is = getClass().getResourceAsStream("/graphql/resortsListQuery.graphql")) {

            if (is == null) {
                throw new FileNotFoundException("GraphQL query file not found");
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // ----------------------------------------------------------
    // Resorts
    // ----------------------------------------------------------

    private List<ResortDto> extractResorts(JsonNode rootNode, String collectionFilter) {

        try {

            JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("propertiesFolderNode")
                    .path("propertiesRoot")
                    .path("nodes");

            if (!nodes.isArray()) {
                return Collections.emptyList();
            }

            List<ResortDto> resorts = MAPPER.readValue(
                    nodes.toString(),
                    new TypeReference<List<ResortDto>>() {
                    });
            String basePath = jahiaApiConfigServiceImpl.getApiImagePath();

            /* Append base path to all resort images */
            for (ResortDto dto : resorts) {
                ImagePathUtils.prependBasePath(dto.getImages(), basePath);
            }
            return resorts.stream()
                    .filter(resort -> !hasDoNotShowFlag(resort))
                    .filter(resort -> {
                        if (StringUtils.isBlank(collectionFilter))
                            return true;

                        return resort.getCollection() != null &&
                                collectionFilter.equalsIgnoreCase(resort.getCollection().trim());
                    })
                    .sorted(ResortComparator.byLocaleAndSubLocalePriority())
                    .collect(Collectors.toList());

        } catch (IOException e) {

            LOGGER.error("Failed parsing resorts", e);
            return Collections.emptyList();

        }
    }

    private boolean hasDoNotShowFlag(ResortDto resort) {

        return resort.getFlags() != null &&
                resort.getFlags().stream()
                        .anyMatch(flag -> "do-not-show".equalsIgnoreCase(flag.getNodename()));
    }

    // ----------------------------------------------------------
    // Filters & Offers
    // ----------------------------------------------------------

    private ResortListFilterDTO buildFilters(List<ResortDto> resorts) {
        return new ResortListFilterModel().buildFilters(resorts);
    }

    private List<Offer> extractOffers(JsonNode rootNode) {

        JsonNode nodes = rootNode.path("data")
                .path("jcr")
                .path("promotions")
                .path("nodes");

        try {

            List<Promotions> promotions = MAPPER.readValue(
                    nodes.toString(),
                    new TypeReference<List<Promotions>>() {
                    });

            promotions.sort((p1, p2) -> Integer.compare(
                    Integer.parseInt(p1.getPriority()),
                    Integer.parseInt(p2.getPriority())));

            return promotions.stream()
                    .map(this::mapToOffer)
                    .collect(Collectors.toList());

        } catch (JsonProcessingException e) {

            LOGGER.error("Failed parsing promotions", e);
            return Collections.emptyList();
        }
    }

    private Offer mapToOffer(Promotions promo) {

        Offer offer = new Offer();

        offer.setTitle(promo.getShortDescriptionAd());
        offer.setEyebrow(promo.getName());
        offer.setButtonOfferTextAd(promo.getButtonOfferTextAd());
        offer.setButtonOfferUrlAd(promo.getButtonOfferUrlAd());
        offer.setDescriptionAd(promo.getDescriptionAd());
        offer.setButtonOfferTextAd1(promo.getButtonOfferTextAd1());
        offer.setButtonOfferUrlAd1(promo.getButtonOfferUrlAd1());
       offer.setBadge(promo.getBadge());
        if (promo.getImagesAd() != null) {

            offer.setImages(
                    promo.getImagesAd().stream()
                            .flatMap(this::mapImages)
                            .collect(Collectors.toList()));
        }

        return offer;
    }

    private java.util.stream.Stream<Offer.Image> mapImages(ImageAd imageAd) {

        String basePath = jahiaApiConfigServiceImpl.getApiImagePath();

        if (imageAd.getPhoto() == null) {

            return java.util.stream.Stream.of(
                    new Offer.Image(null, imageAd.getAltText(), null, null));
        }

        return imageAd.getPhoto().stream()
                .map(photo -> {

                    String path = photo.getPath();

                    if (path != null && !path.startsWith("http")) {
                        path = basePath + path;
                    }

                    return new Offer.Image(
                            photo.getName(),
                            imageAd.getAltText(),
                            path,
                            photo.getRatio());
                });
    }

    // ----------------------------------------------------------
    // Response / Error
    // ----------------------------------------------------------

    private void writeResponse(SlingHttpServletResponse response,
            List<ResortDto> resorts,
            ResortListFilterDTO filters,
            List<Offer> offers,
            String collectionFilter) {

        ResortPromotionsResponseDto dto = new ResortPromotionsResponseDto(resorts.size(), resorts, offers);

        dto.setFilters(List.of(filters));

        try {

            MAPPER.writeValue(response.getWriter(), dto);

        } catch (IOException e) {

            LOGGER.error("Failed writing response", e);

        }
    }

    private void handleError(SlingHttpServletResponse response, Throwable t) {

        LOGGER.error("Resort list servlet failed", t);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        try {

            response.getWriter().write("{\"error\":\"Unable to fetch resort list\"}");

        } catch (IOException ioException) {

            LOGGER.error("Failed writing error response", ioException);
        }
    }
}
