package com.mvw.core.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;

public class Villas {

    private String name;
    private String nodename;
    private String sleeps;
    private String squareFootage;
    private Object view;
    private List<Gallery> gallery;
    private List<Images> images;
    private String description;

    /* 🔥 NEW FIELDS FROM JAHIA */
    private List<Amenities> amenities;
    private String checkAvailabilityUrl;
    private String disclaimer;
    private String floorPlanUrl;



    private static final ObjectMapper MAPPER = new ObjectMapper();

    /* ---------------- GETTERS ---------------- */

    public String getName() { return name; }

    public String getNodename() { return nodename; }

    public String getSleeps() { return sleeps; }

    public String getSquareFootage() { return squareFootage; }

    public Object getView() { return view; }

    public List<Gallery> getGallery() { return gallery; }

    public List<Images> getImages() { return images; }

    public String getDescription() { return description; }

    public List<Amenities> getAmenities() { return amenities; }

    

     public void setName(String name) {
        this.name = name;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setSleeps(String sleeps) {
        this.sleeps = sleeps;
    }

    public void setSquareFootage(String squareFootage) {
        this.squareFootage = squareFootage;
    }

    public void setView(Object view) {
        this.view = view;
    }

    public void setGallery(List<Gallery> gallery) {
        this.gallery = gallery;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmenities(List<Amenities> amenities) {
        this.amenities = amenities;
    }

    public void setCheckAvailabilityUrl(String checkAvailabilityUrl) {
        this.checkAvailabilityUrl = checkAvailabilityUrl;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    

     public String getFloorPlanUrl() {
    if (images == null || images.isEmpty()) {
        return null;
    }

    return images.stream()
            .filter(Objects::nonNull)
            .filter(img -> img.getNodename() != null)
            .filter(img -> img.getNodename().contains("floorplan"))
            .filter(img -> img.getPhoto() != null && !img.getPhoto().isEmpty())
            .map(img -> img.getPhoto().get(0))
            .filter(Objects::nonNull)
            .map(Photo::getPath)
            .findFirst()
            .orElse(null);
}


    public String getCheckAvailabilityUrl() { return checkAvailabilityUrl; }

    public String getDisclaimer() { return disclaimer; }

    /* ---------------- JSON HELPERS FOR HTL → JS ---------------- */

    public String getGalleryJson() {
        try {
            return MAPPER.writeValueAsString(gallery);
        } catch (Exception e) {
            return "[]";
        }
    }

    public String getAmenitiesJson() {
        try {
            return MAPPER.writeValueAsString(amenities);
        } catch (Exception e) {
            return "[]";
        }
    }

    public void setFloorPlanUrl(String floorPlanUrl) {
        this.floorPlanUrl = floorPlanUrl;
    }

   
    
}
