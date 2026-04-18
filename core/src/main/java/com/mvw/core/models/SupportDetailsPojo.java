package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.mvw.core.utils.UrlUtils;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SupportDetailsPojo {

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String iconClass;

    @ValueMapValue
    private String label;

    @ValueMapValue
    private String linkText;

    @ValueMapValue
    private String buttonLink;

    public String getButtonLink() {
        return (UrlUtils.getNormalizedUrl(buttonLink, resolver));
    }

}
