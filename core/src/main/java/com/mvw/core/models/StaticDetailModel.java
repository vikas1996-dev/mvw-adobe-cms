package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.services.JahiaClientService;
import com.mvw.core.services.StaticContentService;

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
        StaticDetailModel.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StaticDetailModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private Resource resource;

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private StaticContentService staticContentService;

    @PostConstruct
    protected void init() {
        HttpResponse<String> staticResponse;
        try {
            // Get UPC code from page properties, fallback to default
            if(staticContentService!=null){
                logger.info("Static sdjfksdhfkjds h");
            }
            staticResponse = staticContentService.callGraphqlService();
            if (staticResponse != null && staticResponse.statusCode() == 200) {
                // Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode staticData = mapper.readTree(staticResponse.body());
                request.setAttribute("staticData", staticData);
                logger.info("Successfully set staticResponse {}", staticData);
            } else {
                logger.error("Failed to fetch data. Status code: {}",
                        staticResponse != null ? staticResponse.statusCode() : "null");
            }

        } catch (IOException e) {
            logger.error("IOException in DestinationResponse.init(): {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in DestinationResponse.init(): {}", e.getMessage(), e);
        }
    }

}