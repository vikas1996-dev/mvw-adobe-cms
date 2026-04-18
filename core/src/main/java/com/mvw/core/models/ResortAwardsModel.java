package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Activities;
import com.mvw.core.models.dto.Awards;
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
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortAwardsModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortAwardsModel {

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

    public List<Awards> getPropertiesList() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("awards").path("nodes");

        List<Awards> propertiesList = mapper.readValue(propertiesNodesArray.toString(),
                new TypeReference<List<Awards>>() {
                });
        logger.info("JSON received from Jahia propertiesList Awards{}", propertiesList);

        propertiesList = propertiesList.stream()
                .filter(place -> place.getNodename() != null)
                .filter(place -> place.getNodename().toLowerCase().contains("tripadvisor-certificate-of-excellence"))
                .collect(Collectors.toList());
        logger.info("JSON received from Jahia propertiesList propertiesList Awards{}", propertiesList);
        appendImagePath(propertiesList);
        return propertiesList;
    }

    public void appendImagePath(List<Awards> awards) {
        if (awards == null || baseImagePath == null) return;

        // Use reusable utility class
        for (Awards award : awards) {
            ImagePathUtils.prependBasePath(award.getImages(), baseImagePath);
        }
    }
}
