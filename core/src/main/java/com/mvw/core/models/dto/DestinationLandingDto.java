package com.mvw.core.models.dto;

import java.util.List;
import java.util.Comparator;

public class DestinationLandingDto {
    private String name;
    private String nodename;
    private String description;
    private List<Images> images;
    private Region region;
    private List<ReferenceProperty> referenceProperty;
    private List<String> structuredContent;
    private String altText;
    private String order;
private String destination;
    private int resortCount;
    private String coordinateLatitude;
    private String coordinateLongitude;

    

    // --- getters/setters ---
    public int getResortCount() {
        return resortCount;
    }

    public void setResortCount(int resortCount) {
        this.resortCount = resortCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<ReferenceProperty> getReferenceProperty() {

        if (referenceProperty != null) {
            referenceProperty.sort(
                    Comparator.comparing(
                            ReferenceProperty::getName,
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        }

        return referenceProperty;
    }

    public void setReferenceProperty(List<ReferenceProperty> referenceProperty) {
        this.referenceProperty = referenceProperty;
        this.resortCount = referenceProperty != null ? referenceProperty.size() : 0;
    }

    public List<String> getStructuredContent() {
        return structuredContent;
    }

    public void setStructuredContent(List<String> structuredContent) {
        this.structuredContent = structuredContent;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCoordinateLatitude() {
        return coordinateLatitude;
    }

    public void setCoordinateLatitude(String coordinateLatitude) {
        this.coordinateLatitude = coordinateLatitude;
    }

    public String getCoordinateLongitude() {
        return coordinateLongitude;
    }

    public void setCoordinateLongitude(String coordinateLongitude) {
        this.coordinateLongitude = coordinateLongitude;
    }

    @Override
    public String toString() {
        return "DestinationLandingDto [name=" + name + ", nodename=" + nodename + ", description=" + description
                + ", images=" + images + ", region=" + region + ", referenceProperty=" + referenceProperty
                + ", structuredContent=" + structuredContent + ", altText=" + altText + ", order=" + order
                + ", destination=" + destination + ", resortCount=" + resortCount + ", coordinateLatitude="
                + coordinateLatitude + ", coordinateLongitude=" + coordinateLongitude + "]";
    }

    

}
