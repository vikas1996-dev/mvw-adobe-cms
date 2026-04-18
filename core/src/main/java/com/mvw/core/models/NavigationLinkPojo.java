package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.mvw.core.utils.UrlUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavigationLinkPojo {

    private static final Logger log = LoggerFactory.getLogger(NavigationLinkPojo.class);

    private static final String DOT_HTML_EXTENSION = ".html";

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String navLinkText;

    @ValueMapValue
    private String navLinkUrl;

    @ValueMapValue(name="navLinkUrl")
    private String navLinkUrlStaticValue;

    @ValueMapValue
    private String navHideLink;

    @ValueMapValue
    private String navLinkTab;

    @ValueMapValue
    private String thirdParty;

    private Page currentPage;

    public void setCurrentPage(Page currentPage) {
        this.currentPage = currentPage;
    }

    private boolean currentPageUrlFlag;

    public String getNavLinkUrl() {
        navLinkUrl = UrlUtils.getNormalizedUrl(navLinkUrl, resolver);
        return navLinkUrl;
    }

    public boolean isCurrentPageUrlFlag() {
        currentPageUrlFlag = navLinkUrlStaticValue.equals(currentPage.getPath());
        log.info("Values for - NavLinkUrlStaticValue: {} & currentPageUrlFlag: {}",
            navLinkUrlStaticValue, currentPageUrlFlag);
        return currentPageUrlFlag;
    }
}
