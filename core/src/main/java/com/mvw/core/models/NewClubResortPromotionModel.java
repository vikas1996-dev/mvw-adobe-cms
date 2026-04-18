package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;
/**
 * Sling Model For Resort Promotion Component
 * */
@Getter
@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewClubResortPromotionModel {

    @ValueMapValue
    private String countryName;

    @ValueMapValue
    private String resortImage;

    @ValueMapValue
    private String altText;

    @ValueMapValue
    private String imageCaption;

    @ValueMapValue
    private String disableLazyLoading;

    @ChildResource
    private List<NavigationLinkPojo> resortListFirstColumn;

    @ChildResource
    private List<NavigationLinkPojo> resortListSecondColumn;

}
 