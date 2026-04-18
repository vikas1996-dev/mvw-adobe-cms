package com.mvw.core.models.dto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResortDto {

    private String name;
    private String marshaCode;
    private String description;
    private String universalPropertyCode;
    private String city;
    private String state;
    private String country;
    private String continent;
    private String region;

    private List<Images> images;
    private String mainPropertyPhotoPath;
    private String mapPhotoPath;

    // INTERNAL ONLY
    private List<Tags> activityTags;
    private List<Tags> vacationTypes;
    private List<Tags> flags;
    private String tripadvisor;
    private String tripadvisorId;
    private String rating;
    private String reviews;
    private String ratingImage;
    private String slug;
    private String webUrl;
    // ---------------- BASIC GETTERS / SETTERS ----------------

    
    public String getName() {
        return name;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getRatingImage() {
        return ratingImage;
    }

    public void setRatingImage(String ratingImage) {
        this.ratingImage = ratingImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarshaCode() {
        return marshaCode;
    }

    public void setMarshaCode(String marshaCode) {
        this.marshaCode = marshaCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniversalPropertyCode() {
        return universalPropertyCode;
    }

    public void setUniversalPropertyCode(String universalPropertyCode) {
        this.universalPropertyCode = universalPropertyCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTripadvisor() {
        return tripadvisor;
    }

    public void setTripadvisor(String tripadvisor) {
        this.tripadvisor = tripadvisor;
    }

    public String getTripadvisorId() {
        return tripadvisorId;
    }

    public void setTripadvisorId(String tripadvisorId) {
        this.tripadvisorId = tripadvisorId;
    }

    public String getWebUrl() {
        return webUrl;
    }
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
    // ---------------- IMAGES ----------------
    @JsonProperty("images")
    public void setImages(List<Images> images) {
        this.images = images;
    }

    @JsonIgnore
    public List<Images> getImages() {
        return images;
    }

   @JsonProperty("image")
public String getMainPropertyPhotoPath() {
    if (images == null || images.isEmpty()) {
        return null;
    }

    return images.stream()
            .filter(Objects::nonNull)
            .filter(img -> img.getReferenceID() != null)
            .filter(img -> "main".equalsIgnoreCase(img.getReferenceID().getName()))
            .filter(img -> img.getPhoto() != null && !img.getPhoto().isEmpty())
            .map(img -> img.getPhoto().get(0).getPath()) 
            .findFirst()
            .orElse(null);
}

@JsonProperty("mapImage")
public String getMapPhotoPath() {
    if (images == null || images.isEmpty()) {
        return null;
    }

    return images.stream()
            .filter(Objects::nonNull)
            .filter(img -> img.getReferenceID() != null)
            .filter(img -> "map".equalsIgnoreCase(img.getReferenceID().getName()))
            .filter(img -> img.getPhoto() != null && !img.getPhoto().isEmpty())
            .map(img -> img.getPhoto().get(0).getPath()) 
            .findFirst()
            .orElse(null);
}
    // ---------------- INTERNAL TAGS (HIDDEN) ----------------

    @JsonIgnore
    public List<Tags> getActivityTags() {
        return activityTags;
    }

    @JsonIgnore
    public List<Tags> getVacationTypes() {
        return vacationTypes;
    }

    // IMPORTANT: setters MUST be annotated so data is populated
    @JsonProperty("activityTags")
    public void setActivityTags(List<Tags> activityTags) {
        this.activityTags = activityTags;
    }

    @JsonProperty("vacationTypes")
    public void setVacationTypes(List<Tags> vacationTypes) {
        this.vacationTypes = vacationTypes;
    }

    // ---------------- EXPOSED JSON OUTPUT ----------------

    @JsonProperty("activities")
public List<String> getActivities() {
    if (activityTags == null || activityTags.isEmpty()) {
        return List.of();   // empty list, never null
    }

    return activityTags.stream()
            .map(Tags::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
}

    @JsonProperty("vacationType")
    public List<String> getVacationType() {
        if (vacationTypes == null)
            return List.of();
        return vacationTypes.stream()
                .map(Tags::getName)
                .collect(Collectors.toList());
    }

    @JsonProperty("image")
    public void setMainPropertyPhotoPath(String mainPropertyPhotoPath) {
        this.mainPropertyPhotoPath = mainPropertyPhotoPath;
    }

    @JsonProperty("mapImage")
    public void setMapPhotoPath(String mapPhotoPath) {
        this.mapPhotoPath = mapPhotoPath;
    }

    @JsonProperty("imageAlt")
    public String getImageAlt() {
        if (images == null || images.isEmpty())
            return null;

        for (Images img : images) {
            if (img != null
                    && img.getName() != null
                    && img.getName().contains("Property, Main")) {

                // 1️⃣ Prefer altText from image
                if (img.getAltText() != null && !img.getAltText().toString().isBlank()) {
                    return img.getAltText().toString();
                }

                // 2️⃣ Fallback to resort name
                return name;
            }
        }
        return name; // final fallback
    }

    @JsonIgnore
    public List<Tags> getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(List<Tags> flags) {
        this.flags = flags;
    }

    @JsonProperty("collection")
    public String getCollection() {
        if (flags != null && !flags.isEmpty()) {
            return flags.stream()
                    .filter(flag -> "city-collection".equals(flag.getNodename()))
                    .map(Tags::getName)
                    .findFirst() // take the first match
                    .orElse(null); // fallback to resort name
        }
        return name; // fallback if flags is null or empty
    }// final fallback

    @JsonProperty("featured")
    public String getFeatures() {
        if (flags != null && !flags.isEmpty()) {
            return flags.stream()
                    .filter(flag -> "featured".equals(flag.getNodename()))
                    .map(Tags::getName)
                    .findFirst() // take the first match
                    .orElse(null); // fallback to resort name
        }
        return name; // fallback if flags is null or empty
    }

    private Tags dcmBrand;

    public Tags getDcmBrand() {
        return dcmBrand;
    }

    @JsonProperty("dcmBrand")
    public void setDcmBrand(Tags dcmBrand) {
        this.dcmBrand = dcmBrand;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    private LocaleInfo locale;
    private LocaleInfo subLocale;

    @JsonIgnore
    public LocaleInfo getLocale() {
        return locale;
    }
@JsonProperty("locale")
    public void setLocale(LocaleInfo locale) {
        this.locale = locale;
    }

    @JsonIgnore
    public LocaleInfo getSubLocale() {
        return subLocale;
    }
@JsonProperty("subLocale")
    public void setSubLocale(LocaleInfo subLocale) {
        this.subLocale = subLocale;
    }

    // ---------------- INNER CLASSES ----------------

    public static class LocaleInfo {
        private String name;
        private String priority;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "ResortDto [universalPropertyCode=" + universalPropertyCode + ", tripadvisorId=" + tripadvisorId
                + ", rating=" + rating + ", ratingImage=" + ratingImage + ", webUrl=" + webUrl + ", getRatingImage()="
                + getRatingImage() + ", getRating()=" + getRating() + "]";
    }

}
