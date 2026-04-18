package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BonvoyBlockContainerModel {

    @ValueMapValue
    private String logo;

    @ValueMapValue
    private String logoAltText;

    @ValueMapValue
    private String headline;

    @ValueMapValue
    private String headlineSize;

    @ValueMapValue
    private String copyAlignment;

    @ValueMapValue
    private String mobileCopyAlignment;

    public String getLogo() {
        return logo;
    }

    public String getLogoAltText() {
        return logoAltText;
    }

    public String getHeadline() {
        return headline;
    }

    public String getHeadlineSize() {
        return headlineSize;
    }

    public String getCopyAlignment() {
        return copyAlignment;
    }

    public String getMobileCopyAlignment() {
        return mobileCopyAlignment;
    }

    public String getHeadlineTag() {
        if (headlineSize != null && headlineSize.matches("h[2-6]")) {
            return headlineSize;
        }
        return "h2";
    }
}
