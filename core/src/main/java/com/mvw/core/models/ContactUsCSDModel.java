package com.mvw.core.models;

import com.mvw.core.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ContactUsCSDModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue(name = "form-type")
    private String formType;

    public boolean isOwner() {
        return "timeshare-owned-csd".equals(formType);
    }

    public boolean isProspect() {
        return "no-timeshare-prospect-csd".equals(formType);
    }


    @ValueMapValue
    private String copySectionBgColor;

    public String getCopySectionBgColor() {
        return copySectionBgColor != null ? copySectionBgColor : "none";
    }
    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineType;

    @ValueMapValue
    private String shortDescription;

    @ValueMapValue
    private String secondaryDescription;

    @ValueMapValue(name = "owner-headline-alignment")
    private String ownerHeadlineAlignment;

    public String getOwnerHeadlineText() {
        return headlineText;
    }

    public String getHeadlineType() {
        return headlineType != null ? headlineType : "heading3";
    }

    public String getOwnerShortDescription() {
        return shortDescription;
    }

    public String getOwnerSecondaryText() {
        return secondaryDescription;
    }

    public String getOwnerHeadlineAlignment() {
        return ownerHeadlineAlignment != null ? ownerHeadlineAlignment : "left";
    }


    @ValueMapValue(name = "propsect-headline-text")
    private String prospectHeadlineText;

    @ValueMapValue(name = "copyText")
    private String prospectCopyText;

    @ValueMapValue(name = "secondaryCopyText")
    private String prospectSecondaryText;

    @ValueMapValue(name = "prospect-headline-alignment")
    private String prospectHeadlineAlignment;

    public String getProspectHeadlineText() {
        return prospectHeadlineText;
    }

    public String getProspectCopyText() {
        return prospectCopyText;
    }

    public String getProspectSecondaryText() {
        return prospectSecondaryText;
    }

    public String getProspectHeadlineAlignment() {
        return prospectHeadlineAlignment != null ? prospectHeadlineAlignment : "left";
    }


    @ChildResource(name = "ctas")
    private List<Resource> ctas;

    private List<CtaItem> ctaList;


    @ChildResource(name = "speakButtons")
    private List<Resource> speakButtons;

    private List<SpeakButtonItem> speakButtonList;

    @PostConstruct
    protected void init() {

        ctaList = new ArrayList<>();
        if (ctas != null) {
            for (Resource resource : ctas) {
                ctaList.add(new CtaItem(resource));
            }
        }

        speakButtonList = new ArrayList<>();
        if (speakButtons != null) {
            for (Resource resource : speakButtons) {
                speakButtonList.add(new SpeakButtonItem(resource));
            }
        }
    }

    public List<CtaItem> getCtas() {
        return ctaList != null ? ctaList : Collections.emptyList();
    }

    public List<SpeakButtonItem> getSpeakButtons() {
        return speakButtonList != null ? speakButtonList : Collections.emptyList();
    }


    public static class CtaItem {

        private final ResourceResolver resolver;
        private final String ctaText;
        private final String ctaUrl;
        private final String phoneNumber;
        private final String ctaTab;
        private final String ctaStyle;

        public CtaItem(Resource resource) {
            this.resolver = resource.getResourceResolver();
            this.ctaText = resource.getValueMap().get("ctaText", String.class);
            this.ctaUrl = resource.getValueMap().get("ctaUrl", String.class);
            this.phoneNumber = resource.getValueMap().get("phoneNumber", String.class);
            this.ctaTab = resource.getValueMap().get("ctaOpensIn", "sameTab");
            this.ctaStyle = resource.getValueMap().get("ctaStyle", "primary");
        }

        public String getCtaText() {
            return ctaText;
        }

        public String getHref() {
            if (StringUtils.isNotEmpty(phoneNumber)) {
                return "tel:" + phoneNumber;
            }
            return UrlUtils.getNormalizedUrl(ctaUrl, resolver);

//            return ctaUrl;
        }

        public String getTarget() {
            return "newTab".equals(ctaTab) ? "_blank" : "_self";
        }

        public String getCtaStyle() {
            return ctaStyle;
        }

        public boolean isPhone() {
            return phoneNumber != null && !phoneNumber.isEmpty();
        }
    }


    public static class SpeakButtonItem {

        private final ResourceResolver resolver;
        private final String ctaText;
        private final String ctaUrl;

        public SpeakButtonItem(Resource resource) {
            this.resolver = resource.getResourceResolver();
            this.ctaText = resource.getValueMap().get("ctaText", String.class);
            this.ctaUrl = resource.getValueMap().get("ctaUrl", String.class);
        }

        public String getCtaText() {
            return ctaText;
        }

        public String getHref() {
            return UrlUtils.getNormalizedUrl(ctaUrl, resolver);
        }
    }
}
