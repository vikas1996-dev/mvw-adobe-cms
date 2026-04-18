package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.mvw.core.utils.UrlUtils;

import lombok.Getter;
import lombok.val;

@Getter
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class LegalLinkPojo {

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String legalLinkText;

    @ValueMapValue
    private String legalLinkUrl;

    @ValueMapValue
    private String legalLinkTab;

    @ValueMapValue
    private String cookieLink;

    @ValueMapValue
    private String thirdParty;

    @ValueMapValue(name = "icon/fileReference")
    private String icon;

    public String getLegalLinkUrl() {
        return UrlUtils.getNormalizedUrl(legalLinkUrl, resolver);
    }
}


