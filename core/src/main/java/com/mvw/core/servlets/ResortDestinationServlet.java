package com.mvw.core.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.dto.ColumnGroupDto;
import com.mvw.core.models.dto.RegionFilter;
import com.mvw.core.models.dto.Tags;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.models.dto.ResortListDto;
import com.mvw.core.models.dto.ResortListFilterDTO;
import com.mvw.core.models.dto.ResortListResponseDTO;
import com.mvw.core.models.dto.ResortPromotionsResponseDto;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/mvw/resort-list")
public class ResortDestinationServlet extends SlingAllMethodsServlet {

        private static final ObjectMapper MAPPER = new ObjectMapper();
        private static final Logger LOGGER = LoggerFactory.getLogger(ResortDestinationServlet.class);

       @Reference
       JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

        @Override
        protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                        JsonNode rootNode = executeGraphQlRequest();
                        List<ResortListDto> resorts = extractResorts(rootNode);
                        ResortListResponseDTO result = buildResponse(resorts);
                        MAPPER.writeValue(response.getWriter(), result);
                } catch (Exception e) {
                        handleError(response, e);
                }
        }

        /* ---------------- GRAPHQL ---------------- */

        JsonNode executeGraphQlRequest() throws IOException, InterruptedException {
                ObjectNode body = buildGraphQlRequestBody();

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(jahiaApiConfigServiceImpl.getApiEndPoint()))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + jahiaApiConfigServiceImpl.getApiAuthToken())
                                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                                .build();

                HttpResponse<String> response = HttpClient.newHttpClient()
                                .send(request, HttpResponse.BodyHandlers.ofString());

                return MAPPER.readTree(response.body());
        }

        private ObjectNode buildGraphQlRequestBody() throws IOException {
                ObjectNode variables = MAPPER.createObjectNode();
                variables.put("propertiesFolderNode", AppConstants.PROPERTIES_FOLDER_NODE);

                ObjectNode body = MAPPER.createObjectNode();
                body.put("query", readGraphQlQuery());
                body.set("variables", variables);

                return body;
        }

        private String readGraphQlQuery() throws IOException {
                try (InputStream is = getClass()
                                .getResourceAsStream("/graphql/resortsListDestinationQuery.graphql")) {
                        if (is == null) {
                                throw new FileNotFoundException("GraphQL query not found");
                        }
                        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
        }

        /* ---------------- RESORTS ---------------- */

        List<ResortListDto> extractResorts(JsonNode rootNode) {
                try {
                        JsonNode nodes = rootNode.path("data")
                                        .path("jcr")
                                        .path("propertiesFolderNode")
                                        .path("propertiesRoot")
                                        .path("nodes");

                        if (!nodes.isArray())
                                return Collections.emptyList();

                        return MAPPER.readValue(
                                        nodes.toString(),
                                        new TypeReference<List<ResortListDto>>() {
                                        });
                } catch (Exception e) {
                        LOGGER.error("Failed to parse resorts", e);
                        return Collections.emptyList();
                }
        }

        ResortListResponseDTO buildResponse(List<ResortListDto> resorts) {

                /* Normalize DC */
                for (ResortListDto resort : resorts) {
                        if ("D.C.".equals(resort.getState()) && "Washington".equals(resort.getCity())) {
                                resort.setState("Washington, D.C.");
                        }
                }

                /* ================= LEFT COLUMN ================= */
                Map<String, Map<String, List<ResortListDto>>> leftGrouped = resorts.stream()
                                .filter(r -> "North America".equalsIgnoreCase(r.getContinent()))
                                .filter(r -> !"Mexico".equalsIgnoreCase(r.getCountry()))
                                .filter(r -> r.getState() != null)
                                .collect(Collectors.groupingBy(
                                                ResortListDto::getContinent,
                                                Collectors.groupingBy(ResortListDto::getState)));

                List<ColumnGroupDto> leftColumn = leftGrouped.entrySet().stream()
                                .flatMap(c -> c.getValue().entrySet().stream()
                                                .map(s -> new ColumnGroupDto(
                                                                c.getKey(),
                                                                s.getKey(),
                                                                sortResorts(s.getValue()))))
                                .sorted(Comparator.comparing(
                                                ColumnGroupDto::getSubHeader,
                                                String.CASE_INSENSITIVE_ORDER))
                                .collect(Collectors.toList());

                sortColumnGroupsByPriority(leftColumn);

                /* ================= RIGHT COLUMN ================= */
                Map<String, Map<String, List<ResortListDto>>> rightGrouped = resorts.stream()
                                .filter(r -> r.getCountry() != null)
                                .filter(r -> !("USA - East".equalsIgnoreCase(r.getRegion())
                                                || "USA - West".equalsIgnoreCase(r.getRegion())))
                                .collect(Collectors.groupingBy(
                                                r -> isMexico(r) ? r.getRegion() : r.getContinent(),
                                                Collectors.groupingBy(
                                                                r -> isMexico(r) ? r.getCity() : r.getCountry())));

                List<ColumnGroupDto> rightColumn = rightGrouped.entrySet().stream()
                                .flatMap(m -> m.getValue().entrySet().stream()
                                                .map(s -> new ColumnGroupDto(
                                                                m.getKey(),
                                                                s.getKey(),
                                                                sortResorts(s.getValue()))))
                                .sorted(Comparator
                                                .comparing(ColumnGroupDto::getMainHeader, String.CASE_INSENSITIVE_ORDER)
                                                .thenComparing(ColumnGroupDto::getSubHeader,
                                                                String.CASE_INSENSITIVE_ORDER))
                                .collect(Collectors.toList());

                sortColumnGroupsByPriority(rightColumn);

                /* ================= REGION FILTER (GROUPED) ================= */
                List<RegionFilter> regionFilters = Stream.concat(leftColumn.stream(), rightColumn.stream())
                                .filter(c -> c.getMainHeader() != null && c.getSubHeader() != null)
                                .collect(Collectors.groupingBy(
                                                ColumnGroupDto::getMainHeader,
                                                LinkedHashMap::new, // preserve header order
                                                Collectors.mapping(
                                                                ColumnGroupDto::getSubHeader,
                                                                Collectors.toCollection(LinkedHashSet::new) // unique
                                                                                                            // subHeaders
                                                )))
                                .entrySet()
                                .stream()
                                .map(entry -> {
                                        RegionFilter rf = new RegionFilter();
                                        rf.setHeader(entry.getKey());

                                        List<String> sortedSubHeaders = new ArrayList<>(entry.getValue());
                                        sortedSubHeaders.sort(String.CASE_INSENSITIVE_ORDER); // ✅ sort subHeaders

                                        rf.setSubHeader(sortedSubHeaders);
                                        return rf;
                                })
                                .collect(Collectors.toList());

                LOGGER.info("regionFilters {}", regionFilters);

                /* ================= BRAND FILTER ================= */
                List<Tags> brandsFilter = resorts.stream()
                                .map(ResortListDto::getDcmBrand)
                                .filter(Objects::nonNull)
                                .map(Tags::getName)
                                .filter(Objects::nonNull)
                                .distinct()
                                .map(this::toTag)
                                .sorted(Comparator.comparing(Tags::getName, String.CASE_INSENSITIVE_ORDER))
                                .collect(Collectors.toList());

                ResortListFilterDTO filters = new ResortListFilterDTO();
                filters.setBrands(brandsFilter);

                /* ================= RESPONSE ================= */
                ResortListResponseDTO dto = new ResortListResponseDTO();
                dto.setLeftColumn(leftColumn);
                dto.setRightColumn(rightColumn);
                dto.setBrandsFilter(brandsFilter);
                dto.setRegionFilter(regionFilters);

                return dto;
        }
        /* ---------------- HELPERS ---------------- */

        boolean isMexico(ResortListDto r) {
                return "North America".equalsIgnoreCase(r.getContinent())
                                && "Mexico".equalsIgnoreCase(r.getRegion());
        }

        private List<ResortListDto> sortResorts(List<ResortListDto> resorts) {

                return resorts.stream()
                                .sorted(Comparator.comparing(
                                                ResortListDto::getName,
                                                String.CASE_INSENSITIVE_ORDER))
                                .collect(Collectors.toList());
        }

        int parsePriority(String p) {
                try {
                        return Integer.parseInt(p);
                } catch (Exception e) {
                        return Integer.MAX_VALUE;
                }
        }

        private Tags toTag(String value) {
                Tags tag = new Tags();
                tag.setName(value);
                tag.setNodename(value);
                return tag;
        }

        private void handleError(SlingHttpServletResponse response, Throwable t) {
                LOGGER.error("Servlet failed", t);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                try {
                        response.getWriter().write("{\"error\":\"Unable to fetch resort list\"}");
                } catch (IOException ignored) {
                }
        }

        /**
         * Sorts a list of ColumnGroupDto based on the priority of the first resort in
         * each group.
         * Resorts without a valid priority are treated as having the lowest priority
         * (Integer.MAX_VALUE).
         *
         * @param columnGroups the list of ColumnGroupDto to sort
         */
        private void sortColumnGroupsByPriority(List<ColumnGroupDto> columnGroups) {
                columnGroups.sort(Comparator.comparingInt(group -> {
                        if (group.getResorts() == null || group.getResorts().isEmpty()) {
                                return Integer.MAX_VALUE;
                        }

                        ResortListDto resort = group.getResorts().get(0); // Take the first resort
                        if (resort.getLocale() == null || resort.getLocale().getPriority() == null) {
                                return Integer.MAX_VALUE;
                        }

                        try {
                                return Integer.parseInt(resort.getLocale().getPriority());
                        } catch (NumberFormatException e) {
                                return Integer.MAX_VALUE;
                        }
                }));
        }

}
