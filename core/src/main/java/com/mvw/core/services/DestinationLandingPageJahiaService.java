package com.mvw.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
@Component(service = DestinationLandingPageJahiaService.class)
public class DestinationLandingPageJahiaService {

        private final Logger logger = LoggerFactory.getLogger(getClass());
        private static final ObjectMapper mapper = new ObjectMapper();
        
        @Reference
        JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

        public HttpResponse<String> executeGraphQlRequest() throws IOException, InterruptedException {
                ObjectNode body = buildGraphQlRequestBody();

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(jahiaApiConfigServiceImpl.getApiEndPoint()))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + jahiaApiConfigServiceImpl.getApiAuthToken())
                                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                                .build();

                HttpResponse<String> response = HttpClient.newHttpClient()
                                .send(request, HttpResponse.BodyHandlers.ofString());
                logger.info("Destinations response{}", response);
                return response;
        }

        private ObjectNode buildGraphQlRequestBody() throws IOException {
                ObjectNode variables = mapper.createObjectNode();
                variables.put("editorialPath", AppConstants.EDITORIAL_PATH);
                variables.put("query1",
                                "SELECT p.* FROM [nt:base] AS p INNER JOIN [nt:base] AS pl ON p.[placementId] = pl.[jcr:uuid] WHERE ISDESCENDANTNODE(p, '/sites/vistana-digital-content-manager/contents/ads/mvcs') AND NAME(pl) = 'sf-destination-offers-1'");

                ObjectNode body = mapper.createObjectNode();
                body.put("query", readGraphQlQuery());
                body.set("variables", variables);

                return body;
        }

        private String readGraphQlQuery() throws IOException {
                try (InputStream is = getClass()
                                .getResourceAsStream("/graphql/destinationLandingPagePromo.graphql")) {
                        if (is == null) {
                                throw new FileNotFoundException("GraphQL query not found");
                        }
                        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
        }

}
