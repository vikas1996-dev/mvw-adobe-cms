package com.mvw.core.models;

import com.mvw.core.utils.UrlUtils;
import org.apache.sling.api.resource.Resource;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class CarouselSlidesPojo {
private static final Logger logger = LoggerFactory.getLogger(CarouselSlidesPojo.class);

    @SlingObject
    ResourceResolver resolver;

    @ValueMapValue
    private String carouselHeadline;

    @ValueMapValue
    private String carouselHeadlineType;

    @ValueMapValue
    private String resortName;

    @ValueMapValue
    private String feature1FontAwesomeIcon;

    @ValueMapValue
    private String feature1Label;

    @ValueMapValue
    private String feature2FontAwesomeIcon;

    @ValueMapValue
    private String feature2Label;

    @ValueMapValue
    private String carouselImage;

    @ValueMapValue
    private String mobileImage;

    @ValueMapValue
    private String carouselImageAlt;

    @ValueMapValue
    private String badge;
    @ValueMapValue
    private String badgeColor;

    @ValueMapValue
    private String badgeFontAwesomeIcon;

    @ValueMapValue
    private String brandIconImage;

    @ValueMapValue

    private String brandIconImageAlt;

    @ValueMapValue

    private String ctaText;

    @ValueMapValue
    private String ctaDestination;

    @Default(values = "sameTab")

    @ValueMapValue

    private String ctaTab;

    @ValueMapValue

    @Default(values = "Tertiary Label")

    private String ctaStyle;

    @ValueMapValue

    private String ctaPlacement;

    @ValueMapValue
    private String fontAwesomeIcon;

    public String getCarouselHeadline() {
        return carouselHeadline;
    }

    public String getCarouselHeadlineType() {
        return carouselHeadlineType;
    }

    public String getResortName() {
    return resortName;
    }

    public String getFeature1FontAwesomeIcon() {
        return feature1FontAwesomeIcon;
    }

    public String getFeature1Label() {
        return feature1Label;
    }

    public String getFeature2FontAwesomeIcon() {
        return feature2FontAwesomeIcon;
    }

    public String getFeature2Label() {
        return feature2Label;
    }

    public String getCarouselImage() {
        return carouselImage;
    }

    public String getMobileImage() {
        return mobileImage;
    }

    public String getCarouselImageAlt() {
        return carouselImageAlt;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public String getBadgeFontAwesomeIcon() {
        return badgeFontAwesomeIcon;
    }

    public String getBrandIconImage() {
        return brandIconImage;
    }

    public String getBrandIconImageAlt() {
        return brandIconImageAlt;
    }

    public String getCtaText() {
        return ctaText;
    }

    public String getCtaDestination() {
        return UrlUtils.getNormalizedUrl(ctaDestination, resolver);
    }

    public String getCtaTab() {
        return ctaTab;
    }

    public String getCtaStyle() {
        return ctaStyle;
    }

    public String getCtaPlacement() {
        return ctaPlacement;
    }

    public String getBadge() {
        return badge;
    }

    public String getFontAwesomeIcon() {
        return fontAwesomeIcon;
    }

     @ValueMapValue
    private String carouselHeadlineTag;

    public String getHeadlineTag() {

    if (carouselHeadlineType != null && carouselHeadlineType.matches("heading[1-6]")) {
        // convert heading1 → h1, heading2 → h2, etc.
        carouselHeadlineTag = "h" + carouselHeadlineType.substring(7); // get the number
        return carouselHeadlineTag;
    }

   return "h3";
}
}
