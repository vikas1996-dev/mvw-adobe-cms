package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CityCollection {

    @ValueMapValue
    private String headlineText;
    @ValueMapValue
    private String headlineType;

   public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineType() {
        return headlineType;
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