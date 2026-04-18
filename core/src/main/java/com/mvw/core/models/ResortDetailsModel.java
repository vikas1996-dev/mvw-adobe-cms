package com.mvw.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.Gallery;
import com.mvw.core.models.dto.Images;
import com.mvw.core.models.dto.Photo;
import com.mvw.core.models.dto.Properties;
import com.mvw.core.models.dto.ResortDto;
import com.mvw.core.models.dto.Video;
import com.mvw.core.services.TripAdvisorEnrichmentService;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;
import com.mvw.core.utils.ImagePathUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Model(adaptables = SlingHttpServletRequest.class, adapters = ResortDetailsModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortDetailsModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private TripAdvisorEnrichmentService tripAdvisorEnrichmentService;

    @OSGiService
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    private String baseImagePath;

    @PostConstruct
    protected void init() {
        baseImagePath = jahiaApiConfigServiceImpl.getApiImagePath();
    }

    public List<Properties> getPropertiesList() throws IOException {

        JsonNode jsonResponse = (JsonNode) request.getAttribute("jahiaResponse");
        if (jsonResponse == null) {
            logger.warn("jahiaResponse attribute is null");
            return Collections.emptyList();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNodesArray = jsonResponse.path("data")
                .path("jcr")
                .path("properties")
                .path("nodes");

        if (!propertiesNodesArray.isArray()) {
            logger.warn("properties.nodes is missing or not an array");
            return Collections.emptyList();
        }

        List<Properties> propertiesList = mapper.readValue(
                propertiesNodesArray.toString(),
                new TypeReference<List<Properties>>() {
                });
        if (propertiesList.isEmpty())
            return propertiesList;

        List<ResortDto> resorts = new ArrayList<>();
        for (Properties p : propertiesList) {
            ResortDto dto = new ResortDto();
            dto.setUniversalPropertyCode(p.getUniversalPropertyCode());
            dto.setTripadvisorId(p.getTripAdvisorId());
            resorts.add(dto);

            // Merge videos into galleries at their defined positions
            mergeVideosIntoGallery(p);

            // Append images from other galleries into the first gallery (ignore videos)
            appendOtherGalleriesImagesIntoFirst(p);
        }

        resorts = tripAdvisorEnrichmentService.enrichWithTripAdvisorParallel(resourceResolver, resorts);

        Map<String, ResortDto> resortMap = resorts.stream()
                .filter(r -> r.getTripadvisorId() != null)
                .collect(Collectors.toMap(
                        ResortDto::getTripadvisorId,
                        r -> r,
                        (a, b) -> a));

        for (Properties p : propertiesList) {
            ImagePathUtils.prependBasePath(p.getImages(), baseImagePath);
            if (p.getGallery() != null && !p.getGallery().isEmpty()) {
                Gallery g = p.getGallery().get(0);
                ImagePathUtils.prependBasePath(g.getImages(), baseImagePath);
                ImagePathUtils.prependBasePath(g.getModalImages(), baseImagePath);
                ImagePathUtils.prependBasePath(g.getSortedImages(), baseImagePath);
            }

            if (p.getTripAdvisorId() != null) {
                ResortDto enriched = resortMap.get(p.getTripAdvisorId());
                if (enriched != null) {
                    p.setRating(enriched.getRating());
                    p.setReviews(enriched.getReviews());
                    p.setRatingImage(enriched.getRatingImage());
                    p.setWebUrl(enriched.getWebUrl());
                }
            }
        }

        logger.info("Successfully enriched properties and merged galleries {}", propertiesList);
        return propertiesList;
    }

    private void mergeVideosIntoGallery(Properties p) {
        if (p.getVideos() == null || p.getVideos().isEmpty())
            return;
        if (p.getGallery() == null || p.getGallery().isEmpty())
            return;

        List<Images> videoImages = mapVideosToGalleryImages(p);
        if (videoImages.isEmpty())
            return;

        for (Gallery gallery : p.getGallery()) {
            if (gallery.getImages() == null)
                gallery.setImages(new ArrayList<>());
            gallery.getImages().addAll(videoImages); // Keep original positions intact
        }
    }

    /**
     * Appends only **non-video images** from all other galleries into the first
     * gallery.
     */
    private void appendOtherGalleriesImagesIntoFirst(Properties p) {
        if (p.getGallery() == null || p.getGallery().size() <= 1)
            return;

        Gallery firstGallery = p.getGallery().get(0);
        if (firstGallery.getImages() == null)
            firstGallery.setImages(new ArrayList<>());
        if (firstGallery.getModalImages() == null)
            firstGallery.setModalImages(new ArrayList<>());
        if (firstGallery.getSortedImages() == null)
            firstGallery.setSortedImages(new ArrayList<>());

        for (int i = 1; i < p.getGallery().size(); i++) {
            Gallery g = p.getGallery().get(i);
            if (g.getImages() != null) {
                for (Images img : g.getImages()) {
                    if (!img.isVideo()) { // skip videos
                        firstGallery.getImages().add(img);
                        firstGallery.getModalImages().add(img);
                        firstGallery.getSortedImages().add(img);
                    }
                }
            }
        }

        // Keep only the first gallery
        List<Gallery> newGalleryList = new ArrayList<>();
        newGalleryList.add(firstGallery);
        p.setGallery(newGalleryList);
    }

    public List<Images> mapVideosToGalleryImages(Properties p) {
        List<Images> images = new ArrayList<>();
        if (p.getVideos() == null || p.getVideos().isEmpty())
            return images;

        for (Video video : p.getVideos()) {
            if (video.getThumbnail() == null)
                continue;

            Images image = new Images();
            image.setName(video.getName());
            image.setNodename(video.getNodename());
            image.setAltText(video.getAltText());
            image.setVideo(true);
            image.setExternalRefId(video.getExternalRefId());
            image.setPriority("2"); // Keep video position unchanged

            Photo photo = new Photo();
            photo.setPath(baseImagePath + video.getThumbnail().getPath());
            photo.setWidth(video.getThumbnail().getWidth());
            photo.setHeight(video.getThumbnail().getHeight());
            photo.setRatio(video.getThumbnail().getRatio());

            image.setPhoto(Collections.singletonList(photo));
            images.add(image);
        }

        return images;
    }
}