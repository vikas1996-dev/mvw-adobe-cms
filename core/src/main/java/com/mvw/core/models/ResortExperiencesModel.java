package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Activities;
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

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortExperiencesModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortExperiencesModel {

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

    public List<Activities> getPropertiesList() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        // logger.info("JSON received from Jahia :", jsonResponse);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("activities").path("nodes");

        List<Activities> propertiesList = mapper.readValue(propertiesNodesArray.toString(),
                new TypeReference<List<Activities>>() {
                });
        logger.info("JSON received from Jahia propertiesList Exper{}", propertiesList);
        appendImagePath(propertiesList);
        return propertiesList;
    }

    private void appendImagePath(List<Activities> activities) {
        if (activities == null || baseImagePath == null)
            return;

        // Use reusable utility class
        for (Activities activitiy : activities) {
            ImagePathUtils.prependBasePath(activitiy.getImages(), baseImagePath);
        }
    }
}
