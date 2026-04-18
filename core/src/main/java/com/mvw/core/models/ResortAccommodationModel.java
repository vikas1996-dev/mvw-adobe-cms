package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Gallery;
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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortAccommodationModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortAccommodationModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        baseImagePath = jahiaApiConfigServiceImpl != null ? jahiaApiConfigServiceImpl.getApiImagePath() : "";
    }

    public List<Villas> getPropertiesList() throws IOException {
        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        if (jsonResponse == null) {
            return new ArrayList<>();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data").path("jcr").path("villas").path("nodes");

        List<Villas> propertiesList = mapper.readValue(propertiesNodesArray.toString(),
                new TypeReference<List<Villas>>() {
                });
        for (Villas villa : propertiesList) {
           ImagePathUtils.prependBasePath(villa.getImages(), baseImagePath);
            if (villa.getGallery() != null) {
                for (Gallery gallery : villa.getGallery()) {
                    gallery.setImages(gallery.getSortedImages());
                    ImagePathUtils.prependBasePath(gallery.getImages(), baseImagePath);
                }
            }
        }
        return propertiesList;
    }

}
