package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Amenities;
import com.mvw.core.models.dto.Places;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ResortPlacesModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ResortPlacesModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

    public List<Places> getPropertiesList () throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        logger.info("JSON received from Jahia :{}", jsonResponse);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("places").path("nodes");

        List<Places> propertiesList = mapper.readValue(propertiesNodesArray.toString(), new TypeReference<List<Places>>() {});
 return  getParkingPlacesList(propertiesList);
    }

    public List<Places> getParkingPlacesList(List<Places> placesList) throws IOException {

    if (placesList == null || placesList.isEmpty()) {
        return Collections.emptyList();
    }

    return placesList.stream()
            .filter(place -> place.getNodename() != null)
            .filter(place -> place.getNodename().toLowerCase().contains("parking"))
            .collect(Collectors.toList());
}

}
