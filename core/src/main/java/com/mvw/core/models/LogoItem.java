package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.mvw.core.utils.UrlUtils;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.api.resource.ResourceResolver;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LogoItem {

    @ValueMapValue
    private String logoImage;

    @ValueMapValue
    private String logoAltText;

    @ValueMapValue
    private String logoCtaUrl;

    @ValueMapValue
    private String logoCtaTab;

    @ValueMapValue
    private String ariaLabel;
    
    @SlingObject
    private ResourceResolver resolver;

    public String getAriaLabel() {
        return ariaLabel;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public String getLogoAltText() {
        return logoAltText;
    }

    public String getLogoCtaUrl() {
        return UrlUtils.getNormalizedUrl(logoCtaUrl, resolver);
    }

    public String getLogoCtaTab() {
        return logoCtaTab;
    }

}
