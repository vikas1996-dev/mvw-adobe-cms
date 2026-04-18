package com.mvw.core.models;


import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.List;
 
@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class HeroBannerModel {
 
    @ValueMapValue
    private String bgType;
 
    @ValueMapValue
    private String bgVideo;
   

    @ValueMapValue
    private String  bgVideoID;
 
    @ValueMapValue

    private String bgImage;
 
    @ValueMapValue

    private String bgImageAlignmentLeftRight;
 
    @ValueMapValue

    private String bgImageAlignmentTopBottom;
 
    @ValueMapValue
    private String taglineText;

     @ValueMapValue
    private String taglineColor;

     @ValueMapValue

    private String headlineText;
 
    @ValueMapValue

    private String headlineAlignment;
 
    @ValueMapValue
    private String copySectionAlignment;

      @ValueMapValue
    private String copySectionBgColor;
 
    @ValueMapValue
    private String copySectionBorderControl;
 
    @ValueMapValue
    private String copySectionBgColorTransparency;

     @ValueMapValue

    private String ctaAlignmentControl;
 
   
    @ValueMapValue

    private String mobileBackgroundImage;
 
    @ValueMapValue

    private String mobileBgImageAlignmentLeftRight;
 
    @ValueMapValue

    private String mobileBgImageAlignmentTopBottom;
 
    @ValueMapValue

    private String mobileHeadlineAlignment;
 
    @ValueMapValue

    private String mobileTertiaryLinkAlignment;
 
    @ValueMapValue

    private String mobileBgColorAlignment;
 
    @ValueMapValue

    private String mobileBgColorBorderControl;
 
   @Inject
    private List<LogoItem> logos;

   public String getBgType() {
     return bgType;
   }

   public String getBgVideo() {
     return bgVideo;
   }

public String getBgVideoID() {
     return bgVideoID;
   }

   

   public String getBgImage() {
     return bgImage;
   }

   public String getBgImageAlignmentLeftRight() {
     return bgImageAlignmentLeftRight;
   }

   public String getBgImageAlignmentTopBottom() {
     return bgImageAlignmentTopBottom;
   }

   public String getTaglineText() {
     return taglineText;
   }

   public String getTaglineColor() {
     return taglineColor;
   }

   public String getHeadlineText() {
     return headlineText;
   }

   public String getHeadlineAlignment() {
     return headlineAlignment;
   }

   public String getCopySectionAlignment() {
     return copySectionAlignment;
   }

   public String getCopySectionBgColor() {
     return copySectionBgColor;
   }

   public String getCopySectionBorderControl() {
     return copySectionBorderControl;
   }

   public String getCopySectionBgColorTransparency() {
     return copySectionBgColorTransparency;
   }

   public String getCtaAlignmentControl() {
     return ctaAlignmentControl;
   }

   public String getMobileBackgroundImage() {
     return mobileBackgroundImage;
   }

   public String getMobileBgImageAlignmentLeftRight() {
     return mobileBgImageAlignmentLeftRight;
   }

   public String getMobileBgImageAlignmentTopBottom() {
     return mobileBgImageAlignmentTopBottom;
   }

   public String getMobileHeadlineAlignment() {
     return mobileHeadlineAlignment;
   }

   public String getMobileTertiaryLinkAlignment() {
     return mobileTertiaryLinkAlignment;
   }

   public String getMobileBgColorAlignment() {
     return mobileBgColorAlignment;
   }

   public String getMobileBgColorBorderControl() {
     return mobileBgColorBorderControl;
   }

   public List<LogoItem> getLogos() {
     return logos;
   }

   private String extractedVideoID;

    @PostConstruct
    protected void init() {
        if (bgVideo != null && !bgVideo.isEmpty()) {
            extractedVideoID = extractVideoIDFromPath(bgVideo);

            // If author filled "videoID" manually, keep it.
            // If not, use derived one.
            if (bgVideoID == null || bgVideoID.isEmpty()) {
                bgVideoID = extractedVideoID;
            }
        }
    }

    private String extractVideoIDFromPath(String path) {

        // Get last part after the last "/"
        String filename = path.substring(path.lastIndexOf('/') + 1);

        // Remove extension (.mp4, .mov, .mkv, etc.)
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            filename = filename.substring(0, dotIndex);
        }

        return filename;
    }
  
 
}


