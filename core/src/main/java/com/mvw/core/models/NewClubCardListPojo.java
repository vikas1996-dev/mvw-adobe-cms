package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
/**
 * Reusable POJO representing card details shared across multiple components,
 * including Advantages Cards and Testimonial components.
 */
@Getter
@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class NewClubCardListPojo {

    @ValueMapValue
    private String iconClass;

    @ValueMapValue
    private String cardTitle;

    @ValueMapValue
    private String cardDescription;

    @ValueMapValue
    private String name;

    @ValueMapValue
    private String thumbnailImage;

    @ValueMapValue
    private String disableLazyLoading;

    @ValueMapValue
    private String altText;
}
 