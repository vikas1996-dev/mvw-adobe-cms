package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.DefaultModel;
import com.mvw.core.models.dto.Dining;
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
import java.util.List;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, adapters = StaticContentModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StaticContentModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

    public List<DefaultModel> getPropertiesList() throws IOException {

        JsonNode jsonResponse = (JsonNode) request.getAttribute("staticData");
        logger.info("Static COntent Jahia jsonResponse {}", jsonResponse);
        ObjectMapper mapper = new ObjectMapper();

        // Destinations
        JsonNode destinationsNode = jsonResponse
                .path("data")
                .path("jcr")
                .path("staticContent")
                .path("textFields")
                .path("documents");

        List<DefaultModel> propertiesList = mapper.readValue(
                destinationsNode.toString(),
                new TypeReference<List<DefaultModel>>() {
                });
        appendImagePath(propertiesList);
        logger.info("Static COntent Jahia {}", propertiesList);
        return propertiesList;
    }

    public void appendImagePath(List<DefaultModel> defaultModels) {
        if (defaultModels == null || baseImagePath == null)
            return;

        // Use reusable utility class
        for (DefaultModel defaultModel : defaultModels) {
            ImagePathUtils.prependBasePath(defaultModel.getImages(), baseImagePath);
        }
    }

}
