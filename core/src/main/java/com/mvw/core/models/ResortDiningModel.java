package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortDiningModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortDiningModel {

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

    /**
     * Returns a list of Dining objects, sorted by priority and with full image
     * paths.
     */
    public List<Dining> getPropertiesList() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        if (jsonResponse == null) {
            logger.warn("No Jahia response found for dining");
            return List.of();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode diningNodes = jsonResponse.path("data").path("jcr").path("dining").path("nodes");

        List<Dining> diningList = mapper.readValue(diningNodes.toString(),
                new TypeReference<List<Dining>>() {
                });
        diningList = diningList.stream()
                .filter(d -> "true".equalsIgnoreCase(d.getTypeOnOffSite()))
                .collect(Collectors.toList());

        logger.info("Dining list fetched from Jahia: {}", diningList);

        // Sort by priority
        sortByPriority(diningList);

        // Append base image path
        appendImagePath(diningList);

        return diningList;
    }

    /**
     * Sorts the dining list by numeric priority (ascending)
     */
    public static void sortByPriority(List<Dining> diningList) {
        if (diningList == null || diningList.isEmpty())
            return;

        diningList.sort(Comparator.comparingInt(d -> {
            try {
                return Integer.parseInt(d.getPriority());
            } catch (Exception e) {
                return Integer.MAX_VALUE;
            }
        }));
    }

    /**
     * Appends baseImagePath to all photo paths in the dining list.
     */
    public void appendImagePath(List<Dining> diningList) {
        if (diningList == null || baseImagePath == null)
            return;

        // Use reusable utility class
        for (Dining dining : diningList) {
            ImagePathUtils.prependBasePath(dining.getImages(), baseImagePath);
        }
    }
}