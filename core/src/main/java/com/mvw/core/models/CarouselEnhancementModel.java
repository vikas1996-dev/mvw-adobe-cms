package com.mvw.core.models;

import com.adobe.cq.wcm.core.components.models.Carousel;

import org.apache.sling.api.SlingHttpServletRequest;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Model(

        adaptables = SlingHttpServletRequest.class, adapters = Carousel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL

)

public class CarouselEnhancementModel implements Carousel {
private static final Logger logger = LoggerFactory.getLogger(CarouselEnhancementModel.class);

    @ValueMapValue
    private String style;

    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineType;


    @ValueMapValue
    private String copySectionAlignment;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private String copySectionBgColor;
    @ValueMapValue
    private String carouselBgColor;

    @ChildResource
    private List<CarouselSlidesPojo> carouselSlide;

    @ValueMapValue
    private String secondaryCopyText;

    @ValueMapValue
    private String brandIconImageBgcolor;

    @ValueMapValue
    private String copyAlignment;

    @ValueMapValue
    private String secondaryCopyAlignment;

    public String getStyle() {
        return style;
    }

    public String getHeadlineText() {
        return headlineText;
    }

    /**
     * True only when headline has real content (not null, not empty, not only spaces).
     * Use in the template so the headline block (and its underline) is not shown when the author enters only a space.
     */
    public boolean isHeadlineTextPresent() {
        return headlineText != null && !headlineText.trim().isEmpty();
    }

    public String getHeadlineType() {
        return headlineType;
    }

    public String getCopySectionAlignment() {
        return copySectionAlignment;
    }

    public String getCopyText() {
        return copyText;
    }

    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }

    public String getCarouselBgColor() {
        return carouselBgColor;
    }

    public List<CarouselSlidesPojo> getCarouselSlide() {
        return carouselSlide;
    }

    public String getSecondaryCopyText() {
        return secondaryCopyText;
    }

    public String getBrandIconImageBgcolor() {
        return brandIconImageBgcolor;
    }

    public String getCopyAlignment() {
        return copyAlignment;
    }

    public String getSecondaryCopyAlignment() {
        return secondaryCopyAlignment;
    }

        @ValueMapValue
    private String headlineTag;

    public String getHeadlineTag() {
         logger.info("headlineType {}", headlineType);
    if (headlineType != null && headlineType.matches("heading[1-6]")) {
        // convert heading1 → h1, heading2 → h2, etc.
        headlineTag = "h" + headlineType.substring(7); 
        return headlineTag;
    }
     logger.info("headlineTag {}", headlineTag);
    return "h2"; // default fallback
}

}
