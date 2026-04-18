package com.mvw.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;

public class StoryBlockModel extends WCMUsePojo {

    private String viewport;
    private String copySectionBgColor;
    private String copySectionBgBorderControl;
    private String selectOverlay;
    // private String overlayStyle;
    private String overlayColor;
    private String logoAltText;
    private String copySectionAlignment;
    private String mobileCopyAlignment;
    private String subHeadlineText;
    private String headlineText;
    private String headlineSize;
    private String headlineStyledBorder;
    private String subTitle1;
    private String shortDescription1;
    private String subTitle2;
    private String shortDescription2;
    private Integer copySectionBgColorTransparency;
    private String imageAlignment;
    private String headlineAlignment;
    private String mobileHeadlineAlignment;
    private String numberOfImages;
    private String bigImage;
    private String bigImageAlt;
    private String bigImageStyle;
    private String smallImage;
    private String smallImageAlt;
    private String smallImageStyle;
    private String smallImagePositioning;
    private String mediaType;
    private String bgVideo;
    private String bgVideoID;
    private String extractedVideoID;
    private String videoPoster;
    private String caption;
    private String overlayOpacity;
    private String mobileBigImageAltText;
    private String mobileSmallImageAltText;
    private String timerCheckbox;
    private String timerText;
    private List<Section> sections;
    private String smallImgPosClassDesktop = "";
    private String smallImgPosClassMobile = "";
    private String mobileBgColorBorderControl;
    private String mobileBgColorAlignment;
    private String stackingOrder;

    @Override
    public void activate() {
        Resource resource = getResource();
        ValueMap valueMap = resource.getValueMap();
        viewport = valueMap.get("viewport", String.class);
        copySectionBgColor = valueMap.get("backgroundColor", String.class);

        copySectionBgBorderControl = valueMap.get("copySectionBgBorderControl", String.class);
        if (copySectionBgBorderControl == null) {
            copySectionBgBorderControl = valueMap.get("copySectionBorderControl", String.class);
        }

        selectOverlay = valueMap.get("selectOverlay", String.class);
        // overlayStyle = valueMap.get("overlayStyle", String.class);
        overlayColor = valueMap.get("overlayColor", String.class);

        logoAltText = valueMap.get("logoAltText", String.class);

        copySectionAlignment = valueMap.get("copySectionAlignment", String.class);
        mobileCopyAlignment = valueMap.get("mobileCopyAlignment", String.class);
        subHeadlineText = valueMap.get("subHeadlineText", String.class);
        headlineText = valueMap.get("headlineText", String.class);
        headlineSize = valueMap.get("headlineSize", String.class);

        headlineStyledBorder = valueMap.get("headlineStyledBorder", String.class);
        if (headlineStyledBorder == null) {
            headlineStyledBorder = valueMap.get("headlineWithStyledBorder", String.class);
        }

        subTitle1 = valueMap.get("subTitle1", String.class);
        shortDescription1 = valueMap.get("shortDescription1", String.class);
        subTitle2 = valueMap.get("subTitle2", String.class);
        shortDescription2 = valueMap.get("shortDescription2", String.class);

        numberOfImages = valueMap.get("numberOfImages", String.class);
        if (numberOfImages == null || numberOfImages.isEmpty()) {
            numberOfImages = "one";
        }

        Integer transparency = valueMap.get("copySectionBgColorTransparency", Integer.class);
        if (transparency == null) {
            try {
                transparency = Integer.valueOf(valueMap.get("copySectionBgColorTransparency", "0"));
            } catch(Exception e) {
                transparency = 0;
            }
        }
        copySectionBgColorTransparency = transparency;
        overlayOpacity = String.format(Locale.US, "%.3f", transparency / 100.0);

        mediaType = valueMap.get("mediaType", String.class);
        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = "image";
        }

        bgVideo = resolveImagePath("bgVideo");
        videoPoster = resolveImagePath("videoPoster");

