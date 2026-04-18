package com.mvw.core.models;

import com.mvw.core.utils.UrlUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { CustomNavigationModel.class,
        ComponentExporter.class }, resourceType = "mvw/components/tmvc/components/navigation", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomNavigationModel implements ComponentExporter {

    @SlingObject
    private ResourceResolver resolver;

    @Self
    @Via(type = ResourceSuperType.class)
    private Navigation navigation;

    @ValueMapValue
    private String promo1BgImage;

    @ValueMapValue
    private String promo1BgImageAlt;

    @ValueMapValue
    private String promo1ShortDescription;

    @ValueMapValue
    private String promo1Url;

    @ValueMapValue
    private String promo1Tab;

    @ValueMapValue
    private String ctaStyle1;

    @ValueMapValue
    private String ctaAlignment1;

    @ValueMapValue
    private String ctaSize1;

    @ValueMapValue
    private String promo1LinkText;

    @ValueMapValue
    private String promo2BgImage;

    @ValueMapValue
    private String promo2BgImageAlt;

    @ValueMapValue
    private String promo2ShortDescription;

    @ValueMapValue
    private String promo2Url;

    @ValueMapValue
    private String promo2Tab;

    @ValueMapValue
    private String ctaStyle2;

    @ValueMapValue
    private String ctaAlignment2;

    @ValueMapValue
    private String ctaSize2;

    @ValueMapValue
    private String promo2LinkText;

    @ValueMapValue
    private String contactUsCopy;

    @ValueMapValue
    private String contactUsUrl;

    @ValueMapValue
    private String contactUsTab;

    @ValueMapValue
    private String contactCtaStyle;

    @ValueMapValue
    private String contactCtaAlignment;

    @ValueMapValue
    private String contactCtaSize;

    @ValueMapValue
    private String contactUsIcon;

    @JsonProperty
    public String getContactUsCopy() {
        return contactUsCopy;
    }

    @JsonProperty
    public String getContactUsUrl() {
        return UrlUtils.getNormalizedUrl(contactUsUrl, resolver);
    }

    @JsonProperty
    public String getContactUsTab() {
        return contactUsTab;
    }

    @JsonProperty
    public String getContactCtaStyle() {
        return contactCtaStyle;
    }

    @JsonProperty
    public String getContactCtaAlignment() {
        return contactCtaAlignment;
    }

    @JsonProperty
    public String getContactCtaSize() {
        return contactCtaSize;
    }

    @JsonProperty
    public String getContactUsIcon() {
        return contactUsIcon;
    }

    @JsonProperty
    public Navigation getNavigation() {
        return navigation;
    }

    @JsonProperty
    public String getPromo1BgImage() {
        return promo1BgImage;
    }

    @JsonProperty
    public String getPromo1BgImageAlt() {
        return promo1BgImageAlt;
    }

    @JsonProperty
    public String getPromo1ShortDescription() {
        return promo1ShortDescription;
    }

    @JsonProperty
    public String getPromo1Url() {
        return UrlUtils.getNormalizedUrl(promo1Url, resolver);
    }

    @JsonProperty
    public String getPromo1Tab() {
        return promo1Tab;
    }

    @JsonProperty
    public String getCtaStyle1() {
        return ctaStyle1;
    }

    @JsonProperty
    public String getCtaAlignment1() {
        return ctaAlignment1;
    }

    @JsonProperty
    public String getCtaSize1() {
        return ctaSize1;
    }

    @JsonProperty
    public String getPromo1LinkText() {
        return promo1LinkText;
    }

    @JsonProperty
    public String getPromo2BgImage() {
        return promo2BgImage;
    }

    @JsonProperty
    public String getPromo2BgImageAlt() {
        return promo2BgImageAlt;
    }

    @JsonProperty
    public String getPromo2ShortDescription() {
        return promo2ShortDescription;
    }

    @JsonProperty
    public String getPromo2Url() {
        return UrlUtils.getNormalizedUrl(promo2Url, resolver);
    }

    @JsonProperty
    public String getPromo2Tab() {
        return promo2Tab;
    }

    @JsonProperty
    public String getCtaStyle2() {
        return ctaStyle2;
    }

    @JsonProperty
    public String getCtaAlignment2() {
        return ctaAlignment2;
    }

    @JsonProperty
    public String getCtaSize2() {
        return ctaSize2;
    }

    @JsonProperty
    public String getPromo2LinkText() {
        return promo2LinkText;
    }
    // ---------------- REQUIRED FOR EXPORTER ----------------

    @Override
    public String getExportedType() {
        return "mvw/components/tmvc/components/navigation"; // same as resourceType
    }
}