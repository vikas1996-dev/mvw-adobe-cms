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

@Component(service = StaticContentService.class)
public class StaticContentService {

        private final Logger logger = LoggerFactory.getLogger(getClass());

         @Reference
        private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

        public HttpResponse<String> callGraphqlService() throws IOException {
                

                String endpoint = jahiaApiConfigServiceImpl.getApiEndPoint();
                String authToken = jahiaApiConfigServiceImpl.getApiAuthToken();
                logger.info("endpoint, token {}{}",endpoint,authToken);
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode variables = mapper.createObjectNode();
                
                
                // Create the request body
                ObjectNode requestBody = mapper.createObjectNode();

                // Need to check if this approach works in cloud.
                InputStream is = getClass().getResourceAsStream("/graphql/staticContent.graphql");
                String query = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                requestBody.put(AppConstants.QUERY_LITERAL, query);

                requestBody.set(AppConstants.VARIABLES_LITERAL, variables);

                String jsonPayload = mapper.writeValueAsString(requestBody);

                // Build the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(endpoint))
                                .header(AppConstants.CONTENTTYPE_LITERAL, AppConstants.CONTENTTYPE_APPLICATION_JSON)
                                .header(AppConstants.AUTHORIZATION_LITERAL, AppConstants.BEARER_LITERAL +
                                                AppConstants.SPACE_LITERAL + authToken)
                                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                                .build();

                // Send the request and get the response
                HttpResponse<String> response = null;
                try {
                        response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        logger.info("Response {}", response);
                        return response;

                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Sonar-compliant
                        logger.error("Thread interrupted while calling Jahia GraphQL service for UPC: {}", e);
                } catch (IOException e) {
                        logger.error("IO error while calling Jahia GraphQL service for UPC: {}", e);
                }
                return response;
        }

        
}
