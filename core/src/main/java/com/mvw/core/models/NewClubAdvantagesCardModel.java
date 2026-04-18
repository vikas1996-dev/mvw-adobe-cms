package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;
/**
 * Sling Model for the Advantages Cards component, used to expose
 authorable fields and card list data
 */
@Getter
@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class NewClubAdvantagesCardModel {

    @ValueMapValue
    private String backgroundImage;

    @ValueMapValue
    private String altText;

    @ValueMapValue
    private String disableLazyLoading;

    @ValueMapValue
    private String hideBackgroundInMobile;

    @ChildResource
    private List<NewClubCardListPojo> cardList;

}
 