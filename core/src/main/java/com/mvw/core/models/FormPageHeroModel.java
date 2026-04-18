package com.mvw.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FormPageHeroModel {

    @ValueMapValue(name = "bg-image/fileReference")
    private String bgImage;

    @ValueMapValue(name = "mobile-bg-image/fileReference")
    private String mobileBgImage;

    @ValueMapValue(name = "form-position")
    private String formPosition;

    @ValueMapValue(name = "headline-text")
    private String headlineText;

    @ValueMapValue(name = "headline-alignment")
    private String headlineAlignment;

    @ValueMapValue(name = "short-description")
    private String shortDescription;

    @ValueMapValue(name = "short-description-alignment")
    private String shortDescriptionAlignment;

    @ValueMapValue(name = "copy-section-bg-color")
    private String copySectionBgColor;

    @ValueMapValue(name = "bg-color-desktop")
    private String BgColorDesktop;

    @ValueMapValue(name = "copy-section-bg-color-mobile")
    private String copySectionBgColorMobile;

    @ValueMapValue(name = "bg-color-mobile")
    private String BgColorMobile;

    @ValueMapValue(name = "special-offer-points")
    private String specialOfferPoints;

    @ValueMapValue(name = "special-offer-copy")
    private String specialOfferCopy;

    @ValueMapValue(name = "special-offer-alignment")
    private String specialOfferAlignment;

    @ValueMapValue(name = "secondary-copy-text")
    private String secondaryCopyText;

    @ValueMapValue(name = "form-type")
    private String formType;

    @ValueMapValue(name = "copy-block-type")
    private String copyBlockType;

    @ValueMapValue(name = "participation-xf-path")
    private String participationXfPath;

    @ValueMapValue(name = "participation-headline")
    private String participationHeadline;

    @Self
    private SlingHttpServletRequest request;

    public String getBgImage() {
        return bgImage;
    }

    public String getMobileBgImage() {
        return mobileBgImage;
    }

    public String getFormPosition() {
        return formPosition;
    }

    public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineAlignment() {
        return headlineAlignment;
    }

    public String getShortDescription() {
        if (shortDescription != null) {
            return shortDescription.replaceAll("</?p>", "").trim();
        }
        return shortDescription;
    }

    public String getShortDescriptionAlignment() {
        return shortDescriptionAlignment;
    }

    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }

    public String getBgColorDesktop() {
        return BgColorDesktop;
    }

    public String getCopySectionBgColorMobile() {
        return copySectionBgColorMobile;
    }

    public String getBgColorMobile() {
        return BgColorMobile;
    }

    public String getSpecialOfferPoints() {
        return specialOfferPoints;
    }

    public String getSpecialOfferCopy() {
        return specialOfferCopy;
    }

    public String getSpecialOfferAlignment() {
        return specialOfferAlignment;
    }

    public String getSecondaryCopyText() {
        String updatedPhone = (request != null) ? (String) request.getAttribute("phoneNumber") : null;
        if (secondaryCopyText != null && updatedPhone != null && secondaryCopyText.contains("{{phoneNumber}}")) {
            return secondaryCopyText.replace("{{phoneNumber}}", updatedPhone);
        }
        return secondaryCopyText;
    }

    public String getFormType() {
        return formType;
    }

    public String getCopyBlockType() {
        return copyBlockType;
    }

    public String getParticipationXfPath() {
        return participationXfPath;
    }

    public String getParticipationXfRootPath() {
        if (participationXfPath == null || participationXfPath.trim().isEmpty()) {
            return null;
        }

        String xfPath = participationXfPath;

        if (!xfPath.endsWith("/jcr:content/root")) {
            xfPath = xfPath + "/jcr:content/root";
        }

        return xfPath;
    }
    public String getParticipationHeadline() {
        return participationHeadline;
    }

    private String resolveHeadingTag(String type, String defaultTag) {
        // if (type != null && type.matches("heading[1-6]")) {
        //     return type;
        // }
        // return defaultTag;
        if (type == null) {
            return "h2";
        }
 
        switch (type) {
            case "heading1":
                return "h1";
            case "heading2":
                return "h2";
            case "heading3":
                return "h3";
            case "heading4":
                return "h4";
            case "heading5":
                return "h5";
            case "heading6":
                return "h6";
            default:
                return "h2";
        }
    }
}