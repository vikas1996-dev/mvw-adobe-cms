package com.mvw.core.models.dto;

import java.util.List;
import java.util.Objects;

public class Properties {
    private String coordinateLatitude;
    private String coordinateLongitude;
    private String checkIn;
    private String checkOut;
    private List<Club> club;
    private String name;
    private String nodename;
    private String description;
    private Object longDescription;
    private String slug;
    private String universalPropertyCode;
    private List<Flags> flags;
    private DcmBrand dcmBrand;
    private String path;
    private String address1;
    private String address2;
    private String city;
    private Object state;
    private String country;
    private String continent;
    private String region;
    private String phoneMain;
    private Object accessibility;
    private Object accessibilityOverride;
    private Object announcements;
    private Object policies;
    private String twitter;
    private String facebook;
    private String instagram;
    private String pinterest;
    private String youtube;
    private String tripadvisor;
    private String tripAdvisorId;
    private List<Video> videos;
    private List<Images> images;
    private List<Documents> documents;
    private List<Gallery> gallery;
    private String galleryCtaImage;
    private String cityCOllection;
    private String hraCheck;
    private String noRental;

    private TripAdvisorDto tripAdvisorDto;
    private String mapPhotoPath;
    private String zip;
    private List<String> structuredContent;

    private boolean isVideo;

    private String rating;
    private String reviews;
    private String ratingImage;
    private String webUrl;

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

    public String getZip() {
        return zip;
    }

    public List<String> getStructuredContent() {
        return structuredContent;
    }

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

    public void setMapPhotoPath(String mapPhotoPath) {
        this.mapPhotoPath = mapPhotoPath;
    }

    public TripAdvisorDto getTripAdvisorDto() {
        return tripAdvisorDto;
    }

    public void setTripAdvisorDto(TripAdvisorDto tripAdvisorDto) {
        this.tripAdvisorDto = tripAdvisorDto;
    }

    public String getCityCOllection() {
        if (flags == null || flags.isEmpty()) {
            return null;
        }

        return flags.stream()
                .filter(f -> f.getNodename() != null &&
                        f.getNodename().toLowerCase().replace("_", "-").contains("city"))
                .map(Flags::getName)
                .filter(name -> name != null && !name.isEmpty())
                .findFirst()
                .orElse(null);
    }

    public boolean isHraCheck() {
        if (flags == null || flags.isEmpty()) {
            return false;
        }

        return flags.stream()
                .anyMatch(f -> f.getNodename() != null &&
                        f.getNodename().equalsIgnoreCase("hra-check-avail-modal"));
    }

    public boolean isNoRental() {
        if (flags == null || flags.isEmpty()) {
            return false;
        }

        return flags.stream()
                .anyMatch(f -> f.getNodename() != null &&
                        f.getNodename().equalsIgnoreCase("no-rental-modal"));
    }

    public String getCoordinateLatitude() {
        return coordinateLatitude;
    }

    public String getCoordinateLongitude() {
        return coordinateLongitude;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public List<Club> getClub() {
        return club;
    }

    public String getName() {
        return name;
    }

    public String getNodename() {
        return nodename;
    }

    public String getGalleryCtaImage() {
        return galleryCtaImage;
    }

    public void setGalleryCtaImage(String galleryCtaImage) {
        this.galleryCtaImage = galleryCtaImage;
    }

    public String getDescription() {
        return description;
    }

    public Object getLongDescription() {
        return longDescription;
    }

    public String getSlug() {
        return slug;
    }

    public String getUniversalPropertyCode() {
        return universalPropertyCode;
    }

    public List<Flags> getFlags() {
        return flags;
    }

    public DcmBrand getDcmBrand() {
        return dcmBrand;
    }

    public String getPath() {
        return path;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public Object getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getContinent() {
        return continent;
    }

    public String getRegion() {
        return region;
    }

    public String getPhoneMain() {
        return phoneMain;
    }

    public Object getAccessibility() {
        return accessibility;
    }

    public Object getAnnouncements() {
        return announcements;
    }

    public Object getPolicies() {
        return policies;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getPinterest() {
        return pinterest;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getTripadvisor() {
        return tripadvisor;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public boolean isVideo() {
        return videos != null && !videos.isEmpty();
    }

    public List<Images> getImages() {
        return images;
    }

    public List<Documents> getDocuments() {
        return documents;
    }

    public List<Gallery> getGallery() {
        return gallery;
    }

    public String getTripAdvisorId() {
        return tripAdvisorId;
    }

    public Object getAccessibilityOverride() {
        return accessibilityOverride;
    }

    public void setGallery(List<Gallery> gallery) {
        this.gallery = gallery;
    }

    @Override
    public String toString() {
        return "Properties [gallery=" + gallery +  "]";
    }

}
