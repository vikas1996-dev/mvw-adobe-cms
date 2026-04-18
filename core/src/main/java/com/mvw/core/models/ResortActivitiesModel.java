package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Activities;
import com.mvw.core.models.dto.Villas;
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

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortActivitiesModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortActivitiesModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        baseImagePath = jahiaApiConfigServiceImpl.getApiImagePath();
    }

    @SlingObject
    private SlingHttpServletRequest request;

    public List<Activities> getPropertiesList() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("activities").path("nodes");

        List<Activities> propertiesList = mapper.readValue(propertiesNodesArray.toString(),
                new TypeReference<List<Activities>>() {
                });

        appendImagePath(propertiesList);
        return propertiesList;
    }

    /**
     * Appends baseImagePath to all photo paths in the dining list.
     */
    public void appendImagePath(List<Activities> activities) {
        if (activities == null || baseImagePath == null)
            return;

        // Use reusable utility class
        for (Activities activity : activities) {
            ImagePathUtils.prependBasePath(activity.getImages(), baseImagePath);
        }
    }
}
