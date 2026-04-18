package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Alerts;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortAlertsModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortAlertsModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

    public List<Alerts> getPropertiesList() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("alerts").path("nodes");
        List<Alerts> propertiesList = mapper.readValue(propertiesNodesArray.toString(),
                new TypeReference<List<Alerts>>() {
                });
        return propertiesList;
    }
}
