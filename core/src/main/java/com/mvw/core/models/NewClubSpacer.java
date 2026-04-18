package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * New Club Spacer Component
 */
@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewClubSpacer {

    @ValueMapValue
    private String desktopSpacing;

    @ValueMapValue
    private String mobileSpacing;

    private String desktopSpacingText;

    private String mobileSpacingText;

    public String getDesktopSpacingText() {
        desktopSpacingText = "--spacer-height-desktop:"+ desktopSpacing +"px;";
        return desktopSpacingText;
    }

    public String getMobileSpacingText() {
        mobileSpacingText = "--spacer-height-mobile:"+ mobileSpacing +"px;";
        return mobileSpacingText;
    }
}
 