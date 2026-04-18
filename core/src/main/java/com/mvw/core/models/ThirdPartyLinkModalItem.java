package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ThirdPartyLinkModalItem {

    @ValueMapValue
    private String ctaText;

    @ValueMapValue
    private String ctaStyle;

    @ValueMapValue
    private String ctaPlacement;

    @ValueMapValue
    private String ctaSize;

    @ValueMapValue
    private String ctaTab;

    public String getCtaText() {
        return ctaText;
    }

    public String getCtaStyle() {
        return ctaStyle != null ? ctaStyle : "Primary Button";
    }

    public String getCtaPlacement() {
        return ctaPlacement != null ? ctaPlacement : "None";
    }

    public String getCtaSize() {
        return ctaSize != null ? ctaSize : "Large";
    }

    public String getCtaTab() {
        return ctaTab != null ? ctaTab : "Same Tab";
    }
}