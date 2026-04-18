package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class QuoteItem {

    @ValueMapValue
    private String ctaText;

    @ValueMapValue
    private String ctaUrl;

    @ValueMapValue
    private String ctaStyle;

    @ValueMapValue
    private String ctaSize;

    @ValueMapValue
    private String ctaTab;

    public String getCtaText() {
        return ctaText;
    }

    public String getCtaUrl() {
        return ctaUrl;
    }

    public String getCtaStyle() {
        return ctaStyle;
    }

    public String getCtaSize() {
        return ctaSize;
    }

    public String getCtaTab() {
        return ctaTab;
    }
}
