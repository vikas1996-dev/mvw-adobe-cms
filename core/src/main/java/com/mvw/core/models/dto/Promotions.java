package com.mvw.core.models.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvw.core.constants.AppConstants;

public class Promotions {
    private String nodename;
    private String name;
    private String eyeBrow;
    private String priority;
    private String path;
    private List<String> persona;
    private List<Tags> placementId;
    private String shortDescriptionAd;
    private String buttonOfferTextAd;
    private String buttonOfferUrlAd;
    private String descriptionAd;
    private String buttonOfferTextAd1;
    private String buttonOfferUrlAd1;
    private List<Tags> destinations;
    private List<String> structuredContent;
    private String badge;

    public String getBadge() {
        return badge;
    }

    public List<Tags> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Tags> destinations) {
        this.destinations = destinations;
    }

    @JsonProperty("imagesAd")
    private List<ImageAd> imagesAd;

    public String getNodename() {
        return nodename;
    }

    public String getName() {
        return name;
    }

    public String getPriority() {
        return priority;
    }

    public String getPath() {
        return path;
    }

    public List<String> getPersona() {
        return persona;
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

    public List<ImageAd> getImagesAd() {
        return imagesAd;
    }

    public String getButtonOfferTextAd1() {
        return buttonOfferTextAd1;
    }

    public String getButtonOfferUrlAd1() {
        return buttonOfferUrlAd1;
    }

    public List<Tags> getPlacementId() {
        return placementId;
    }

    @Override
    public String toString() {
        return "Promotions [nodename=" + nodename + ", name=" + name + ", priority=" + priority + ", path=" + path
                + ", persona=" + persona + ", placementId=" + placementId + ", shortDescriptionAd=" + shortDescriptionAd
                + ", buttonOfferTextAd=" + buttonOfferTextAd + ", buttonOfferUrlAd=" + buttonOfferUrlAd
                + ", descriptionAd=" + descriptionAd + ", buttonOfferTextAd1=" + buttonOfferTextAd1
                + ", buttonOfferUrlAd1=" + buttonOfferUrlAd1 + ", imagesAd=" + imagesAd + "]";
    }

    public String getEyeBrow() {
        return eyeBrow;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEyeBrow(String eyeBrow) {
        this.eyeBrow = eyeBrow;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPersona(List<String> persona) {
        this.persona = persona;
    }

    public void setPlacementId(List<Tags> placementId) {
        this.placementId = placementId;
        updateButtonUrlIfRequired();
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

    public void setButtonOfferTextAd1(String buttonOfferTextAd1) {
        this.buttonOfferTextAd1 = buttonOfferTextAd1;
        updateButtonUrlIfRequired();
    }

    public void setButtonOfferUrlAd1(String buttonOfferUrlAd1) {
        this.buttonOfferUrlAd1 = buttonOfferUrlAd1;
    }

    public void setImagesAd(List<ImageAd> imagesAd) {
        this.imagesAd = imagesAd;
    }

    private void updateButtonUrlIfRequired() {

        if (placementId == null || placementId.isEmpty()) {
            return;
        }

        boolean containsSfPromo = placementId.stream()
                .anyMatch(tag -> tag != null
                        && tag.getNodename() != null
                        && "sf-promo-1".equalsIgnoreCase(tag.getNodename()));

        if (containsSfPromo
                && buttonOfferUrlAd != null
                && !buttonOfferUrlAd.isEmpty()
                && !buttonOfferUrlAd.startsWith(AppConstants.TMVC_BASE_CONTENT_PATH)) {

            this.buttonOfferUrlAd = AppConstants.TMVC_BASE_CONTENT_PATH + buttonOfferUrlAd;
        }
    }

    public List<String> getStructuredContent() {
        return structuredContent;
    }

    public void setStructuredContent(List<String> structuredContent) {
        this.structuredContent = structuredContent;
        extractBadge();
    }

    private void extractBadge() {

        if (structuredContent == null || structuredContent.isEmpty()) {
            return; // leave badge as null
        }

        for (String item : structuredContent) {

            if (item != null && item.startsWith("badge=")) {
                String[] parts = item.split("=", 2);

                if (parts.length == 2 && parts[1] != null && !parts[1].isEmpty()) {
                    this.badge = parts[1].trim();
                    return; // stop after first match
                }
            }
        }
    }
}
