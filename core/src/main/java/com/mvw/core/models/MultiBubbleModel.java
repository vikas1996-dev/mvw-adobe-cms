package com.mvw.core.models;
 
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
 
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MultiBubbleModel {
 
    @ValueMapValue
    private String bgColor;
 
    @ValueMapValue
    private String bgImage;
 
    @ValueMapValue
    private String mobileImage;
 
    @ValueMapValue
    private Integer bgImageTransparency;
 
    @ValueMapValue
    private String headlineText;
 
    @ValueMapValue
    private String headlineSize;
 
    @ValueMapValue
    private String headlineAlignment;
 
    @ValueMapValue
    private String description;
 
    @ValueMapValue
    private String descriptionAlignment;
 
    @ValueMapValue
    private String image1;
 
    @ValueMapValue
    private String altText1;
 
    @ValueMapValue
    private String image2;
 
    @ValueMapValue
    private String altText2;
 
    @ValueMapValue
    private String image3;
 
    @ValueMapValue
    private String altText3;
 
    @ValueMapValue
    private String image4;
 
    @ValueMapValue
    private String altText4;
 
    @ValueMapValue
    private String image5;
 
    @ValueMapValue
    private String altText5;
 
    @ValueMapValue
    private String image6;
 
    @ValueMapValue
    private String altText6;
 
    @ValueMapValue
    private String image7;
 
    @ValueMapValue
    private String altText7;
 
    @ValueMapValue
    private String mobileHeadlineAlignment;
 
    @ValueMapValue
    private String mobileDescriptionAlignment;
 
    public String getBgColor() {
        return bgColor;
    }
 
    public String getBgImage() {
        return bgImage;
    }
 
    public String getMobileImage() {
        if (mobileImage != null) {
            return mobileImage.replace(" ", "%20");
        }
        return mobileImage;
    }
 
    public Integer getBgImageTransparency() {
        return bgImageTransparency;
    }
 
    public String getHeadlineText() {
        return headlineText;
    }
 
    public String getHeadlineSize() {
        return headlineSize;
    }
 
    public String getHeadlineAlignment() {
        return headlineAlignment;
    }
 
    public String getDescription() {
        return description;
    }
 
    public String getDescriptionAlignment() {
        return descriptionAlignment;
    }
 
    public String getImage1() {
        return image1;
    }
 
    public String getAltText1() {
        return altText1;
    }
 
    public String getImage2() {
        return image2;
    }
 
    public String getAltText2() {
        return altText2;
    }
 
    public String getImage3() {
        return image3;
    }
 
    public String getAltText3() {
        return altText3;
    }
 
    public String getImage4() {
        return image4;
    }
 
    public String getAltText4() {
        return altText4;
    }
 
    public String getImage5() {
        return image5;
    }
 
    public String getAltText5() {
        return altText5;
    }
 
    public String getImage6() {
        return image6;
    }
 
    public String getAltText6() {
        return altText6;
    }
 
    public String getImage7() {
        return image7;
    }
 
    public String getAltText7() {
        return altText7;
    }
 
    public String getMobileHeadlineAlignment() {
        return mobileHeadlineAlignment;
    }
 
    public String getMobileDescriptionAlignment() {
        return mobileDescriptionAlignment;
    }
 
    public String getHeadlineTag() {
        if (headlineSize != null && headlineSize.matches("heading[2-6]")) {
            return headlineSize.replace("heading", "h");
        }
        return "h2";
    }
}