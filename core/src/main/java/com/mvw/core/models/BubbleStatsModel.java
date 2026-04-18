package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = Resource.class, adapters = { BubbleStatsModel.class,
        ComponentExporter.class }, resourceType = "mvw/components/tmvc/components/bubblestats", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BubbleStatsModel  implements ComponentExporter {

    @ValueMapValue
    private String style;

    @ValueMapValue
    private String bgColor;

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
    private String copyWithinCircle1;

    @ValueMapValue
    private String copyUnderCircle1;

    @ValueMapValue
    private String copyWithinCircle2;

    @ValueMapValue
    private String copyUnderCircle2;

    @ValueMapValue
    private String copyWithinCircle3;

    @ValueMapValue
    private String copyUnderCircle3;

    @ValueMapValue
    private String copyWithinCircle4;

    @ValueMapValue
    private String copyUnderCircle4;

    @ValueMapValue
    private String copyWithinCircle5;

    @ValueMapValue
    private String copyUnderCircle5;

    @ValueMapValue
    private String mobileHeadlineAlignment;

    @ValueMapValue
    private String mobileDescriptionAlignment;

    @ValueMapValue
    private String headlineTag;

    @ValueMapValue
    private String secondaryCopyText;

    @JsonProperty
    public String getSecondaryCopyText() {
        return secondaryCopyText;
    }

    @JsonProperty
    public String getStyle() {
        return style;
    }

    @JsonProperty
    public String getBgColor() {
        return bgColor;
    }

    @JsonProperty
    public String getHeadlineText() {
        return headlineText;
    }

    @JsonProperty
    public String getHeadlineSize() {
        return headlineSize;
    }

    @JsonProperty
    public String getHeadlineAlignment() {
        return headlineAlignment;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public String getDescriptionAlignment() {
        return descriptionAlignment;
    }

    @JsonProperty
    public String getCopyWithinCircle1() {
        return copyWithinCircle1;
    }

    @JsonProperty
    public String getCopyUnderCircle1() {
        return copyUnderCircle1;
    }

    @JsonProperty
    public String getCopyWithinCircle2() {
        return copyWithinCircle2;
    }

    @JsonProperty
    public String getCopyUnderCircle2() {
        return copyUnderCircle2;
    }

    @JsonProperty
    public String getCopyWithinCircle3() {
        return copyWithinCircle3;
    }

    @JsonProperty
    public String getCopyUnderCircle3() {
        return copyUnderCircle3;
    }

    @JsonProperty
    public String getCopyWithinCircle4() {
        return copyWithinCircle4;
    }

    @JsonProperty
    public String getCopyUnderCircle4() {
        return copyUnderCircle4;
    }

    @JsonProperty
    public String getCopyWithinCircle5() {
        return copyWithinCircle5;
    }

    @JsonProperty
    public String getCopyUnderCircle5() {
        return copyUnderCircle5;
    }

    @JsonProperty
    public String getMobileHeadlineAlignment() {
        return mobileHeadlineAlignment;
    }

    @JsonProperty
    public String getMobileDescriptionAlignment() {
        return mobileDescriptionAlignment;
    }

    public String getHeadlineTag() {
        if (headlineSize != null && headlineSize.matches("heading[2-6]")) {
            headlineTag = "h" + headlineSize.substring(7);
            return headlineTag;
        }
        return "h2";
    }

    @Override
    public String getExportedType() {
        return "mvw/components/tmvc/components/bubblestats"; // same as resourceType
    }

}