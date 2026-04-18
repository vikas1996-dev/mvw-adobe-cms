package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * Sling Model for Third Party Link Modal Component
 * This model handles two separate CTAs: Left Button and Right Button
 */
@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ThirdPartyLinkModalModel {

    // Modal Content Fields
    @ValueMapValue
    private String modalTitle;

    @ValueMapValue
    private String modalDescription;

    // Left Button Fields
    @ValueMapValue
    private String leftCtaText;

    @ValueMapValue
    private Boolean leftIsMainButton;

    @ValueMapValue
    private String leftCtaStyle;

    @ValueMapValue
    private String leftCtaPlacement;

    @ValueMapValue
    private String leftCtaSize;

    @ValueMapValue
    private String leftCtaTab;
    @ValueMapValue
    private String leftCtaUrl;

    // Right Button Fields
    @ValueMapValue
    private String rightCtaText;

    @ValueMapValue
    private Boolean rightIsMainButton;

    @ValueMapValue
    private String rightCtaStyle;

    @ValueMapValue
    private String rightCtaPlacement;

    @ValueMapValue
    private String rightCtaSize;

    @ValueMapValue
    private String rightCtaTab;

    @ValueMapValue
    private String rightCtaUrl;

    @PostConstruct
    protected void init() {
        // Set default values for checkbox fields if null
        if (leftIsMainButton == null) {
            leftIsMainButton = false;
        }
        if (rightIsMainButton == null) {
            rightIsMainButton = false;
        }

        // Set default values for other fields if needed
        if (leftCtaStyle == null && leftCtaText != null) {
            leftCtaStyle = "primary";
        }
        if (rightCtaStyle == null && rightCtaText != null) {
            rightCtaStyle = "primary";
        }

        if (leftCtaSize == null && leftCtaText != null) {
            leftCtaSize = "large";
        }
        if (rightCtaSize == null && rightCtaText != null) {
            rightCtaSize = "large";
        }

        if (leftCtaTab == null && leftCtaText != null) {
            leftCtaTab = "sameWindow";
        }
        if (rightCtaTab == null && rightCtaText != null) {
            rightCtaTab = "sameWindow";
        }
    }

    // Modal Content Getters
    public String getModalTitle() {
        return modalTitle;
    }

    public String getModalDescription() {
        return modalDescription;
    }

    // Left Button Getters
    public String getLeftCtaText() {
        return leftCtaText;
    }

    public Boolean getLeftIsMainButton() {
        return leftIsMainButton;
    }

    public String getLeftCtaStyle() {
        return leftCtaStyle;
    }

    public String getLeftCtaPlacement() {
        return leftCtaPlacement;
    }

    public String getLeftCtaSize() {
        return leftCtaSize;
    }

    public String getLeftCtaTab() {
        return leftCtaTab;
    }


    // Right Button Getters
    public String getRightCtaText() {
        return rightCtaText;
    }

    public Boolean getRightIsMainButton() {
        return rightIsMainButton;
    }

    public String getRightCtaStyle() {
        return rightCtaStyle;
    }

    public String getRightCtaPlacement() {
        return rightCtaPlacement;
    }

    public String getRightCtaSize() {
        return rightCtaSize;
    }

    public String getRightCtaTab() {
        return rightCtaTab;
    }

    // Utility methods for HTL
    public boolean hasLeftButton() {
        return leftCtaText != null && !leftCtaText.trim().isEmpty();
    }

    public boolean hasRightButton() {
        return rightCtaText != null && !rightCtaText.trim().isEmpty();
    }

    public boolean hasAnyButton() {
        return hasLeftButton() || hasRightButton();
    }


}