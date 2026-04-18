package com.mvw.core.models;

import com.mvw.core.utils.UrlUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CopyBlockModel {

    @ValueMapValue
    private String copySectionBgColor;

    @ValueMapValue
    private String copyAlignment;

    // @ValueMapValue
    // private String copyAlignmentDesktop;

    @ValueMapValue
    private String mobileCopyAlignment;

    @ValueMapValue
    private String copyText;

    @ChildResource
    private List<Resource> ctas;

    private List<CtaItem> ctaList;

    @Self
    private SlingHttpServletRequest request;

    
    @PostConstruct
    protected void init() {
        ctaList = new ArrayList<>();
        if (ctas != null) {
            for (Resource ctaResource : ctas) {
                ctaList.add(new CtaItem(ctaResource));
            }
        }
    }

    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }

    public String getCopyAlignment() {
        return copyAlignment;
    }

    // public String getCopyAlignmentDesktop() {
    //     return copyAlignmentDesktop;
    // }

    public String getMobileCopyAlignment() {
        return mobileCopyAlignment;
    }

    public String getCopyText() {
        String updatedPhone = (request != null) ? (String) request.getAttribute("phoneNumber") : null;
        if (copyText != null && updatedPhone != null && copyText.contains("{{phoneNumber}}")) {
            return copyText.replace("{{phoneNumber}}", updatedPhone);
        }
        return copyText;
    }

    public List<CtaItem> getCtas() {
        return ctaList != null ? ctaList : Collections.emptyList();
    }

    @Model(
            adaptables = Resource.class,
            defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public static class CtaItem {

        @ValueMapValue
        private String ctaText;

        @ValueMapValue
        private String ctaDestination;

        @ValueMapValue
        private String phoneNumber;

        @ValueMapValue
        private String ctaTab;

        @ValueMapValue
        private String ctaStyle;

        @ValueMapValue
        private String ctaPlacement;

        @ValueMapValue
        private String fontAwesomeIcon;

        @ValueMapValue
        private String appendPhoneNumber;

        @ValueMapValue
        private String thirdPartyLink;

        private ResourceResolver resolver;

        public CtaItem(Resource resource) {
            if (resource != null) {
                this.resolver = resource.getResourceResolver();
                this.ctaText = resource.getValueMap().get("ctaText", String.class);
                this.ctaDestination = resource.getValueMap().get("ctaDestination", String.class);
                this.phoneNumber = resource.getValueMap().get("phoneNumber", String.class);
                this.ctaTab = resource.getValueMap().get("ctaTab", "sameTab");
                this.ctaStyle = resource.getValueMap().get("ctaStyle", "tertiary");
                this.ctaPlacement = resource.getValueMap().get("ctaPlacement", "none");
                this.fontAwesomeIcon = resource.getValueMap().get("fontAwesomeIcon", String.class);
                this.appendPhoneNumber = resource.getValueMap().get("appendPhoneNumber", String.class);
                this.thirdPartyLink = resource.getValueMap().get("thirdPartyLink", String.class);
            }
        }

        public String getCtaText() {
            return ctaText;
        }

        public String getCtaDestination() {
            return UrlUtils.getNormalizedUrl(ctaDestination, resolver);
        }

        @Self
        private SlingHttpServletRequest request;

        public String getPhoneNumber() {
            if (request != null && request.getAttribute("phoneNumber") != null) {
                return (String) request.getAttribute("phoneNumber");
            }
            return phoneNumber;
        }

        public String getCtaTab() {
            return ctaTab;
        }

        public String getCtaStyle() {
            return ctaStyle;
        }

        public String getCtaPlacement() {
            return ctaPlacement;
        }

        public String getFontAwesomeIcon() {
            return fontAwesomeIcon;
        }

        public String getAppendPhoneNumber() {
            return appendPhoneNumber;
        }

        public String getThirdPartyLink() {
            return thirdPartyLink;
        }

    }
}