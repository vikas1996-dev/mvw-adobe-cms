package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.utils.ImagePathUtils;

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

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationLandingPageEditorialArticalsImages {

    private static final Logger logger = LoggerFactory.getLogger(DestinationLandingPageEditorialArticalsImages.class);
    ObjectMapper mapper = new ObjectMapper();
    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        // Get base image path from OSGi service
        baseImagePath = jahiaApiConfigServiceImpl.getApiImagePath();
    }

    public List<DefaultModel> getPropertiesList() {
        JsonNode rootNode = (JsonNode) request.getAttribute("destinationLandingPageResponse");
        try {
            JsonNode nodes = rootNode.path("data")
                    .path("jcr")
                    .path("editorialImages")
                    .path("images")
                    .path("nodes");

            if (!nodes.isArray()) {
                logger.debug("No resort nodes found in GraphQL response");
                return Collections.emptyList();
            }

            List<DefaultModel> propertiesList = mapper.readValue(
                    nodes.toString(),
                    new TypeReference<List<DefaultModel>>() {
                    });

            appendImagePath(propertiesList);
            logger.info("Static COntent Jahia {}", propertiesList);
            return propertiesList;
        } catch (IOException e) {
            logger.error("Failed to parse resorts from GraphQL response", e);
            return Collections.emptyList();
        }
    }

    public void appendImagePath(List<DefaultModel> defaultModels) {
        if (defaultModels == null || baseImagePath == null)
            return;

        // Use reusable utility class
        for (DefaultModel defaultModel : defaultModels) {
            logger.info("Static COntent Jahia 123 {} path {}", defaultModel.getName(), defaultModel.getImagePath());
            ImagePathUtils.prependBasePath(defaultModel.getImages(), baseImagePath);
        }
    }
}
