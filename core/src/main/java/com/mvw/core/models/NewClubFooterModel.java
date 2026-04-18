package com.mvw.core.models;

import com.mvw.core.utils.UrlUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import java.util.List;

@Getter
@Model(adaptables = Resource.class,
        defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
public class NewClubFooterModel {

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String logoImage;

    @ValueMapValue
    private String logoImageAltText;

    @ValueMapValue
    private String homeLink;

    @ValueMapValue
    private String footerCopy;

    @ValueMapValue
    private String contactUsText;

    @ValueMapValue
    private String legalCopy;

    @ValueMapValue
    private String hideSectionA;

    @ValueMapValue
    private String hideSectionB;

    @ValueMapValue
    private String navigationHeading;

    @ValueMapValue
    private String navigationHeadingColTwo;

    @ValueMapValue
    private String navigationHeadingColThree;

    @ChildResource
    private List<NavigationLinkPojo> ctaNavLinks;

    @ChildResource
    private List<NavigationLinkPojo> ctaNavLinksCol2;

    @ChildResource
    private List<NavigationLinkPojo> ctaNavLinksCol3;

    @ChildResource
    private List<NavigationLinkPojo> socialMediaLinks;

    @ChildResource
    private List<LegalLinkPojo> legalLinks;

    public String getHomeLink() {
        return UrlUtils.getNormalizedUrl(homeLink, resolver);
    }
}
