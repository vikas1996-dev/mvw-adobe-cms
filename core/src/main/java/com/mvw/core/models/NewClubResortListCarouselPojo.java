package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.api.resource.ResourceResolver;
import com.mvw.core.utils.UrlUtils;

@Getter
@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewClubResortListCarouselPojo {

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String disableLazyLoading;

    @ValueMapValue
    private String altText;

    @ValueMapValue
    private String ctaText;

    @ValueMapValue
    private String ctaLink;

    @ValueMapValue
    private String opensIn;

    @ValueMapValue
    private String thirdParty;

    public String getCtaLink() {
       ctaLink =  UrlUtils.getNormalizedUrl(ctaLink, resolver);
        return ctaLink;
    }
}
 