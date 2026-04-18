package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageHeroModel {

    @ValueMapValue
    private String backgroundImage;

    @ValueMapValue
    private String bgPositionLeftAndRight;

    @ValueMapValue
    private String bgPositionTopAndBottom;

    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineAlignment;

    @ValueMapValue
    private String shortDescription;

    @ValueMapValue 
    private String copySectionAlignment;

    @ValueMapValue
    private String copySectionBgColor;

    @ValueMapValue
    private String copySectionBgBorderControl;

    @ValueMapValue
    private String backgroundTransControl;

    @ValueMapValue
    private String mobileBackgroundImage;

    @ValueMapValue
    private String mobileBgPositionLeftAndRight;

    @ValueMapValue
    private String mobileBgPositionTopAndBottom;

    @ValueMapValue
    private String mobileHeadlineAlignment;

    @ValueMapValue
    private String mobileShortDescriptionAlignment;

    @ValueMapValue
    private String mobileBgColor;

    @ValueMapValue
    private String mobileBgColorBorderControl;

    @ValueMapValue
    private String mobileBackgroundTransControl;

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public String getBgPositionLeftAndRight() {
        return bgPositionLeftAndRight;
    }

    public String getBgPositionTopAndBottom() {
        return bgPositionTopAndBottom;
    }

    public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineAlignment() {
        return headlineAlignment;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getCopySectionAlignment() {
        return copySectionAlignment;
    }

    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }

    public String getCopySectionBgBorderControl() {
        return copySectionBgBorderControl;
    }

    public String getBackgroundTransControl() {
        return backgroundTransControl;
    }

    public String getMobileBackgroundImage() {
        return mobileBackgroundImage;
    }

    public String getMobileBgPositionLeftAndRight() {
        return mobileBgPositionLeftAndRight;
    }

    public String getMobileBgPositionTopAndBottom() {
        return mobileBgPositionTopAndBottom;
    }

    public String getMobileHeadlineAlignment() {
        return mobileHeadlineAlignment;
    }

    public String getMobileShortDescriptionAlignment() {return mobileShortDescriptionAlignment; }

    public String getMobileBgColor() {
        return mobileBgColor;
    }

    public String getMobileBgColorBorderControl() {
        return mobileBgColorBorderControl;
    }

    public String getMobileBackgroundTransControl() {
        return mobileBackgroundTransControl;
    }

} 