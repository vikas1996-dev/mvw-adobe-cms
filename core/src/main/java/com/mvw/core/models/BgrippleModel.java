package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BgrippleModel {

    @ValueMapValue
    private String bgImg;

    @ValueMapValue
    private String bgAlt;

    @ValueMapValue
    private String hideSpacing;

    public String getBgImg() {
        return bgImg;
    }

    public String getBgAlt() {
        return bgAlt;
    }

    public String getHideSpacing() {
        return hideSpacing;
    }


}