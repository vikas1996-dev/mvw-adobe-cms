package com.mvw.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(
        adaptables = Resource.class,
        adapters = {BrandFamily.class, ComponentExporter.class},
        resourceType = "mvw/components/tmvc/components/brandfamily",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
        name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandFamily implements ComponentExporter {

    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineType;

    @ValueMapValue
    private String shortDescription;

    @ValueMapValue
    private String copySectionAlignment;

    @ValueMapValue
    private String copySectionBgColor;

    @ValueMapValue
    private String secondaryHeadline;

    @ValueMapValue
    private String secondaryDescription;

    @ValueMapValue
    private String brandIconImageBgColor;

    @ValueMapValue
    private String copyAlignment;

    @Inject
    private List<LogoItem> logos;

    @ValueMapValue
    private String headlineTag;

    // ---------------- GETTERS ----------------

    @JsonProperty
    public List<LogoItem> getLogos() {
        return logos;
    }

    @JsonProperty
    public String getHeadlineText() {
        return headlineText;
    }

    @JsonProperty
    public String getShortDescription() {
        return shortDescription;
    }

    @JsonProperty
    public String getCopySectionAlignment() {
        return copySectionAlignment;
    }

    @JsonProperty
    public String getSecondaryHeadline() {
        return secondaryHeadline;
    }

    @JsonProperty
    public String getBrandIconImageBgColor() {
        return brandIconImageBgColor;
    }

    @JsonProperty
    public String getCopyAlignment() {
        return copyAlignment;
    }

    @JsonProperty
    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }

    @JsonProperty
    public String getSecondaryDescription() {
        return secondaryDescription;
    }

    @JsonProperty
    public String getHeadlineType() {
        return headlineType;
    }

    @JsonProperty
    public String getHeadlineTag() {
        if (headlineType != null && headlineType.matches("heading[1-6]")) {
            return "h" + headlineType.substring(7);
        }
        return "h2";
    }

    // ---------------- REQUIRED FOR EXPORTER ----------------

    @Override
    public String getExportedType() {
        return "mvw/components/tmvc/components/brandfamily"; // same as resourceType
    }
}