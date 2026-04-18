package com.mvw.core.models;

import java.util.List;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvw.core.utils.UrlUtils;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, adapters = { MarriottFooterModel.class,
        ComponentExporter.class }, resourceType = "mvw/components/tmvc/components/footerMarriott-v1", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarriottFooterModel implements ComponentExporter {

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private String logoAltText;

    @ValueMapValue
    private String logoURL;

    @ValueMapValue
    private String logoURLOpens;

    @ChildResource
    private List<BrandLogo> brandLogos;

    @ChildResource
    private List<CTA> cta1;

    @ChildResource
    private List<CTA> cta2;

    @ValueMapValue
    private String headlineText;

    @ChildResource
    private List<newCTA> cta3;

    @ValueMapValue
    private String headingText;

    @ValueMapValue
    private String ctaText4;

    @ValueMapValue
    private String ctaStyle4;

    @ValueMapValue
    private String phoneNumber;

    @ValueMapValue
    private String hoursTitle;

    @ValueMapValue
    private String hoursDescription;

    @ValueMapValue
    private String legalSubFooterCopy;

    @ChildResource
    private List<legalLink> legalLinks;

    @ValueMapValue
    private String appendPhoneNumber;

    @SlingObject
    private ResourceResolver resolver;

    @Self
    private SlingHttpServletRequest request;

    public String getAppendPhoneNumber() {
        return appendPhoneNumber;
    }

    public String getPhoneNumber() {
        if (request != null && request.getAttribute("phoneNumber") != null) {
            return (String) request.getAttribute("phoneNumber");
        }
        return phoneNumber;
    }

    @JsonProperty
    public String getHoursTitle() {
        return hoursTitle;
    }

    @JsonProperty
    public String getHoursDescription() {
        return hoursDescription;
    }

    @JsonProperty
    public String getLegalSubFooterCopy() {
        return legalSubFooterCopy;
    }

    @JsonProperty
    public List<legalLink> getLegalLinks() {
        return legalLinks;
    }

    @JsonProperty
    public String getFileReference() {
        return fileReference;
    }

    @JsonProperty
    public String getLogoAltText() {
        return logoAltText;
    }

    @JsonProperty
    public String getLogoURL() {
        return UrlUtils.getNormalizedUrl(logoURL, resolver);
    }

    @JsonProperty
    public String getLogoURLOpens() {
        return logoURLOpens;
    }

    @JsonProperty
    public String getCtaText4() {
        String updatedPhone = (request != null) ? (String) request.getAttribute("phoneNumber") : null;
        if (ctaText4 != null && updatedPhone != null && ctaText4.contains("{{phoneNumber}}")) {
            return ctaText4.replace("{{phoneNumber}}", updatedPhone);
        }
        return ctaText4;
    }

    @JsonProperty
    public String getCtaStyle4() {
        return ctaStyle4;
    }

    @JsonProperty
    public List<CTA> getCta1() {
        return cta1;
    }

    @JsonProperty
    public List<CTA> getCta2() {
        return cta2;
    }

    @JsonProperty
    public List<newCTA> getCta3() {
        return cta3;
    }

    @JsonProperty
    public List<BrandLogo> getBrandLogos() {
        return brandLogos;
    }

    @JsonProperty
    public String getHeadlineText() {
        return headlineText;
    }

    @JsonProperty
    public String getHeadingText() {
        return headingText;
    }

    @Model(adaptables = { Resource.class,
            SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class BrandLogo {

        @ValueMapValue
        private String fileReference;

        @ValueMapValue
        private String logoCTAURL;

        @ValueMapValue
        private String logoAltText;

        @ValueMapValue
        private String logoCTAOpens;

        @ValueMapValue
        private String ariaLabel;

        @SlingObject
        private ResourceResolver resolver;

        @JsonProperty
        public String getAriaLabel() {
            return ariaLabel;
        }

        @JsonProperty
        public String getFileReference() {
            return fileReference;
        }

        @JsonProperty
        public String getLogoCTAURL() {
            return UrlUtils.getNormalizedUrl(logoCTAURL, resolver);
        }

        @JsonProperty
        public String getLogoCTAOpens() {
            return logoCTAOpens;
        }

        @JsonProperty
        public String getLogoAltText() {
            return logoAltText;
        }

    }

    @Model(adaptables = { Resource.class,
            SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class CTA {

        @ValueMapValue
        private String ctaText;

        @ValueMapValue
        private String ctaURL;

        @ValueMapValue
        private String ctaOpens;

        @ValueMapValue
        private String showNewTabIcon;

        @ValueMapValue
        private String thirdParty;

        @SlingObject
        private ResourceResolver resolver;

        @JsonProperty
        public String getShowNewTabIcon() {
            return showNewTabIcon;
        }

        @JsonProperty
        public String getThirdParty() {
            return thirdParty;
        }

        @JsonProperty
        public String getCtaText() {
            return ctaText;
        }

        @JsonProperty
        public String getCtaURL() {
            return UrlUtils.getNormalizedUrl(ctaURL, resolver);
        }

        @JsonProperty
        public String getCtaOpens() {
            return ctaOpens;
        }
    }

    @Model(adaptables = { Resource.class,
            SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class newCTA {

        @ValueMapValue
        private String ctaText;

        @ValueMapValue
        private String ctaURL;

        @ValueMapValue
        private String ctaOpens;

        @ValueMapValue
        private String facebook;

        @ValueMapValue
        private String instagram;

        @ValueMapValue
        private String youtube;

        @ValueMapValue
        private String pinterest;

        @ValueMapValue
        private String x;

        @ValueMapValue
        private String linkedIn;

        @ValueMapValue
        private String tiktok;

        @ValueMapValue
        private String fbURL;

        @ValueMapValue
        private String instaURL;

        @ValueMapValue
        private String ytURL;

        @ValueMapValue
        private String ttURL;

        @ValueMapValue
        private String piURL;

        @ValueMapValue
        private String xURL;

        @ValueMapValue
        private String liURL;

        @ValueMapValue
        private String showNewTabIcon;

        @ValueMapValue
        private String thirdParty;

        @ValueMapValue
        private String fbCtaTab;

        @ValueMapValue
        private String fbShowNewTabIcon;

        @ValueMapValue
        private String fbThirdParty;

        @ValueMapValue
        private String instaCtaTab;

        @ValueMapValue
        private String instaShowNewTabIcon;

        @ValueMapValue
        private String instaThirdParty;

        @ValueMapValue
        private String ytCtaTab;

        @ValueMapValue
        private String ytShowNewTabIcon;

        @ValueMapValue
        private String ytThirdParty;

        @ValueMapValue
        private String ttCtaTab;

        @ValueMapValue
        private String ttShowNewTabIcon;

        @ValueMapValue
        private String ttThirdParty;

        @ValueMapValue
        private String piCtaTab;

        @ValueMapValue
        private String piShowNewTabIcon;

        @ValueMapValue
        private String piThirdParty;

        @ValueMapValue
        private String xCtaTab;

        @ValueMapValue
        private String xShowNewTabIcon;

        @ValueMapValue
        private String xThirdParty;

        @ValueMapValue
        private String liCtaTab;

        @ValueMapValue
        private String liShowNewTabIcon;

        @ValueMapValue
        private String liThirdParty;

        @SlingObject
        private ResourceResolver resolver;

        @JsonProperty
        public String getFbCtaTab() {
            return fbCtaTab;
        }

        @JsonProperty
        public String getInstaCtaTab() {
            return instaCtaTab;
        }

        @JsonProperty
        public String getYtCtaTab() {
            return ytCtaTab;
        }

        @JsonProperty
        public String getTtCtaTab() {
            return ttCtaTab;
        }

        @JsonProperty
        public String getPiCtaTab() {
            return piCtaTab;
        }

        @JsonProperty
        public String getXCtaTab() {
            return xCtaTab;
        }

        @JsonProperty
        public String getLiCtaTab() {
            return liCtaTab;
        }

        @JsonProperty
        public String getFbShowNewTabIcon() {
            return fbShowNewTabIcon;
        }

        @JsonProperty
        public String getFbThirdParty() {
            return fbThirdParty;
        }

        @JsonProperty
        public String getInstaShowNewTabIcon() {
            return instaShowNewTabIcon;
        }

        @JsonProperty
        public String getInstaThirdParty() {
            return instaThirdParty;
        }

        @JsonProperty
        public String getYtShowNewTabIcon() {
            return ytShowNewTabIcon;
        }

        @JsonProperty
        public String getYtThirdParty() {
            return ytThirdParty;
        }

        @JsonProperty
        public String getTtShowNewTabIcon() {
            return ttShowNewTabIcon;
        }

        @JsonProperty
        public String getTtThirdParty() {
            return ttThirdParty;
        }

        @JsonProperty
        public String getPiShowNewTabIcon() {
            return piShowNewTabIcon;
        }

        @JsonProperty
        public String getPiThirdParty() {
            return piThirdParty;
        }

        @JsonProperty
        public String getXShowNewTabIcon() {
            return xShowNewTabIcon;
        }

        @JsonProperty
        public String getXThirdParty() {
            return xThirdParty;
        }

        @JsonProperty
        public String getLiShowNewTabIcon() {
            return liShowNewTabIcon;
        }

        @JsonProperty
        public String getLiThirdParty() {
            return liThirdParty;
        }

        @JsonProperty
        public String getShowNewTabIcon() {
            return showNewTabIcon;
        }

        @JsonProperty
        public String getThirdParty() {
            return thirdParty;
        }

        @JsonProperty
        public String getFbURL() {
            return fbURL;
        }

        @JsonProperty
        public String getInstaURL() {
            return instaURL;
        }

        @JsonProperty
        public String getYtURL() {
            return ytURL;
        }

        @JsonProperty
        public String getTtURL() {
            return ttURL;
        }

        @JsonProperty
        public String getPiURL() {
            return piURL;
        }

        @JsonProperty
        public String getXURL() {
            return xURL;
        }

        @JsonProperty
        public String getLiURL() {
            return liURL;
        }

        @JsonProperty
        public String isPinterest() {
            return pinterest;
        }

        @JsonProperty
        public String isX() {
            return x;
        }

        @JsonProperty
        public String isFacebook() {
            return facebook;
        }

        @JsonProperty
        public String isInstagram() {
            return instagram;
        }

        @JsonProperty
        public String isYoutube() {
            return youtube;
        }

        @JsonProperty
        public String isLinkedIn() {
            return linkedIn;
        }

        @JsonProperty
        public String isTiktok() {
            return tiktok;
        }

        @JsonProperty
        public String getCtaText() {
            return ctaText;
        }

        @JsonProperty
        public String getCtaURL() {
            return (UrlUtils.getNormalizedUrl(ctaURL, resolver));
        }

        @JsonProperty
        public String getCtaOpens() {
            return ctaOpens;
        }
    }

    @Model(adaptables = { Resource.class,
            SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class legalLink {

        @ValueMapValue
        private String ctaText;

        @ValueMapValue
        private String ctaURL;

        @ValueMapValue
        private String ctaStyles;

        @ValueMapValue
        private String ctaOpens;

        @ValueMapValue
        private String fileReference;

        @ValueMapValue
        private String iconNewAlt;

        @ValueMapValue
        private String showNewTabIcon;

        @ValueMapValue(name = "icon-url")
        private String iconUrl;

        @ValueMapValue
        private boolean thirdPartylink;

        @ValueMapValue
        private String cssClass;

        @ValueMapValue
        private boolean enableCookieSetting;

        @SlingObject
        private ResourceResolver resolver;

        @JsonProperty
        public String getCtaText() {
            return ctaText;
        }

        @JsonProperty
        public String getCtaURL() {
            if (enableCookieSetting) {
                return "#";
            }
            return UrlUtils.getNormalizedUrl(ctaURL, resolver);
        }

        @JsonProperty
        public String getCtaOpens() {
            if (enableCookieSetting) {
                return null;
            }
            return ctaOpens;
        }

        @JsonProperty
        public String getCtaStyles() {
            return ctaStyles;
        }

        @JsonProperty
        public String getFileReference() {
            return fileReference;
        }

        @JsonProperty
        public String getCssClass() {
            return cssClass;
        }

        @JsonProperty
        public String getIconNewAlt() {
            return iconNewAlt;
        }

        @JsonProperty
        public String getIconUrl() {
            return UrlUtils.getNormalizedUrl(iconUrl, resolver);
        }

        @JsonProperty
        public boolean isThirdPartylink() {
            return thirdPartylink;
        }

        @JsonProperty
        public boolean isEnableCookieSetting() {
            return enableCookieSetting;
        }

        @JsonProperty
        public String getShowNewTabIcon() {
            return showNewTabIcon;
        }
    }

    @Override
    public String getExportedType() {
        return "mvw/components/tmvc/components/footerMarriott-v1";
    }
}