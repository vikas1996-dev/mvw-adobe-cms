package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.mvw.core.utils.UrlUtils;

import lombok.Getter;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LegalBrandImages {


    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String brandImage;

    @ValueMapValue
    private String brandImageAltText;

    @ValueMapValue
    private String brandImageLink;

    @ValueMapValue
    private String brandImageLinkTarget;

    @ValueMapValue
    private String ariaLabel;

    public String getBrandImageLinkTarget() {
        return UrlUtils.getNormalizedUrl(brandImageLinkTarget, resolver);
    }
}
