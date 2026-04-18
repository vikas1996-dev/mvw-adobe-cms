package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

@Getter
@Model(adaptables = {Resource.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortsCardModel {

    @ValueMapValue
    private String resortCardHeading;

    @ValueMapValue
    private String resortCardDescription;

    @ChildResource
    private List<NewClubResortCardsItemModal>  cardsItem;

}
