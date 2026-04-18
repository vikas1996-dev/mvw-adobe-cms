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

@Component(service = JahiaClientService.class)
public class JahiaClientService {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Reference
        private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

        public HttpResponse<String> callGraphqlService(String upcCode) throws IOException {
                if (jahiaApiConfigServiceImpl == null) {
                    logger.error("JahiaApiConfigServiceImpl is not available");
                    return null;
                }
                logger.info("jahiaResponse attribute get by id");
                String resortDetailPage = "/sites/mvcs/contents/resort-detail-page";
                String nearbyDiningPath = "/sites/vistana/contents/sections/on-site-dining/resorts/" + upcCode;
                String defaultSmokingPolicyPath = "/sites/vistana/contents/properties/default/welcome-kit/resort-information/smoking-policy";
                String specificResortInfo = "/sites/vistana/contents/properties/" + upcCode
                                + "/welcome-kit/resort-information";
                String propertiesFolderNode = "/sites/vistana-digital-content-manager/contents/properties/properties";

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode variables = mapper.createObjectNode();
                variables.put("query1",
                                "SELECT promo.* FROM [jnt:content] AS promo INNER JOIN [dcm:properties] AS prop  ON promo.[referenceProperty] = prop.[jcr:uuid] WHERE ISDESCENDANTNODE(promo, '/sites/vistana-digital-content-manager/contents/ads/mvcs') AND prop.[universalPropertyCode] = '"
                                                + upcCode + "'");
                variables.put("query2", "SELECT * FROM [dcm:properties] as p where p.[universalPropertyCode] = '"
                                + upcCode
                                + "' and ISDESCENDANTNODE(p,'/sites/vistana-digital-content-manager/contents/properties/properties')");
                variables.put("query3",
                                "SELECT * FROM [dcm:amenities] as a inner join [dcm:properties] as p on a.[referenceProperty] = p.[jcr:uuid] where p.[universalPropertyCode] = '"
                                                + upcCode + "'");
                variables.put("query4",
                                "SELECT * FROM [dcm:villas] as a inner join [dcm:properties] as p on a.[referenceProperty] = p.[jcr:uuid] where p.[universalPropertyCode] = '"
                                                + upcCode + "'");
                variables.put("query5",
                                "SELECT * FROM [dcm:activities] as a inner join [dcm:properties] as p on a.[referenceProperty] = p.[jcr:uuid] where p.[universalPropertyCode] = '"
                                                + upcCode
                                                + "' and ISDESCENDANTNODE(a,'/sites/vistana-digital-content-manager/contents/properties/activities/resort-experiences')");
                variables.put("query6",
                                "SELECT * FROM [dcm:dining] as a inner join [dcm:properties] as p on a.[referenceProperty] = p.[jcr:uuid] where p.[universalPropertyCode] = '"
                                                + upcCode + "'");
                variables.put("query7",
                                "SELECT * FROM [dcm:awards] as a inner join [dcm:properties] as p on a.[referenceProperty] = p.[jcr:uuid] where p.[universalPropertyCode] = '"
                                                + upcCode + "'");
                variables.put("query8",
                                "SELECT * FROM [dcm:places] as a inner join [dcm:properties] as p on a.[referenceProperty] = p.[jcr:uuid] where p.[universalPropertyCode] = '"
                                                + upcCode + "'");
                variables.put("query9",
                                "SELECT * FROM [dcm:alert] as a inner join [dcm:properties] as p on a.[referenceProperty] = p.[jcr:uuid] where p.[universalPropertyCode] = '"
                                                + upcCode + "'");
                variables.put("query10",
                                "SELECT promo.* FROM [jnt:content] AS promo " +
                "INNER JOIN [nt:base] AS placement ON promo.[placementId] = placement.[jcr:uuid] " +
                "WHERE ISDESCENDANTNODE(promo, '/sites/vistana-digital-content-manager/contents/ads/mvcs') " +
                "AND (NAME(placement) = 'sf-promo-1')");                                
                variables.put("nearbyDiningPath", nearbyDiningPath);
                variables.put("defaultSmokingPolicyPath", defaultSmokingPolicyPath);
                variables.put("specificResortInfo", specificResortInfo);
                variables.put("propertiesFolderNode", propertiesFolderNode);
                variables.put("resortDetailPage", resortDetailPage);
                
                // Create the request body
                ObjectNode requestBody = mapper.createObjectNode();

                // Need to check if this approach works in cloud.
                InputStream is = getClass().getResourceAsStream("/graphql/resortDetailQuery.graphql");
                String query = new String(is.readAllBytes(), StandardCharsets.UTF_8);
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

                // Send the request and get the response
                HttpResponse<String> response = null;
                try {
                        response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        logger.info("Response {}", response);
                        return response;

                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Sonar-compliant
                        logger.error("Thread interrupted while calling Jahia GraphQL service for UPC: {}", upcCode, e);
                } catch (IOException e) {
                        logger.error("IO error while calling Jahia GraphQL service for UPC: {}", upcCode, e);
                }
                return response;
        }

        
}
