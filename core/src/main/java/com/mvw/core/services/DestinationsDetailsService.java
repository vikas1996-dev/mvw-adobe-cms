package com.mvw.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component(service = DestinationsDetailsService.class)
public class DestinationsDetailsService {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Reference
        JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

        protected HttpClient getHttpClient() {
                return HttpClient.newHttpClient();
        }

        public HttpResponse<String> callGraphqlService(String name) throws IOException {
                logger.info("Response attribute get by id");

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode variables = mapper.createObjectNode();
                variables.put("query1", "SELECT * FROM [dcm:destinationsReferences] as results WHERE NAME(results) = '"
                                + name + "'");
                variables.put("query2",
                                "SELECT p.* FROM [nt:base] AS p INNER JOIN [dcm:destinationsReferences] AS d ON p.[destinations] = d.[jcr:uuid] WHERE ISDESCENDANTNODE(p, '/sites/vistana-digital-content-manager/contents/ads/mvcs') AND NAME(d) = '"
                                                + name + "'");
                variables.put("query3", "SELECT promo.* FROM [jnt:content] AS promo " +
                                "INNER JOIN [nt:base] AS placement ON promo.[placementId] = placement.[jcr:uuid] " +
                                "WHERE ISDESCENDANTNODE(promo, '/sites/vistana-digital-content-manager/contents/ads/mvcs') "
                                +
                                "AND (NAME(placement) = 'sf-promo-1')");
                variables.put("query4",
                                "SELECT * FROM [jnt:content] As node WHERE ISDESCENDANTNODE (node, '/sites/vistana-digital-content-manager/contents/vacation-ideas/articles')");
                variables.put("path", "/sites/vistana-digital-content-manager/contents/references/destinations");

                // Create the request body

                ObjectNode requestBody = mapper.createObjectNode();

                // Need to check if this approach works in cloud.
                InputStream is = getClass().getResourceAsStream("/graphql/destinationDetailPage.graphql");
                String query = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                requestBody.put(AppConstants.QUERY_LITERAL, query);

                requestBody.set(AppConstants.VARIABLES_LITERAL, variables);

                String jsonPayload = mapper.writeValueAsString(requestBody);

                // Build the HTTP request
                HttpClient client = getHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(jahiaApiConfigServiceImpl.getApiEndPoint()))
                                .header(AppConstants.CONTENTTYPE_LITERAL, AppConstants.CONTENTTYPE_APPLICATION_JSON)
                                .header(AppConstants.AUTHORIZATION_LITERAL, AppConstants.BEARER_LITERAL +
                                                AppConstants.SPACE_LITERAL
                                                + jahiaApiConfigServiceImpl.getApiAuthToken())
                                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                                .build();

                // Send the request and get the response
                HttpResponse<String> response = null;
                try {
                        response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        return response;

                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Sonar-compliant
                        logger.error("Thread interrupted while calling GraphQL service for UPC: {}", name, e);
                } catch (IOException e) {
                        logger.error("IO error while calling GraphQL service for UPC: {}", name, e);
                }
                return response;
        }

}
