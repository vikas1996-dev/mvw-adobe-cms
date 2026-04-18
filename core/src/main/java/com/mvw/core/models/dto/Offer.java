package com.mvw.core.models.dto;

import java.util.List;

public class Offer {

    private String title;
    private String eyebrow;
    private String shortDescriptionAd;
    private String buttonOfferTextAd;
    private String buttonOfferUrlAd;
    private String buttonOfferTextAd1;
    private String buttonOfferUrlAd1;
    private String descriptionAd;
    private List<String> structuredContent;

    // Final image list exposed by Offer
    private List<Image> images;
    private String badge;

    // Getters
    public String getTitle() {
        return title;
    }

    public String getEyebrow() {
        return eyebrow;
    }

    public String getShortDescriptionAd() {
        return shortDescriptionAd;
    }

    public String getButtonOfferTextAd() {
        return buttonOfferTextAd;
    }

    public String getButtonOfferUrlAd() {
        return buttonOfferUrlAd;
    }

    public String getDescriptionAd() {
        return descriptionAd;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEyebrow(String eyebrow) {
        this.eyebrow = eyebrow;
    }

    public void setShortDescriptionAd(String shortDescriptionAd) {
        this.shortDescriptionAd = shortDescriptionAd;
    }

    public void setButtonOfferTextAd(String buttonOfferTextAd) {
        this.buttonOfferTextAd = buttonOfferTextAd;
    }

    public void setButtonOfferUrlAd(String buttonOfferUrlAd) {
        this.buttonOfferUrlAd = buttonOfferUrlAd;
    }

    public void setDescriptionAd(String descriptionAd) {
        this.descriptionAd = descriptionAd;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getButtonOfferTextAd1() {
        return buttonOfferTextAd1;
    }

    public void setButtonOfferTextAd1(String buttonOfferTextAd1) {
        this.buttonOfferTextAd1 = buttonOfferTextAd1;
    }

    public String getButtonOfferUrlAd1() {
        return buttonOfferUrlAd1;
    }

    public void setButtonOfferUrlAd1(String buttonOfferUrlAd1) {
        this.buttonOfferUrlAd1 = buttonOfferUrlAd1;
    }

    // 🔹 Inner Image class
    public static class Image {

        private String name; // from Photo
        private String altText; // from ImageAd
        private String path;
        private String ratio;
        // from Photo

        public Image(String name, String altText, String path, String ratio) {
            this.name = name;
            this.altText = altText;
            this.path = path;
            this.ratio = ratio;
        }

        public String getName() {
            return name;
        }

        public String getAltText() {
            return altText;
        }

        public String getPath() {
            return path;
        }

        public String getRatio() {
            return ratio;
        }

    }

    @Override
    public String toString() {
        return "Offer [title=" + title + ", eyebrow=" + eyebrow + ", shortDescriptionAd=" + shortDescriptionAd
                + ", buttonOfferTextAd=" + buttonOfferTextAd + ", buttonOfferUrlAd=" + buttonOfferUrlAd
                + ", descriptionAd=" + descriptionAd + ", images=" + images + ", getTitle()=" + getTitle()
                + ", getEyebrow()=" + getEyebrow() + ", getShortDescriptionAd()=" + getShortDescriptionAd()
                + ", getButtonOfferTextAd()=" + getButtonOfferTextAd() + ", getButtonOfferUrlAd()="
                + getButtonOfferUrlAd() + ", getDescriptionAd()=" + getDescriptionAd() + ", getImages()=" + getImages()
                + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
                + "]";
    }

    public List<String> getStructuredContent() {
        return structuredContent;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

}
