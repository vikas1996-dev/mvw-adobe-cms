package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Dining;
import com.mvw.core.models.dto.DefaultModel;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ResortNearByDiningModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ResortNearByDiningModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResortNearByDiningModel.class);

    @SlingObject
    private SlingHttpServletRequest request;

    public List<DefaultModel> getPropertiesList() {

        try {
            Object jahiaResponseObj = request.getAttribute("jahiaResponse");

            if (!(jahiaResponseObj instanceof JsonNode)) {
                LOGGER.warn("jahiaResponse attribute is missing or not a JsonNode");
                return Collections.emptyList();
            }

            JsonNode jsonResponse = (JsonNode) jahiaResponseObj;
            LOGGER.debug("Received Jahia response for dining");

            JsonNode propertiesNodesArray = jsonResponse
                    .path("data")
                    .path("jcr")
                    .path("nearbyDining")
                    .path("copy")
                    .path("textFields");

            if (propertiesNodesArray.isMissingNode() || !propertiesNodesArray.isArray()) {
                LOGGER.warn("Near By Dining nodes array is missing or not an array in Jahia response");
                return Collections.emptyList();
            }

            ObjectMapper mapper = new ObjectMapper();
            List<DefaultModel> propertiesList = mapper.readValue(
                    propertiesNodesArray.toString(),
                    new TypeReference<List<DefaultModel>>() {}
            );

            LOGGER.info("Successfully mapped {} dining items", propertiesList.size());
            return propertiesList;

        } catch (Exception e) {
            LOGGER.error("Error while mapping Jahia dining response", e);
        }

        return Collections.emptyList();
    }
}
