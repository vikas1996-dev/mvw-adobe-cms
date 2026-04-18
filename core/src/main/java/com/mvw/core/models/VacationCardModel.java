package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class VacationCardModel {

    /** Primary Copy Section **/
    @ValueMapValue
    private String style;

    @ValueMapValue
    private String subheader;

    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineType;

    @ValueMapValue
    private String headlineAlignment;

    @ValueMapValue
    private String headlineStyledBorder;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private String backgroundColor;

    /** Secondary Copy Section **/
    @ValueMapValue
    private String secondaryCopyText;

    /** Mobile Section **/
    @ValueMapValue
    private String brandIconImageBgColor;

    @ValueMapValue
    private String copyAlignment;

    @ValueMapValue
    private String secondaryCopyAlignment;

    @ValueMapValue
    private String stackOnMobile;

    public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineType() {
        return headlineType;
    }

    public String getHeadlineAlignment() {
        return headlineAlignment;
    }

    public String getCopyText() {
        return copyText;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getSecondaryCopyText() {
        return secondaryCopyText;
    }

    public String getBrandIconImageBgColor() {
        return brandIconImageBgColor;
    }

    public String getCopyAlignment() {
        return copyAlignment;
    }

    public String getSecondaryCopyAlignment() {
        return secondaryCopyAlignment;
    }

    public String getStackOnMobile() {
        return stackOnMobile;
    }

    public String getStyle() {
    return style;
    }

    public String getSubheader() {
        return subheader;
    }

    public String getHeadlineStyledBorder() {
        return headlineStyledBorder;
    }

    @ValueMapValue
    private String headlineTag;

    public String getHeadlineTag() {
        if (headlineType != null && headlineType.matches("heading[1-6]")) {
            headlineTag = "h" + headlineType.substring(7);
            return headlineTag;
        }
        return "h2";
    }
}