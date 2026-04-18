package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BonvoyBlockCardModel {

    @ValueMapValue
    private String headline;

    @ValueMapValue
    private String headlineSize;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String copyAlignment;

    @ValueMapValue
    private String mobileCopyAlignment;

    @Inject
    private List<BrandItem> brandLogos;

    public String getHeadline() {
        return headline;
    }

    public String getHeadlineSize() {
        return headlineSize;
    }

    public String getDescription() {
        return description;
    }

    public String getCopyAlignment() {
        return copyAlignment;
    }

    public String getMobileCopyAlignment() {
        return mobileCopyAlignment;
    }

    public List<BrandItem> getBrandLogos() {
        return brandLogos;
    }

    public String getHeadlineTag() {
        if (headlineSize != null && headlineSize.matches("h[2-6]")) {
            return headlineSize;
        }
        return "h3";
    }
}