        bgVideoID = valueMap.get("bgVideoID", String.class);
        if (bgVideo != null && !bgVideo.isEmpty()) {
            extractedVideoID = extractVideoIDFromPath(bgVideo);

            if (bgVideoID == null || bgVideoID.isEmpty()) {
                bgVideoID = extractedVideoID;
            }
        }

        bigImage = valueMap.get("bigImage", String.class);
        bigImageAlt = valueMap.get("bigImageAlt", String.class);
        
        // CHANGE 2 & 3: Set bigImageStyle based on copySectionBgBorderControl
        // If container-v2 is selected, bigImageStyle is always "squared"
        // If container-v3 is selected, bigImageStyle is always "rounded"
        if ("container-v2".equals(copySectionBgBorderControl)) {
            bigImageStyle = "squared";
        } else if ("container-v3".equals(copySectionBgBorderControl)) {
            bigImageStyle = "rounded";
        } else {
            bigImageStyle = valueMap.get("bigImageStyle", String.class);
        }

        smallImage = valueMap.get("smallImage", String.class);
        smallImageAlt = valueMap.get("smallImageAlt", String.class);
        
        // CHANGE 1: smallImageStyle is always "rounded" regardless of user input
        smallImageStyle = "rounded";
        
        smallImagePositioning = valueMap.get("smallImagePositioning", String.class);

        if ("one".equals(numberOfImages)) {
            smallImagePositioning = "";
        }

        mobileBigImageAltText = valueMap.get("mobileBigImageAltText", String.class);
        if (mobileBigImageAltText == null) {
            mobileBigImageAltText = valueMap.get("mobileBigBgImageAlt", String.class);
        }
        mobileSmallImageAltText = valueMap.get("mobileSmallImageAltText", String.class);
        if (mobileSmallImageAltText == null) {
            mobileSmallImageAltText = valueMap.get("mobileSmallBgImageAlt", String.class);
        }

        imageAlignment = valueMap.get("imageAlignment", String.class);
        headlineAlignment = valueMap.get("headlineAlignment", String.class);
        mobileHeadlineAlignment = valueMap.get("mobileHeadlineAlignment", String.class);
        caption = valueMap.get("caption", String.class);

        timerCheckbox = valueMap.get("timerCheckbox", String.class);
        timerText = valueMap.get("timerText", String.class);

        mobileBgColorBorderControl = valueMap.get("mobileBgColorBorderControl", String.class);
        mobileBgColorAlignment = valueMap.get("mobileBgColorAlignment", String.class);

        stackingOrder = valueMap.get("stackingOrder", String.class);

        String mobileSmallPosRaw = valueMap.get("mobileSmallImagePositioning", String.class);
        if (mobileSmallPosRaw == null) {
            mobileSmallPosRaw = valueMap.get("mobileSmallImagePosition", String.class);
        }

        if ("one".equals(numberOfImages)) {
            mobileSmallPosRaw = "";
        }

        smallImgPosClassDesktop = normalizeSmallPosDesktop(smallImagePositioning);
        smallImgPosClassMobile = normalizeSmallPosMobile(mobileSmallPosRaw);

