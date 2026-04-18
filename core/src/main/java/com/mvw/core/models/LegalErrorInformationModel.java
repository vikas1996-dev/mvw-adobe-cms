package com.mvw.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import lombok.Getter;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LegalErrorInformationModel {
    @ValueMapValue
    private String twoColumns;

    @ValueMapValue(name = "mainImage/fileReference")
    private String mainImageFileReference;

    @ValueMapValue(name = "mainImageAltText")
    private String mainImageAltText;

    @ValueMapValue(name = "overlay/fileReference")
    private String overlayImagePath;

    @ValueMapValue(name = "mboverlay/fileReference")
    private String overlayImageAltText;

    @ChildResource
    private List<SupportDetailsPojo> cardList;

    @ValueMapValue
    private String copyText;
}