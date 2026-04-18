package com.mvw.core.models;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;
/**
 * Sling Model for the Testimonial Cards component, responsible for
 * exposing a list of user testimonials including quotes, contributor
 * details, and optional photos.
 */
@Getter
@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewclubTestimonialsModel {
    @ChildResource
    private List<NewClubCardListPojo> cardList;

}
 