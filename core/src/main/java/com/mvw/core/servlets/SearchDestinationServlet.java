package com.mvw.core.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/mvw/experiences/searchDestinations")
public class SearchDestinationServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDestinationServlet.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Reference
    JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;
    

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            JsonNode rootNode = executeGraphQlRequest();
            List<DestinationLandingDto> destinations = extractDestinations(rootNode);

            response.setStatus(HttpServletResponse.SC_OK);
            MAPPER.writeValue(response.getWriter(), destinations);

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // ------------------------------------------------------------------
    // GraphQL Execution
    // ------------------------------------------------------------------

    private JsonNode executeGraphQlRequest() throws IOException, InterruptedException {

        ObjectNode requestBody = MAPPER.createObjectNode();
        requestBody.put("query", readGraphQlQuery());

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(jahiaApiConfigServiceImpl.getApiEndPoint()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jahiaApiConfigServiceImpl.getApiAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> httpResponse =
                HttpClient.newHttpClient()
                        .send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return MAPPER.readTree(httpResponse.body());
    }

    private String readGraphQlQuery() throws IOException {

        try (InputStream is =
                     getClass().getResourceAsStream("/graphql/searchDestinations.graphql")) {

            if (is == null) {
                throw new FileNotFoundException(
                        "GraphQL query file not found: /graphql/searchDestinations.graphql");
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // ------------------------------------------------------------------
    // Extract Destinations
    // ------------------------------------------------------------------

    private List<DestinationLandingDto> extractDestinations(JsonNode rootNode) {

        try {

            JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("destinations")
                    .path("propertiesRoot")
                    .path("nodes");

            if (!nodes.isArray()) {
                LOGGER.warn("No destination nodes found in GraphQL response");
                return Collections.emptyList();
            }

            return MAPPER.readValue(
                    nodes.toString(),
                    new TypeReference<List<DestinationLandingDto>>() {});

        } catch (Exception e) {
            LOGGER.error("Failed to parse destination response", e);
            return Collections.emptyList();
        }
    }

    // ------------------------------------------------------------------
    // Error Handling
    // ------------------------------------------------------------------

    private void handleError(SlingHttpServletResponse response, Throwable t) {

        LOGGER.error("SearchDestinationServlet failed", t);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        try {
            response.getWriter().write("{\"error\":\"Unable to fetch destinations\"}");
        } catch (IOException e) {
            LOGGER.error("Failed to write error response", e);
        }
    }
}
