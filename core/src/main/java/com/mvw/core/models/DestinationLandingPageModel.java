package com.mvw.core.models;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.services.DestinationLandingPageJahiaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.http.HttpResponse;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {
        DestinationLandingPageModel.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationLandingPageModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private Resource resource;

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private DestinationLandingPageJahiaService jahiaClientService;
    
    private HttpResponse<String> jahiaResponse;

    @PostConstruct
    protected void init() {
        try {
              jahiaResponse = jahiaClientService.executeGraphQlRequest();

            if (jahiaResponse != null && jahiaResponse.statusCode() == 200) {
                // Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jahiaResponse.body());

                request.setAttribute("destinationLandingPageResponse", root);
                logger.info("Successfully set destinationLandingPageResponse {}", root);
            } else {
                logger.error("Failed to fetch Jahia data. Status code: {}",
                        jahiaResponse != null ? jahiaResponse.statusCode() : "null");
            }

        } catch (IOException e) {
            logger.error("IOException in DestinationResponse.init(): {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in DestinationResponse.init(): {}", e.getMessage(), e);
        }
    }

    
}