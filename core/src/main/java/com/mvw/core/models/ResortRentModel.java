package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.DefaultModel;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortRentModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortRentModel {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @SlingObject
        private SlingHttpServletRequest request;

        public List<DefaultModel> getPropertiesList() throws IOException {
                JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
                logger.info("JSON received from Jahia :{}", jsonResponse);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("mvcsResortDetailPage")
                                .path("copy").path("textFields");

                List<DefaultModel> propertiesList = mapper.readValue(propertiesNodesArray.toString(),
                                new TypeReference<List<DefaultModel>>() {
                                });
                JsonNode lableJson = jsonResponse.path("data").path("jcr").path("mvcsLabels").path("copy")
                                .path("textFields");
                List<DefaultModel> lables = mapper.readValue(lableJson.toString(),
                                new TypeReference<List<DefaultModel>>() {
                                });

                propertiesList.addAll(lables);
                return propertiesList;
        }

}
