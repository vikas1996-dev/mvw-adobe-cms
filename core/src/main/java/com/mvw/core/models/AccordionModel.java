package com.mvw.core.models;

import com.mvw.core.utils.UrlUtils;
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
public class AccordionModel {

    @ValueMapValue
    private String headlineType;

    @ValueMapValue
    private String backgroundColor;

    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineStyledBorder;

    @ValueMapValue
    private String generateFaqSchema;

    public String getGenerateFaqSchema() {
        return generateFaqSchema;
    }

    @ValueMapValue
    private String headlineTag;

    @ChildResource
    private List<Resource> ctas;

    private List<CtaItem> ctaList;

    @PostConstruct
    protected void init() {
        ctaList = new ArrayList<>();
        if (ctas != null) {
            for (Resource ctaResource : ctas) {
                ctaList.add(new CtaItem(ctaResource));
            }
        }
    }

    public String getHeadlineType() {
        return headlineType;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineStyledBorder() {
        return headlineStyledBorder;
    }

    public String getHeadlineTag() {
        if (headlineType != null && headlineType.matches("heading[1-6]")) {
            headlineTag = "h" + headlineType.substring(7);
            return headlineTag;
        }
        return "h2";
    }

    public List<CtaItem> getCtas() {
        return ctaList != null ? ctaList : Collections.emptyList();
    }

    @Model(
            adaptables = Resource.class,
            defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public static class CtaItem {

        @SlingObject
        ResourceResolver resolver;

        @ValueMapValue
        private String ctaText;

        @ValueMapValue
        private String ctaDestination;

        @ValueMapValue
        private String ctaTab;

        @ValueMapValue
        private String ctaStyle;

        @ValueMapValue
        private String ctaPlacement;

        @ValueMapValue
        private String fontAwesomeIcon;

        public CtaItem(Resource resource) {
            if (resource != null) {
                this.ctaText = resource.getValueMap().get("ctaText", String.class);
                this.ctaDestination = resource.getValueMap().get("ctaDestination", String.class);
                this.ctaTab = resource.getValueMap().get("ctaTab", "sameTab");
                this.ctaStyle = resource.getValueMap().get("ctaStyle", "tertiary");
                this.ctaPlacement = resource.getValueMap().get("ctaPlacement", "none");
                this.fontAwesomeIcon = resource.getValueMap().get("fontAwesomeIcon", String.class);
            }
        }

        public String getCtaText() {
            return ctaText;
        }

        public String getCtaDestination() {
            return UrlUtils.getNormalizedUrl(ctaDestination, resolver);
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
    }
}