package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class OBLModel {

    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineSize;

    @ValueMapValue
    private String headlineAlignment;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String descriptionAlignment;

    @ValueMapValue
    private String mobileHeadlineAlignment;

    @ValueMapValue
    private String mobileDescriptionAlignment;

    @ChildResource
    private List<Resource> offerBadgeList;


    public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineAlignment() {
        return (headlineAlignment != null && !headlineAlignment.trim().isEmpty())
                ? headlineAlignment : "center";
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionAlignment() {
        return (descriptionAlignment != null && !descriptionAlignment.trim().isEmpty())
                ? descriptionAlignment : "center";
    }

    public String getMobileHeadlineAlignment() {
        return (mobileHeadlineAlignment != null && !mobileHeadlineAlignment.trim().isEmpty())
                ? mobileHeadlineAlignment : "center";
    }

    public String getMobileDescriptionAlignment() {
        return (mobileDescriptionAlignment != null && !mobileDescriptionAlignment.trim().isEmpty())
                ? mobileDescriptionAlignment : "center";
    }

    public String getHeadlineClass() {
        return resolveHeadingTag(headlineSize, "heading2");
    }


    public String getHeadlineTag() {
        String headingClass = getHeadlineClass();
        if (headingClass != null && headingClass.matches("heading[2-4]")) {
            return "h" + headingClass.substring(7);
        }
        return "h2";
    }

    private String resolveHeadingTag(String size, String defaultTag) {
        if (size != null && size.matches("heading[2-4]")) {
            return size;
        }
        return defaultTag;
    }

    public List<OfferBadge> getOfferBadgeList() {
        if (offerBadgeList == null || offerBadgeList.isEmpty()) {
            return Collections.emptyList();
        }

        List<OfferBadge> badges = new ArrayList<>();
        for (Resource resource : offerBadgeList) {
            OfferBadge badge = resource.adaptTo(OfferBadge.class);
            if (badge != null) {
                badges.add(badge);
            }
        }
        return badges;
    }

    @Model(
            adaptables = Resource.class,
            defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public static class OfferBadge {

        @ValueMapValue
        private String oblColor;

        @ValueMapValue
        private String oblLabel;

        @ValueMapValue
        private String oblPointsRange;

        public String getOblColor() {
            return oblColor;
        }

        public String getOblLabel() {
            return oblLabel;
        }

        public String getOblPointsRange() {
            return oblPointsRange;
        }
    }
}