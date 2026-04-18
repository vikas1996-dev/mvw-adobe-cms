package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Properties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = PropertiesListModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PropertiesListModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

    public List<Properties> getPropertiesList () throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("properties").path("nodes");

        List<Properties> propertiesList = mapper.readValue(propertiesNodesArray.toString(), new TypeReference<List<Properties>>() {});
logger.info("JSON received from Jahia propertiesList Properties:{}", propertiesList);
        return  propertiesList;
    }
}
