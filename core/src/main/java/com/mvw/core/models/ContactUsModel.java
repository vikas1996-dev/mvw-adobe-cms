package com.mvw.core.models;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import javax.annotation.PostConstruct;
import java.util.Map;
@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ContactUsModel {
    @Self
    private Resource resource;
    @ValueMapValue(name = "bg-color")
    private String bgColor;
    @ValueMapValue(name = "image-position")
    private String imagePosition;
    @ValueMapValue(name = "initial-headline-text")
    private String initialHeadlineText;
    @ValueMapValue(name = "initial-headline-size")
    private String initialHeadlineSize;
    @ValueMapValue(name = "initial-headline-alignment")
    private String initialHeadlineAlignment;
    @ValueMapValue(name = "initial-description")
    private String initialDescription;
    @ValueMapValue(name = "description-alignment")
    private String descriptionAlignment;
    @ValueMapValue(name = "initial-left-button-title")
    private String initialLeftButtonTitle;
    @ValueMapValue(name = "initial-right-button-title")
    private String initialRightButtonTitle;
    @ValueMapValue(name = "number-of-images")
    private String numberOfImages;
    @ValueMapValue(name = "big-image-alt")
    private String bigImageAlt;
    @ValueMapValue(name = "big-image-style")
    private String bigImageStyle;
    @ValueMapValue(name = "small-image-alt")
    private String smallImageAlt;
    @ValueMapValue(name = "small-image-position")
    private String smallImagePosition;
    @ValueMapValue(name = "no-timeshare-headline-text")
    private String noTimeshareHeadlineText;
    @ValueMapValue(name = "no-timeshare-headline-size")
    private String noTimeshareHeadlineSize;
    @ValueMapValue(name = "no-timeshare-headline-alignment")
    private String noTimeshareHeadlineAlignment;
    @ValueMapValue(name = "no-timeshare-description")
    private String noTimeshareDescription;
    @ValueMapValue(name = "no-timeshare-description-alignment")
    private String noTimeshareDescriptionAlignment;
    @ValueMapValue(name = "timeshare-headline-text")
    private String timeshareHeadlineText;
    @ValueMapValue(name = "timeshare-headline-size")
    private String timeshareHeadlineSize;
    @ValueMapValue(name = "timeshare-headline-alignment")
    private String timeshareHeadlineAlignment;
    @ValueMapValue(name = "timeshare-description-above-buttons")
    private String timeshareDescriptionAboveButtons;
    @ValueMapValue(name = "timeshare-description-alignment")
    private String timeshareDescriptionAlignment;
    @ValueMapValue(name = "left-button-text")
    private String leftButtonText;
    @ValueMapValue(name = "right-button-text")
    private String rightButtonText;
    @ValueMapValue(name = "timeshare-description-above-the-form")
    private String timeshareDescriptionAboveTheForm;
    @ValueMapValue(name = "timeshare-description-below-the-form")
    private String timeshareDescriptionBelowTheForm;
    @ValueMapValue(name = "mobile-headline-alignment")
    private String mobileHeadlineAlignment;
    @ValueMapValue(name = "hide-image-on-mobile")
    private Boolean hideImageOnMobile;
    @ValueMapValue(name = "stacking-order")
    private Boolean stackingOrder;

    private String bigImage;
    private String smallImage;

    @PostConstruct
    protected void init() {
        if (resource == null) {
            return;
        }
        bigImage = resolveImagePath("bigImage");
        smallImage = resolveImagePath("smallImage");
    }

    private String resolveImagePath(String propertyName) {
        if (resource == null) {
            return null;
        }
        ValueMap valueMap = resource.getValueMap();

        String direct = valueMap.get(propertyName, String.class);
        if (direct != null && !direct.isEmpty()) {
            return direct;
        }

        String[] suffixes = {
                "FileReference",
                "Reference",
                "fileReference"
        };
        for (String suffix : suffixes) {
            String value = valueMap.get(propertyName + suffix, String.class);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        Resource child = resource.getChild(propertyName);
        String deep = findFileReferenceDeep(child);
        if (deep != null) {
            return deep;
        }

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
        if (node == null) {
            return null;
        }

        ValueMap valueMap = node.getValueMap();
        String fileReference = valueMap.get("fileReference", String.class);
        if (fileReference != null && !fileReference.isEmpty()) {
            return fileReference;
        }

        Resource jcrContent = node.getChild("jcr:content");
        if (jcrContent != null) {
            ValueMap jcrValueMap = jcrContent.getValueMap();
            fileReference = jcrValueMap.get("fileReference", String.class);
            if (fileReference != null && !fileReference.isEmpty()) {
                return fileReference;
            }
        }

        for (Resource child : node.getChildren()) {
            if ("jcr:content".equals(child.getName())) {
                continue;
            }
            fileReference = findFileReferenceDeep(child);
            if (fileReference != null) {
                return fileReference;
            }
        }
        return null;
    }
    public String getBigImage() {
        return bigImage;
    }

    public String getSmallImage() {
        return smallImage;
    }
    public String getBgColor() {
        return StringUtils.defaultIfBlank(bgColor, "none");
    }
    public String getImagePosition() {
        return StringUtils.defaultIfBlank(imagePosition, "left");
    }
    public String getInitialHeadlineText() {
        return initialHeadlineText;
    }
    public String getInitialHeadlineSize() {
        return StringUtils.defaultIfBlank(initialHeadlineSize, "heading2");
    }
    public String getInitialHeadlineAlignment() {
        return StringUtils.defaultIfBlank(initialHeadlineAlignment, "center");
    }
    public String getInitialDescription() {
        return initialDescription;
    }
    public String getDescriptionAlignment() {
        return StringUtils.defaultIfBlank(descriptionAlignment, "center");
    }
    public String getInitialLeftButtonTitle() {
        return initialLeftButtonTitle;
    }
    public String getInitialRightButtonTitle() {
        return initialRightButtonTitle;
    }
    public String getNumberOfImages() {
        return StringUtils.defaultIfBlank(numberOfImages, "one");
    }
    public String getBigImageAlt() {
        return bigImageAlt;
    }
    public String getBigImageStyle() {
        return StringUtils.defaultIfBlank(bigImageStyle, "default");
    }
    public String getSmallImageAlt() {
        return smallImageAlt;
    }
    public String getSmallImagePosition() {
        return StringUtils.defaultIfBlank(smallImagePosition, "small--bottom-left");
    }
    public String getNoTimeshareHeadlineText() {
        return noTimeshareHeadlineText;
    }
    public String getNoTimeshareHeadlineSize() {
        return StringUtils.defaultIfBlank(noTimeshareHeadlineSize, "heading2");
    }
    public String getNoTimeshareHeadlineAlignment() {
        return StringUtils.defaultIfBlank(noTimeshareHeadlineAlignment, "left");
    }
    public String getNoTimeshareDescription() {
        return noTimeshareDescription;
    }
    public String getNoTimeshareDescriptionAlignment() {
        return StringUtils.defaultIfBlank(noTimeshareDescriptionAlignment, "left");
    }
    public String getTimeshareHeadlineText() {
        return timeshareHeadlineText;
    }
    public String getTimeshareHeadlineSize() {
        return StringUtils.defaultIfBlank(timeshareHeadlineSize, "heading2");
    }
    public String getTimeshareHeadlineAlignment() {
        return StringUtils.defaultIfBlank(timeshareHeadlineAlignment, "left");
    }
    public String getTimeshareDescriptionAboveButtons() {
        return timeshareDescriptionAboveButtons;
    }
    public String getTimeshareDescriptionAlignment() {
        return StringUtils.defaultIfBlank(timeshareDescriptionAlignment, "left");
    }
    public String getLeftButtonText() {
        return leftButtonText;
    }
    public String getRightButtonText() {
        return rightButtonText;
    }
    public String getTimeshareDescriptionAboveTheForm() {
        return timeshareDescriptionAboveTheForm;
    }
    public String getTimeshareDescriptionBelowTheForm() {
        return timeshareDescriptionBelowTheForm;
    }
    public String getMobileHeadlineAlignment() {
        return StringUtils.defaultIfBlank(mobileHeadlineAlignment, "center");
    }
    public Boolean getHideImageOnMobile() {
        return hideImageOnMobile != null ? hideImageOnMobile : false;
    }
    public Boolean getStackingOrder() {
        return stackingOrder != null ? stackingOrder : false;
    }
}