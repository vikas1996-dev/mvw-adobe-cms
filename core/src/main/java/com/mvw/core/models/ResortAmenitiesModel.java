package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Activities;
import com.mvw.core.models.dto.Amenities;
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
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ResortAmenitiesModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ResortAmenitiesModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

     @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        baseImagePath = jahiaApiConfigServiceImpl.getApiImagePath();
    }

    public List<Amenities> getPropertiesList () throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        logger.info("JSON received from Jahia :{}", jsonResponse);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("amenities").path("nodes");

        List<Amenities> propertiesList = mapper.readValue(propertiesNodesArray.toString(), new TypeReference<List<Amenities>>() {});
        propertiesList.sort(
        Comparator.comparingInt(a -> {
            try {
                return Integer.parseInt(a.getPriority());
            } catch (Exception e) {
                return Integer.MAX_VALUE; // push null/invalid priority to end
            }
        })
);
appendImagePath(propertiesList);
 return  propertiesList;
    }

    public List<Amenities> getFeaturedAmenities() {
      List<Amenities> propertiesList = null;
      try {
        propertiesList = getPropertiesList();
         return propertiesList.stream()
            .filter(a -> "true".equalsIgnoreCase(a.getFeatured()))
            .filter(a -> a.getImages() != null && !a.getImages().isEmpty())
            .limit(4) 
            .collect(Collectors.toList());
      } 
catch (IOException e) {
        e.printStackTrace();
      }
      appendImagePath(propertiesList);
      return propertiesList;
   
}
public List<Amenities> getRemainingAmenities() {
    List<Amenities> allAmenities =null;
    try {
        allAmenities = getPropertiesList();
        List<Amenities> featured = getFeaturedAmenities();

        Set<String> featuredKeys = featured.stream()
            .map(Amenities::getNodename)
            .collect(Collectors.toSet());

        return allAmenities.stream()
            .filter(a -> !featuredKeys.contains(a.getNodename()))
            .sorted(Comparator.comparing((Amenities a) -> hasIcon(a) ? 0 : 1)
                .thenComparingInt(a -> {
                    try {
                        return Integer.parseInt(a.getPriority());
                    } catch (Exception e) {
                        return Integer.MAX_VALUE;
                    }
                }))
            .collect(Collectors.toList());

    } catch (IOException e) {
        e.printStackTrace();
    }
    logger.info("Amenities allAmenities{}",allAmenities);
    return List.of();
}

private boolean hasIcon(Amenities amenity) {
    return amenity.getMvcsIcon() != null && !amenity.getMvcsIcon().trim().isEmpty();
}

/**
     * Appends baseImagePath to all photo paths in the dining list.
     */
    public void appendImagePath(List<Amenities> amenities) {
        if (amenities == null || baseImagePath == null)
            return;

        // Use reusable utility class
        for (Amenities amenity : amenities) {
            ImagePathUtils.prependBasePath(amenity.getImages(), baseImagePath);
        }
    }

}
