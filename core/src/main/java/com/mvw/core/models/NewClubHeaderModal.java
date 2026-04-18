package com.mvw.core.models;

import java.util.List;

import com.day.cq.wcm.api.Page;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.*;

import com.mvw.core.utils.UrlUtils;

import javax.annotation.PostConstruct;

@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewClubHeaderModal {

    @SlingObject
    private ResourceResolver resolver;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    private String logoImage;

    @ValueMapValue
    private String logoImageAltText;

    @ValueMapValue
    private String homeLink;
   
    @ValueMapValue
    private String buttonHide;

    @Self
    @Via("resource")
    private NewClubButtonModel newClubButtonModel;

    @ChildResource
    private List<NavigationLinkPojo> navigationLink;

    @PostConstruct
    protected void init() {
        if (navigationLink != null && currentPage != null) {
            navigationLink.forEach(link ->
                link.setCurrentPage(currentPage)
            );
        }
    }

    public String getHomeLink() {
        homeLink = UrlUtils.getNormalizedUrl(homeLink, resolver);
        return homeLink;
    }

}
