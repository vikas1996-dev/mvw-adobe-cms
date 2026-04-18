package com.mvw.core.models;



import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SingleQA {

    @ValueMapValue
    private String headlineText;
    @ValueMapValue
    private String headlineType;
    @ValueMapValue
    private String logo;
    @ValueMapValue
    private String bgImageAlt;
    @ValueMapValue
    private String copySectionBgColor;
    @ValueMapValue
    private String brandIconImageBgColor;
    @ValueMapValue
    private String copyAlignment;
    @ValueMapValue
    private String copyAlignmentDesktop;

    public String getHeadlineText() {
        return headlineText;
    }
    public String getLogo() {
        return logo;
    }
    public String getBgImageAlt() {
        return bgImageAlt;
    }
    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }
    public String getBrandIconImageBgColor() {
        return brandIconImageBgColor;
    }
    public String getCopyAlignment() {
        return copyAlignment;
    }
    public String getHeadlineType() {
        return headlineType;
    }
    public String getCopyAlignmentDesktop() {
        return copyAlignmentDesktop;
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