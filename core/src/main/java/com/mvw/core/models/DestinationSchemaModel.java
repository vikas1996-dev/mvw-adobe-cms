package com.mvw.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import com.mvw.core.constants.AppConstants;
import com.mvw.core.models.dto.DestinationLandingDto;
import com.mvw.core.models.dto.ReferenceProperty;
import com.mvw.core.models.dto.Images;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class DestinationSchemaModel {

    private static final Logger LOG = LoggerFactory.getLogger(DestinationSchemaModel.class);

    @Self
    private DestinationDetails destinationDetails;

    @SlingObject
    private SlingHttpServletRequest request;

    private String jsonLD;

    @PostConstruct
    protected void init() {
        try {
            if (destinationDetails == null) {
                LOG.warn("DestinationDetails model is null for path: {}", request.getResource().getPath());
                this.jsonLD = "{}";
                return;
            }

            List<DestinationLandingDto> destinations = destinationDetails.getPropertiesList();

            if (destinations == null || destinations.isEmpty()) {
                LOG.warn("No destinations found for path: {}", request.getResource().getPath());
                this.jsonLD = "{}";
                return;
            }

            DestinationLandingDto destination = destinations.get(0);

            Map<String, Object> schema = buildSchema(destination);

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            this.jsonLD = gson.toJson(schema);

        } catch (Exception e) {
            LOG.error("Failed to initialize DestinationSchemaModel for path: {}",
                    request.getResource().getPath(), e);
            this.jsonLD = "{}";
        }
    }

    private Map<String, Object> buildSchema(DestinationLandingDto destination) {
        Map<String, Object> schema = new LinkedHashMap<>();

        String currentUrl = request.getRequestURL().toString();

        schema.put("@context", "https://schema.org");
        schema.put(AppConstants.TYPE, "TouristDestination");
        schema.put("@id", currentUrl);
        schema.put("name", destination.getName());
        schema.put("description", destination.getDescription());
        schema.put("url", currentUrl);

        List<String> images = buildImageList(destination);
        if (!images.isEmpty()) {
            schema.put("image", images);
        }

        Map<String, String> address = buildAddress(destination);
        if (!address.isEmpty()) {
            schema.put("address", address);
        }

        Map<String, Object> geo = buildGeoCoordinates(destination);
        if (!geo.isEmpty()) {
            schema.put("geo", geo);
        }

        return schema;
    }

    private List<String> buildImageList(DestinationLandingDto destination) {
        List<String> images = new ArrayList<>();

        List<Images> destinationImages = destination.getImages();
        if (destinationImages != null) {
            for (Images image : destinationImages) {
                if (image != null && image.getPhoto() != null && !image.getPhoto().isEmpty()) {
                    var photo = image.getPhoto().get(0);
                    if (photo != null && StringUtils.isNotBlank(photo.getPath())) {
                        images.add(photo.getPath());
                    }
                }
            }
        }

        return images;
    }

    private Map<String, String> buildAddress(DestinationLandingDto destination) {
        Map<String, String> address = new LinkedHashMap<>();

        List<ReferenceProperty> refProps = destination.getReferenceProperty();
        if (refProps != null && !refProps.isEmpty()) {
            ReferenceProperty firstProp = refProps.get(0);

            address.put(AppConstants.TYPE, "PostalAddress");

            if (StringUtils.isNotBlank(firstProp.getCity())) {
                address.put(AppConstants.ADDRESSLOCALITY, firstProp.getCity());
            }
            if (StringUtils.isNotBlank(firstProp.getState())) {
                address.put("addressRegion", firstProp.getState());
            }
            if (StringUtils.isNotBlank(firstProp.getCountry())) {
                address.put("addressCountry", firstProp.getCountry());
            }
        }

        if (address.size() <= 1 && StringUtils.isNotBlank(destination.getName())) {
            address.put(AppConstants.TYPE, "PostalAddress");

            String name = destination.getName();
            if (name.contains(",")) {
                String[] parts = name.split(",", 2);
                address.put("addressLocality", parts[0].trim());
                if (parts.length > 1) {
                    address.put("addressRegion", parts[1].trim());
                }
            } else {
                address.put("addressLocality", name);
            }
        }

        return address;
    }

    private Map<String, Object> buildGeoCoordinates(DestinationLandingDto destination) {
        Map<String, Object> geo = new LinkedHashMap<>();

        String latitude = destination.getCoordinateLatitude();
        String longitude = destination.getCoordinateLongitude();

        // Primary source
        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
            try {
                geo.put(AppConstants.TYPE, "GeoCoordinates");
                geo.put(AppConstants.LATITUDE, Double.parseDouble(latitude));
                geo.put(AppConstants.LONGITUDE, Double.parseDouble(longitude));
                return geo;
            } catch (NumberFormatException e) {
                LOG.warn("Invalid coordinate format: lat={}, lon={}", latitude, longitude);
            }
        }

        // Fallback: structuredContent
        List<String> structuredContent = destination.getStructuredContent();

        if (structuredContent != null && !structuredContent.isEmpty()) {

            Double lat = null;
            Double lon = null;

            for (String content : structuredContent) {
                if (StringUtils.isBlank(content)) {
                    continue;
                }

                try {
                    // Case 1: JSON format
                    if (content.trim().startsWith("{")) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(content);

                        if (node.has("geo")) {
                            JsonNode geoNode = node.get("geo");

                            if (geoNode.has(AppConstants.LATITUDE)) {
                                lat = geoNode.get(AppConstants.LATITUDE).asDouble();
                            }
                            if (geoNode.has(AppConstants.LONGITUDE)) {
                                lon = geoNode.get(AppConstants.LONGITUDE).asDouble();
                            }
                        }
                    }
                    // Case 2: key=value format
                    else if (content.contains("=")) {
                        String[] parts = content.split("=", 2);
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        if ("latitude".equalsIgnoreCase(key)) {
                            lat = Double.parseDouble(value);
                        } else if ("longitude".equalsIgnoreCase(key)) {
                            lon = Double.parseDouble(value);
                        }
                    }

                } catch (Exception e) {
                    LOG.debug("Error parsing structuredContent: {}", content);
                }
            }

            if (lat != null && lon != null) {
                geo.put(AppConstants.TYPE, "GeoCoordinates");
                geo.put(AppConstants.LATITUDE, lat);
                geo.put(AppConstants.LONGITUDE, lon);
            }
        }

        return geo;
    }

    public String getJsonLD() {
        return jsonLD;
    }
}