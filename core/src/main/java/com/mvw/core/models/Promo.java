package com.mvw.core.models;


import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Promo {

    @ValueMapValue
    private String promo1BgImage;

    @ValueMapValue
    private String promo2BgImage;

    @ValueMapValue
    private String promo1BgImageAlt;

    @ValueMapValue
    private String promo2BgImageAlt;

    @ValueMapValue
    private String promo1ShortDescription;

    @ValueMapValue
    private String promo2ShortDescription;

    @ValueMapValue
    private String promo1Url;

    @ValueMapValue
    private String promo2Url;

    @ValueMapValue
    private String promo1Tab;

    @ValueMapValue
    private String promo2Tab;

    @ValueMapValue
    private String ctaStyle1;

    @ValueMapValue
    private String ctaStyle2;

    @ValueMapValue
    private String ctaAlignment1;

    @ValueMapValue
    private String ctaAlignment2;

    @ValueMapValue
    private String ctaSize1;

    @ValueMapValue
    private String ctaSize2;

    public String getPromo1BgImage() {
        return promo1BgImage;
    }

    public String getPromo2BgImage() {
        return promo2BgImage;
    }

    public String getPromo1BgImageAlt() {
        return promo1BgImageAlt;
    }

    public String getPromo2BgImageAlt() {
        return promo2BgImageAlt;
    }

    public String getPromo1ShortDescription() {
        return promo1ShortDescription;
    }

    public String getPromo2ShortDescription() {
        return promo2ShortDescription;
    }

    public String getPromo1Url() {
        return promo1Url;
    }

    public String getPromo2Url() {
        return promo2Url;
    }

    public String getPromo1Tab() {
        return promo1Tab;
    }

    public String getPromo2Tab() {
        return promo2Tab;
    }

    public String getCtaStyle1() {
        return ctaStyle1;
    }

    public String getCtaStyle2() {
        return ctaStyle2;
    }

    public String getCtaAlignment1() {
        return ctaAlignment1;
    }

    public String getCtaAlignment2() {
        return ctaAlignment2;
    }

    public String getCtaSize1() {
        return ctaSize1;
    }

    public String getCtaSize2() {
        return ctaSize2;
    }

}