        Resource sectionsNode = resource.getChild("sections");
        if (sectionsNode != null) {
            sections = new ArrayList<>();
            for (Resource item : sectionsNode.getChildren()) {
                ValueMap itemValueMap = item.getValueMap();
                sections.add(new Section(
                        itemValueMap.get("subheading", String.class),
                        itemValueMap.get("description", String.class)));
            }
        } else {
            sections = Collections.emptyList();
        }
    }

    private String extractVideoIDFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String filename = path.substring(path.lastIndexOf('/') + 1);

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            filename = filename.substring(0, dotIndex);
        }

        return filename;
    }

    private String resolveImagePath(String propertyName) {
        ValueMap valueMap = getResource().getValueMap();

        String direct = valueMap.get(propertyName, String.class);
        if (direct != null && !direct.isEmpty()) return direct;

        String[] suffixes = {
                "FileReference",
                "Reference",
                "fileReference"
        };
        for (String suffix : suffixes) {
            String value = valueMap.get(propertyName + suffix, String.class);
            if (value != null && !value.isEmpty()) return value;
        }

        Resource child = getResource().getChild(propertyName);
        String deep = findFileReferenceDeep(child);
        if (deep != null) return deep;

        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            String key = entry.getKey();
            if (key != null && (key.equalsIgnoreCase(propertyName + "reference") ||
                    key.equalsIgnoreCase(propertyName + "filereference"))) {
                Object value = valueMap.get(key);
                if (value != null && !value.toString().isEmpty()) {
                    return value.toString();
                }
            }
        }
        return null;
    }

    private String findFileReferenceDeep(Resource node) {
        if (node == null) return null;

        ValueMap valueMap = node.getValueMap();
        String fileReference = valueMap.get("fileReference", String.class);
        if (fileReference != null && !fileReference.isEmpty()) return fileReference;

        Resource jcr = node.getChild("jcr:content");
        if (jcr != null) {
            ValueMap jcrValueMap = jcr.getValueMap();
            fileReference = jcrValueMap.get("fileReference", String.class);
            if (fileReference != null && !fileReference.isEmpty()) return fileReference;
        }

        for (Resource child : node.getChildren()) {
            if ("jcr:content".equals(child.getName())) continue;
            fileReference = findFileReferenceDeep(child);
            if (fileReference != null) return fileReference;
        }
        return null;
    }

    private String mapOrRaw(String path) {
        if (path == null || path.isEmpty()) return null;
        try {
            return getRequest().getResourceResolver().map(path);
        } catch(Exception e) {
            return path;
        }
    }

    public String getLogoUrl() {
        String path = resolveImagePath("logoAboveHeadline");
        if (path == null) path = resolveImagePath("logoAboveHeadlines");
        return mapOrRaw(path);
    }

    public String getBigImageUrl() {
        String path = resolveImagePath("bigImage");
        return mapOrRaw(path);
    }

    public String getMobileBigImageUrl() {
        String path = resolveImagePath("mobileBigBgImage");
        if (path == null) path = resolveImagePath("mobileBigBgImageFileReference");
        return mapOrRaw(path);
    }

    public String getSmallImageUrl() {
        String path = resolveImagePath("smallImage");
        if (path == null) path = resolveImagePath("smallImageReference");
        return mapOrRaw(path);
    }

    public String getMobileSmallImageUrl() {
        String path = resolveImagePath("mobileSmallBgImage");
        if (path == null) path = resolveImagePath("mobileSmallBgImageFileReference");
        return mapOrRaw(path);
    }

    public boolean hasLogo() {
        return getLogoUrl() != null;
    }

    public boolean hasBigImage() {
        return getBigImageUrl() != null;
    }

    public boolean hasSmallImage() {
        return getSmallImageUrl() != null;
    }

    public boolean hasMobileBigImage() {
        return getMobileBigImageUrl() != null;
    }

    public boolean hasMobileSmallImage() {
        return getMobileSmallImageUrl() != null;
    }

    public String getBgThemeClass() {
        StringBuilder classes = new StringBuilder();
        
        if (copySectionBgColor != null && !"none".equals(copySectionBgColor)) {
            classes.append("bg-theme-").append(copySectionBgColor);
        }
        
        if (mobileBgColorAlignment != null && !"none".equals(mobileBgColorAlignment)) {
            if (classes.length() > 0) {
                classes.append(" ");
            }
            classes.append("m-bg-theme-").append(mobileBgColorAlignment);
        }
        
        return classes.toString();
    }

    public String getWrapperClass() {
        if ("container-v3".equals(copySectionBgBorderControl)) {
            return "";
        }
        return getBgThemeClass();
    }

    public String getContainerClass() {
        StringBuilder classes = new StringBuilder();

        if ("container-v2".equals(copySectionBgBorderControl)) {
            classes.append("container-v2");
        } else if ("container-v3".equals(copySectionBgBorderControl)) {
            classes.append("container-v3");

            String bgClass = getBgThemeClass();
            if (bgClass != null && !bgClass.isEmpty()) {
                classes.append(" ").append(bgClass);
            }
        }

        return classes.toString();
    }

    public boolean hasOverlay() {
        return "container-v3".equals(copySectionBgBorderControl);
    }

    // public String getOverlayClass() {
    //     if (hasOverlay() && "concave".equals(overlayStyle)) {
    //         return "concave";
    //     }
    //     return "";
    // }

    public String getMobileOverlayClass() {
        if (mobileBgColorBorderControl == null || "none".equals(mobileBgColorBorderControl)) {
            return "";
        }
        if ("concave Top".equals(mobileBgColorBorderControl) || "concave top".equalsIgnoreCase(mobileBgColorBorderControl)) {
            return "m-concave";
        }
        if ("convex top".equalsIgnoreCase(mobileBgColorBorderControl)) {
            return "m-convex";
        }
        return "";
    }

    public String getStackingOrderClass() {
        if ("true".equals(stackingOrder)) {
            return "stackingOrder";
        }
        return "";
    }

    public String getSvgColor() {
        if (overlayColor != null && !"none".equals(overlayColor)) {
            return overlayColor;
        }
        return copySectionBgColor != null && !"none".equals(copySectionBgColor) ? copySectionBgColor : "";
    }

    public String getSvgBgColor() {
        return copySectionBgColor != null && !"none".equals(copySectionBgColor) ? copySectionBgColor : "";
    }

    public String getAlignClass() {
        if ("center".equals(copySectionAlignment)) return "align-center";
        if ("right".equals(copySectionAlignment)) return "align-right";
        return "align-left";
    }

    public String getMobileAlignClass() {
        if ("center".equals(mobileCopyAlignment)) return "m-align-center";
        if ("right".equals(mobileCopyAlignment)) return "m-align-right";
        return "m-align-left";
    }

    public String getHeadlineAlignmentClass() {
        if ("center".equals(headlineAlignment)) return "align-center";
        if ("right".equals(headlineAlignment)) return "align-right";
        return "align-left";
    }
    public String getSubHeadlineText()
    {
        return subHeadlineText;
    }
    public String getMobileHeadlineAlignmentClass() {
        if ("center".equals(mobileHeadlineAlignment)) return "m-align-center";
        if ("right".equals(mobileHeadlineAlignment)) return "m-align-right";
        return "m-align-left";
    }

    public String getHeadlineSizeClass() {
        if ("h3".equals(headlineSize)) return "heading3";
        if ("h4".equals(headlineSize)) return "heading4";
        return "heading2";
    }

    public boolean isHeadlineBorderEnabled() {
        return "yes".equalsIgnoreCase(headlineStyledBorder);
    }

    public String getBigImgStyleClass() {
        return resolveImageStyleClass(bigImageStyle);
    }

    public String getSmallImgStyleClass() {
        return resolveImageStyleClass(smallImageStyle);
    }

    private String resolveImageStyleClass(String style) {
        if ("rounded".equals(style)) return "img--rounded";
        if ("doubleRounded".equals(style)) return "img--double-rounded";
        if ("squared".equals(style)) return "img--squared";
        return "";
    }

    public String getContentFlipClass() {
        return "right".equals(imageAlignment) ? "content-flip" : "";
    }

    private String normalizeToken(String raw) {
        if (raw == null) {
            return "";
        }

        String normalized = raw.trim().toLowerCase();
        normalized = normalized.replaceAll("[_\\s]+", "-").replaceAll("[^a-z0-9\\-]", "");

        switch (normalized) {
            case "topleft":
            case "top-left":
                return "top-left";
            case "topcenter":
            case "top-center":
                return "top-center";
            case "topright":
            case "top-right":
                return "top-right";
            case "centerleft":
            case "center-left":
                return "center-left";
            case "center":
            case "centre":
                return "center";
            case "centerright":
            case "center-right":
                return "center-right";
            case "bottomleft":
            case "bottom-left":
                return "bottom-left";
            case "bottomcenter":
            case "bottom-center":
                return "bottom-center";
            case "bottomright":
            case "bottom-right":
                return "bottom-right";
            default:
                return "";
        }
    }

    private String normalizeSmallPosDesktop(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        return "small--" + normalizeToken(raw);
    }

    private String normalizeSmallPosMobile(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        return "m-small--" + normalizeToken(raw);
    }

    public String getSmallImgPosClassDesktop() {
        return smallImgPosClassDesktop;
    }

    public String getSmallImgPosClassMobile() {
        return smallImgPosClassMobile;
    }

    public String getHeadlineText() {
        return headlineText;
    }

    public String getResolvedHeadlineText() {
        if (headlineText == null || !headlineText.contains("{{country}}")) {
            return headlineText;
        }

        String country = getRequest().getParameter("country");

        if (country == null || country.isBlank()) {
            country = "your country";
        }

        return headlineText.replace("{{country}}", country);
    }

    public String getHeadlineStyledBorder() {
        return headlineStyledBorder;
    }

    public String getSubTitle1() {
        return subTitle1;
    }

    public String getShortDescription1() {
        return shortDescription1;
    }

    public String getSubTitle2() {
        return subTitle2;
    }

    public String getShortDescription2() {
        return shortDescription2;
    }

    public String getLogoAltText() {
        return logoAltText;
    }

    public String getBigImageAlt() {
        return bigImageAlt;
    }

    public String getSmallImageAlt() {
        return smallImageAlt;
    }

    public String getMobileBigImageAltText() {
        return mobileBigImageAltText;
    }

    public String getMobileSmallImageAltText() {
        return mobileSmallImageAltText;
    }

    public String getNumberOfImages() {
        return numberOfImages;
    }

    // public String getOverlayStyle() {
    //     return overlayStyle;
    // }

    public String getOverlayColor() {
        return overlayColor;
    }

    public String getCaption() {
        return caption;
    }

    public String getOverlayOpacity() {
        return overlayOpacity;
    }

    public List<Section> getSections() {
        return sections;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getBgVideo() {
        return mapOrRaw(bgVideo);
    }

    public String getBgVideoID() {
        return bgVideoID;
    }

    public String getVideoPoster() {
        return mapOrRaw(videoPoster);
    }

    public boolean hasVideo() {
        return "video".equals(mediaType) && bgVideo != null && !bgVideo.isEmpty();
    }

    public String getTimerCheckbox() {
        return timerCheckbox;
    }

    public String getTimerText() {
        return timerText;
    }

    public String getRedirectUrl() {
        Resource ctaResource = getResource().getChild("cta");
        if (ctaResource != null) {
            ValueMap ctaProps = ctaResource.getValueMap();

            String url = ctaProps.get("url", String.class);
            if (url != null && !url.isEmpty()) {
                return url;
            }

            url = ctaProps.get("ctaUrl", String.class);
            if (url != null && !url.isEmpty()) {
                return url;
            }

            url = ctaProps.get("link", String.class);
            if (url != null && !url.isEmpty()) {
                return url;
            }

            url = ctaProps.get("ctaLink", String.class);
            if (url != null && !url.isEmpty()) {
                return url;
            }

            url = ctaProps.get("linkUrl", String.class);
            if (url != null && !url.isEmpty()) {
                return url;
            }
        }

        ValueMap valueMap = getResource().getValueMap();
        return valueMap.get("redirectUrl", String.class);
    }

    public boolean hasRedirectUrl() {
        String url = getRedirectUrl();
        return url != null && !url.isEmpty();
    }

    public static class Section {
        private final String subheading;
        private final String description;

        public Section(String subheading, String description) {
            this.subheading = subheading;
            this.description = description;
        }

        public String getSubheading() {
            return subheading;
        }

        public String getDescription() {
            return description;
        }
    }

    public String getViewport() {
        return viewport;
    }
    
    
}