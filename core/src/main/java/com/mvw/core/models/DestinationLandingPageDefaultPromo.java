package com.mvw.core.models;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Promotions;
import com.mvw.core.services.DestinationLandingService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class DestinationLandingPageDefaultPromo {

    private static final Logger logger =
            LoggerFactory.getLogger(DestinationLandingPageDefaultPromo.class);

     @SlingObject
    private SlingHttpServletRequest request;
    ObjectMapper mapper = new ObjectMapper();

   public List<Promotions> getPropertiesList() {
         JsonNode rootNode = (JsonNode) request.getAttribute("destinationLandingPageResponse");
        logger.info("getEditorials {}", rootNode);
        try {
           JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("defaultPromotions")
                    .path("nodes");

            if (!nodes.isArray()) {
                logger.debug("No resort nodes found in GraphQL response");
                return Collections.emptyList();
            }

            return mapper.readValue(
                    nodes.toString(),
                    new TypeReference<List<Promotions>>() {
                    });

        } catch (IOException e) {
            logger.error("Failed to parse resorts from GraphQL response", e);
            return Collections.emptyList();
        }
    }

   
}
