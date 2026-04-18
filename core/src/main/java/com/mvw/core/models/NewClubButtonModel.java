package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.mvw.core.utils.UrlUtils;

/**
 * Sling Model for the New Club button component.
 */
@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewClubButtonModel {

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String buttonText;

    @ValueMapValue
    private String buttonLink;

    @ValueMapValue
    private String buttonVariation;

    @ValueMapValue
    private String iconOnly;

    @ValueMapValue
    private String iconClass;

    @ValueMapValue
    private String thirdParty;

    @ValueMapValue
    private String opensIn;

    @ValueMapValue
    private String buttonHide;

    public String getButtonLink() {
        buttonLink = UrlUtils.getNormalizedUrl(buttonLink, resolver);
        return buttonLink;
    }
}
