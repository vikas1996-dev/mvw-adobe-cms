package com.mvw.core.models;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = org.apache.sling.api.SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CustomButton {

    @ValueMapValue
    private String ctaStyle;

    @ValueMapValue
    private String ctaSize;

    @ValueMapValue
    private String ctaAlignment;

    public String getCtaStyle() {
        return ctaStyle;
    }

    public String getCtaSize() {
        return ctaSize;
    }

    public String getCtaAlignment() {
        return ctaAlignment;
    }

}
