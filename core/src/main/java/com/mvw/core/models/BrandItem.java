package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BrandItem {

    @ValueMapValue
    private String brandLogoImage;

    @ValueMapValue
    private String brandLogoAltText;

    public String getBrandLogoImage() {
        return brandLogoImage;
    }

    public String getBrandLogoAltText() {
        return brandLogoAltText;
    }
}
