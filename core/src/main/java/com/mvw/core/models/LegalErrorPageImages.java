package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import lombok.Getter;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LegalErrorPageImages {

    @ValueMapValue(name = "mainImage/fileReference")
    private String mainImageFileReference;

    @ValueMapValue(name = "mainImageAltText")
    private String mainImageAltText;

    @ValueMapValue(name = "overlay/fileReference")
    private String overlayImagePath;

    @ValueMapValue(name = "mboverlay/fileReference")
    private String overlayImageAltText;

}