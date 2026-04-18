package com.mvw.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvw.core.utils.UrlUtils;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, adapters = { HeaderModel.class,
        ComponentExporter.class }, resourceType = "mvw/components/tmvc/components/header", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeaderModel implements ComponentExporter {

    // Required for request adaptable scenarios
    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resolver;

    @OSGiService
    private ModelFactory modelFactory;

    @SlingObject
    private Resource resource;
    // ---------------- PROMO ----------------

    @ValueMapValue
    private String promo1BgImage;
    @ValueMapValue
    private String promo2BgImage;
    @ValueMapValue
    private String promo1BgImageAlt;
    @ValueMapValue
    private String promo2BgImageAlt;
    @ValueMapValue
    private String promo1ShortDescription;
    @ValueMapValue
    private String promo2ShortDescription;
    @ValueMapValue
    private String promo1LinkText;
    @ValueMapValue
    private String promo2LinkText;
    @ValueMapValue
    private String promo1Url;
    @ValueMapValue
    private String promo2Url;
    @ValueMapValue
    private String promo1Tab;
    @ValueMapValue
    private String promo2Tab;

    // ---------------- CTA STYLE ----------------

    @ValueMapValue
    private String ctaStyle1;
    @ValueMapValue
    private String ctaStyle2;
    @ValueMapValue
    private String ctaAlignment1;
    @ValueMapValue
    private String ctaAlignment2;
    @ValueMapValue
    private String ctaSize1;
    @ValueMapValue
    private String ctaSize2;

    @ValueMapValue
    private String fontAwesomeIcon1;
    @ValueMapValue
    private String fontAwesomeIcon2;

    // ---------------- CONTACT ----------------

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

    // ---------------- LOGIN ----------------

    @ValueMapValue
    private String loginHeaderText;
    @ValueMapValue
    private String loginText;
    @ValueMapValue
    private String loginIcon;
    @ValueMapValue
    private String loginAlt;
    @ValueMapValue
    private String loginHeadline;

    // ---------------- MULTIFIELD ----------------

    @ChildResource
    private List<CTAItem> ctas;

    // ---------------- LOGO ----------------

    @ValueMapValue
    private String logoImage;
    @ValueMapValue
    private String logoAltText;
    @ValueMapValue
    private String logoUrl;
    @ValueMapValue
    private String accessibilityLabel;

    // ---------------- GETTERS ----------------

    @JsonProperty
    public String getPromo1BgImage() {
        return promo1BgImage;
    }

    @JsonProperty
    public String getPromo2BgImage() {
        return promo2BgImage;
    }

    @JsonProperty
    public String getPromo1BgImageAlt() {
        return promo1BgImageAlt;
    }

    @JsonProperty
    public String getPromo2BgImageAlt() {
        return promo2BgImageAlt;
    }

    @JsonProperty
    public String getPromo1ShortDescription() {
        return promo1ShortDescription;
    }

    @JsonProperty
    public String getPromo2ShortDescription() {
        return promo2ShortDescription;
    }

    @JsonProperty
    public String getPromo1LinkText() {
        return promo1LinkText;
    }

    @JsonProperty
    public String getPromo2LinkText() {
        return promo2LinkText;
    }

    @JsonProperty
    public String getPromo1Url() {
        return promo1Url;
    }

    @JsonProperty
    public String getPromo2Url() {
        return promo2Url;
    }

    @JsonProperty
    public String getPromo1Tab() {
        return promo1Tab;
    }

    @JsonProperty
    public String getPromo2Tab() {
        return promo2Tab;
    }

    @JsonProperty
    public String getCtaStyle1() {
        return ctaStyle1;
    }

    @JsonProperty
    public String getCtaStyle2() {
        return ctaStyle2;
    }

    @JsonProperty
    public String getCtaAlignment1() {
        return ctaAlignment1;
    }

    @JsonProperty
    public String getCtaAlignment2() {
        return ctaAlignment2;
    }

    @JsonProperty
    public String getCtaSize1() {
        return ctaSize1;
    }

    @JsonProperty
    public String getCtaSize2() {
        return ctaSize2;
    }

    @JsonProperty
    public String getFontAwesomeIcon1() {
        return fontAwesomeIcon1;
    }

    @JsonProperty
    public String getFontAwesomeIcon2() {
        return fontAwesomeIcon2;
    }

    @JsonProperty
    public String getContactUsCopy() {
        return contactUsCopy;
    }

    @JsonProperty
    public String getContactUsUrl() {
        return resolver != null ? UrlUtils.getNormalizedUrl(contactUsUrl, resolver) : contactUsUrl;
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
    public String getLoginHeaderText() {
        return loginHeaderText;
    }

    @JsonProperty
    public String getLoginText() {
        return loginText;
    }

    @JsonProperty
    public String getLoginIcon() {
        return loginIcon;
    }

    @JsonProperty
    public String getLoginAlt() {
        return loginAlt;
    }

    @JsonProperty
    public String getLoginHeadline() {
        return loginHeadline;
    }

    @JsonProperty
    public List<CTAItem> getCtas() {
        return ctas;
    }

    @JsonProperty
    public String getLogoImage() {
        return logoImage;
    }

    @JsonProperty
    public String getLogoAltText() {
        return logoAltText;
    }

    @JsonProperty
    public String getLogoUrl() {
        return resolver != null ? UrlUtils.getNormalizedUrl(logoUrl, resolver) : logoUrl;
    }

    @JsonProperty
    public String getAccessibilityLabel() {
        return accessibilityLabel;
    }

    // ---------------- REQUIRED ----------------

    @Override
    public String getExportedType() {
        return "mvw/components/tmvc/components/header";
    }

    @JsonProperty("mainNavigation")
    public CustomNavigationModel getMainNavigation() {
        Resource navResource = resource.getChild("main-navigation");

        if (navResource != null) {
            return modelFactory.getModelFromWrappedRequest(request, navResource, CustomNavigationModel.class);
        }

        return null;
    }

   @ChildResource(name = "cta")
private Resource ctaContainer;

private List<CTAItem> ctaItems;

@JsonProperty


@PostConstruct
protected void init() {
    ctaItems = new ArrayList<>();

    if (ctaContainer != null) {
        Resource ctasNode = ctaContainer.getChild("ctas");

        if (ctasNode != null) {
            for (Resource child : ctasNode.getChildren()) {
                CTAItem item = child.adaptTo(CTAItem.class);
                if (item != null) {
                    ctaItems.add(item);
                }
            }
        }
    }
}

public List<CTAItem> getCtaItems() {
    return ctaItems;
}

}

