package com.mvw.core.models.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultModel {
    private String name;
    private String nodename;
    private String path;
    private String description;
    private String properties;
    private List<Images> images;

    // optional logos
    private String mvcLogo;
    private String svcLogo;
    private String wvcLogo;

    // --- getters/setters ---
    public void setName(String name) { this.name = name; }
    public void setNodename(String nodename) { this.nodename = nodename; }
    public void setPath(String path) { this.path = path; }
    public void setDescription(String description) { this.description = description; }
    public String getProperties() { return properties; }
    public void setProperties(String properties) { this.properties = properties; }

    public String getName() { return name; }
    public String getNodename() { return nodename; }
    public String getPath() { return path; }
    public String getDescription() { return description; }

    @JsonProperty("images")
    public void setImages(List<Images> images) { this.images = images; }

    @JsonIgnore
    public List<Images> getImages() { return images; }

    /**
     * Returns the first image path with base URL already prepended.
     */
    @JsonProperty("image")
    public String getImagePath() {
        if (images == null || images.isEmpty()) {
            return null;
        }

        return images.stream()
                .filter(Objects::nonNull)
                .filter(img -> img.getPhoto() != null && !img.getPhoto().isEmpty())
                .map(img -> img.getPhoto().get(0).getPath())  // already has base path applied
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
    @Override
    public String toString() {
        return "DefaultModel [name=" + name + ", nodename=" + nodename + ", path=" + path + ", description="
                + description + ", properties=" + properties + "]";
    }

    
}