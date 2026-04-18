package com.mvw.core.models;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

@Getter
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class LegalFooterModel {

    private static final Logger LOG = LoggerFactory.getLogger(LegalFooterModel.class);

    @ValueMapValue
    private String logoImage;

    @ValueMapValue
    private String logoImageAltText;

    @ChildResource
    private List<LegalBrandImages> brandDetails;

    @ValueMapValue
    private String footerText;

    @ValueMapValue
    private String legalText;

    @ValueMapValue(name = "equalImage/fileReference")
    private String equalImage;

    @ChildResource
    private List<LegalLinkPojo> legalLinks;

    @ValueMapValue
    private String legalLinkUrl;

    @ValueMapValue
    private String legalLinkTab;

    @ValueMapValue
    private String thirdParty;

    @ValueMapValue(name = "icon/fileReference")
    private String icon;

    @PostConstruct
    protected void init() {
        LOG.debug("LegalFooterModel initialized. brandDetails={}, legalLinks={}",
                brandDetails != null ? brandDetails.size() : 0,
                legalLinks != null ? legalLinks.size() : 0);
    }
}
