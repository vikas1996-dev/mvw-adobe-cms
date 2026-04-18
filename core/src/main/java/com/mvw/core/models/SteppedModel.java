package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SteppedModel {

    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineType;

    @ValueMapValue
    private String primaryCopy;

    @ValueMapValue
    private String copySectionAlignment;

    @ValueMapValue
    private String copySectionBgColor;

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String fileName;

    @ValueMapValue
    private String columnsAlignment;

    @ValueMapValue
    private String stepsBgColor;

    @ValueMapValue
    private Boolean stackcolumns;

    @ValueMapValue
    private Boolean hideCircles;

    @ValueMapValue
    private Boolean showDivider;

    @ChildResource
    private List<Resource> steps;

    @ValueMapValue
    private String secondaryCopy;

    @ValueMapValue
    private String mobileCopySectionAlignmnet;

    @ValueMapValue
    private String mobileBgColor;

    @ValueMapValue
    private String columnAlignment;

    private List<StepItem> stepList;

    @PostConstruct
    protected void init() {
        stepList = new ArrayList<>();
        if (steps != null) {
            for (Resource stepResource : steps) {
                stepList.add(new StepItem(stepResource));
            }
        }
    }

    public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineType() {
        return headlineType;
    }

    public String getHeadlineTag() {
        if (headlineType != null && headlineType.matches("heading[2-3]")) {
            return "h" + headlineType.substring(7);
        }
        return "h2";
    }

    public String getPrimaryCopy() {
        return primaryCopy;
    }

    public String getCopySectionAlignment() {
        return copySectionAlignment;
    }

    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }

    public String getImage() {
        return image;
    }

    public String getFileName() {
        return fileName;
    }

    public String getColumnsAlignment() {
        return columnsAlignment;
    }

    public String getStepsBgColor() {
        return stepsBgColor;
    }

    public Boolean getStackcolumns() {
        return stackcolumns;
    }

    public Boolean getHideCircles() {
        return hideCircles;
    }

    public Boolean getShowDivider() {
        return showDivider;
    }

    public List<StepItem> getSteps() {
        return stepList != null ? stepList : Collections.emptyList();
    }

    public String getSecondaryCopy() {
        return secondaryCopy;
    }

    public String getMobileCopySectionAlignmnet() {
        return mobileCopySectionAlignmnet;
    }

    public String getMobileBgColor() {
        return mobileBgColor;
    }

    public String getColumnAlignment() {
        return columnAlignment;
    }

    @Model(
            adaptables = Resource.class,
            defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public static class StepItem {

        @ValueMapValue
        private String typeOfColumn;

        @ValueMapValue
        private String stepTitle;

        @ValueMapValue
        private String fontAwesomeIcon;

        @ValueMapValue
        private String headlineText;

        @ValueMapValue
        private String headlineType;

        @ValueMapValue
        private String shortDescription;

        public StepItem(Resource resource) {
            if (resource != null) {
                this.typeOfColumn = resource.getValueMap().get("typeOfColumn", String.class);
                this.stepTitle = resource.getValueMap().get("stepTitle", String.class);
                this.fontAwesomeIcon = resource.getValueMap().get("fontAwesomeIcon", String.class);
                this.headlineText = resource.getValueMap().get("headlineText", String.class);
                this.headlineType = resource.getValueMap().get("headlineType", String.class);
                this.shortDescription = resource.getValueMap().get("shortDescription", String.class);
            }
        }

        public String getTypeOfColumn() {
            return typeOfColumn;
        }

        public String getStepTitle() {
            return stepTitle;
        }

        public String getFontAwesomeIcon() {
            return fontAwesomeIcon;
        }

        public String getHeadlineText() {
            return headlineText;
        }

        public String getHeadlineType() {
            return headlineType;
        }

        public String getHeadlineTag() {
            if (headlineType != null && headlineType.matches("heading[3-6]")) {
                return "h" + headlineType.substring(7);
            }
            return "h3";
        }

        public String getShortDescription() {
            return shortDescription;
        }
    }
}