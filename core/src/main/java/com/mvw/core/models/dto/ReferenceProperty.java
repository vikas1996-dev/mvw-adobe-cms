package com.mvw.core.models.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReferenceProperty {
    private String name;
    private String nodename;
    private String featured;
    private String marshaCode;
    private String universalPropertyCode;
    private String city;
    private String state;
    private String country;
    private String continent;
    private String region;
    private String coordinateLatitude;
    private String coordinateLongitude;
    private List<Images> images;
    private String mainPropertyPhotoPath;
    private List<Tags> flags;
    private DcmBrand dcmBrand;
    private String slug;

    private String logo;

     private String rating;
    private String reviews;
    private String ratingImage;
    private String webUrl;
 private String tripadvisorId;
    

   public String getTripadvisorId() {
    return tripadvisorId;
}
 public void setTripadvisorId(String tripadvisorId) {
    this.tripadvisorId = tripadvisorId;
 }
   public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
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
    public String getWebUrl() {
        return webUrl;
    }
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
   @JsonProperty("logo")
public String getLogo() {

    if (logo != null && !logo.isEmpty()) {
        return logo;
    }

    if (dcmBrand == null || dcmBrand.getImages() == null) {
        return null;
    }

    for (Images img : dcmBrand.getImages()) {

        if (img == null || img.getNodename() == null) {
            continue;
        }

        String nodename = img.getNodename().toLowerCase();

       boolean isValidLogo =
                nodename.equals("marriott-vacation-club-logo") ||
                nodename.equals("sheraton-logo") ||
                nodename.equals("grc-logo")||
              nodename.equals("dwk-westin-vacation-club-logo") ||
              nodename.equals("the-ritz-carlton-club-logo") ;

        if (isValidLogo
                && img.getPhoto() != null
                && !img.getPhoto().isEmpty()
                && img.getPhoto().get(0) != null
                && img.getPhoto().get(0).getPath() != null) {

            return img.getPhoto().get(0).getPath();
        }
    }

    return null;
}
    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getMarshaCode() {
        return marshaCode;
    }

    public void setMarshaCode(String marshaCode) {
        this.marshaCode = marshaCode;
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
        if (images == null || images.isEmpty())
            return null;

        for (Images img : images) {
            if (img != null
                    && img.getName() != null
                    && img.getName().contains("Property, Main")
                    && img.getPhoto() != null
                    && !img.getPhoto().isEmpty()
                    && img.getPhoto().get(0).getPath() != null) {

                mainPropertyPhotoPath = img.getPhoto().get(0).getPath();
                return mainPropertyPhotoPath;
            }
        }
        return null;
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public String toString() {
        return "ReferenceProperty [name=" + name + ", nodename=" + nodename + ", featured=" + featured + ", marshaCode="
                + marshaCode + ", universalPropertyCode=" + universalPropertyCode + ", city=" + city + ", state="
                + state + ", country=" + country + ", continent=" + continent + ", region=" + region
                + ", coordinateLatitude=" + coordinateLatitude + ", coordinateLongitude=" + coordinateLongitude
                + ", images=" + images + ", mainPropertyPhotoPath=" + mainPropertyPhotoPath + ", flags=" + flags
                + ", slug=" + slug + ", logo=" + logo + "]";
    }


    @JsonProperty("dcmBrand")
public void setDcmBrand(DcmBrand dcmBrand) {
    this.dcmBrand = dcmBrand;
}

@JsonIgnore
public DcmBrand getDcmBrand() {
    return dcmBrand;
}

}
