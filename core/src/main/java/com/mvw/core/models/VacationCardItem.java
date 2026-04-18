package com.mvw.core.models;

import com.mvw.core.utils.UrlUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class VacationCardItem {

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String cardBgColor;

    @ValueMapValue
    private String cardHeadline;

    @ValueMapValue
    private String headlineType;

    @ValueMapValue
    private String cardDescription;

    @ValueMapValue
    private String cardImage;

    @ValueMapValue
    private String mobileImage;

    @ValueMapValue
    private String cardImageAlt;

    @ValueMapValue
    private String badge;

    @ValueMapValue
    private String badgeColor;

    @ValueMapValue
    private String badgeFontAwesomeIcon;

    @ValueMapValue
    private String ctaText;

    @ValueMapValue
    private String ctaDestination;

    @ValueMapValue
    private String ctaTab;

    @ValueMapValue
    private String ctaStyle;

    @ValueMapValue
    private String ctaPlacement;

    @ValueMapValue
    private String fontAwesomeIcon;

    // --- Getters ---

    public String getCardBgColor() {
        return cardBgColor;
    }

    public String getCardHeadline() {
        return cardHeadline;
    }

    public String getHeadlineType() {
        return headlineType;
    }

    public String getCardDescription() {
        return cardDescription;
    }

    public String getCardImage() {
        return cardImage;
    }

    public String getMobileImage() {
        return mobileImage;
    }

    public String getCardImageAlt() {
        return cardImageAlt;
    }

    public String getBadge() {
        return badge;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public String getBadgeFontAwesomeIcon() {
        return badgeFontAwesomeIcon;
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

    public String getFontAwesomeIcon() {
        return fontAwesomeIcon;
    }

    @ValueMapValue
    private String headlineTag;

    public String getHeadlineTag() {
        if (headlineType != null && headlineType.matches("heading[1-6]")) {
            headlineTag = "h" + headlineType.substring(7);
            return headlineTag;
        }
        return "h4";
    }
}
