package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class QuoteModel {

    /** Copy Section **/
    @ValueMapValue
    private String headlineText;

    @ValueMapValue
    private String headlineType;

    @ValueMapValue
    private String shortDescription;

    @ValueMapValue
    private String copySectionAlignment;

    @ValueMapValue
    private String copySectionBgColor;

    /** Quote / Image Section **/
    @ValueMapValue
    private String image;

    @ValueMapValue
    private String bgImageAlt;

    @ValueMapValue
    private String bgImageTopMargin;

    @ValueMapValue
    private String bgImageAlignmentLeft;

    @ValueMapValue
    private String bgImageAlignmentRight;

    @ValueMapValue
    private String bgImageJustification;

    @ValueMapValue
    private String quote;

    @ValueMapValue
    private String quoteHeadlineType;

    @ValueMapValue
    private String quoteJustification;

    @ValueMapValue
    private String author;

//    /** CTA multifield (child resources) **/
//    @Inject
//    @Via("resource")
//    private List<QuoteItem> cta;

    /** Mobile Section **/

    @ValueMapValue
    private String mobileBgImageTopMargin;

    @ValueMapValue
    private String brandIconImageBgColor;

    @ValueMapValue
    private String copyAlignment;

    /* ============================
       Getters
    ============================ */

    public String getHeadlineText() {
        return headlineText;
    }

    public String getHeadlineType() {
        return headlineType;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getCopySectionAlignment() {
        return copySectionAlignment;
    }

    public String getCopySectionBgColor() {
        return copySectionBgColor;
    }

    public String getImage() {
        return image;
    }

    public String getBgImageAlt() {
        return bgImageAlt;
    }

    public String getBgImageTopMargin() {
        return bgImageTopMargin;
    }

    public String getBgImageAlignmentLeft() {
        return bgImageAlignmentLeft;
    }

    public String getBgImageAlignmentRight() {
        return bgImageAlignmentRight;
    }

    public String getBgImageJustification() {
        return bgImageJustification;
    }

    public String getQuote() {
        return quote;
    }

    public String getQuoteHeadlineType() {
        return quoteHeadlineType;
    }

    public String getQuoteJustification() {
        return quoteJustification;
    }

    public String getAuthor() {
        return author;
    }

//    public List<QuoteItem> getCta() {
//        return cta != null ? cta : Collections.emptyList();
//    }

    public String getMobileBgImageTopMargin(){ return mobileBgImageTopMargin;}

    public String getBrandIconImageBgColor() {
        return brandIconImageBgColor;
    }

    public String getCopyAlignment() {
        return copyAlignment;
    }

    @ValueMapValue
    private String headlineTag;

    public String getHeadlineTag() {
        if (headlineType != null && headlineType.matches("heading[1-6]")) {
            headlineTag = "h" + headlineType.substring(7);
            return headlineTag;
        }
        return "h2";
    }

    @ValueMapValue
    private String quoteHeadlineTag;

    public String getQuoteHeadlineTag() {
        if (quoteHeadlineType != null && quoteHeadlineType.matches("heading[1-6]")) {
            quoteHeadlineTag = "h" + quoteHeadlineType.substring(7);
            return quoteHeadlineTag;
        }
        return "h2";
    }
}
