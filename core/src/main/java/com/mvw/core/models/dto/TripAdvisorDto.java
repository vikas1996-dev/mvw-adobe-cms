package com.mvw.core.models.dto;

public class TripAdvisorDto {

     private String tripadvisorId;
     private String universalPropertyCode;
     private String rating;
    private String reviews;
    private String ratingImage;
    private String webUrl;

    
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
    public String getUniversalPropertyCode() {
        return universalPropertyCode;
    }
    public void setUniversalPropertyCode(String universalPropertyCode) {
        this.universalPropertyCode = universalPropertyCode;
    }
    public String getWebUrl() {
        return webUrl;
    }
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }


    
}
