package com.mvw.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvw.core.utils.UrlUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(
    adaptables = { Resource.class, SlingHttpServletRequest.class },
    adapters = { CTAItem.class },
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CTAItem {

    private static final Logger logger = LoggerFactory.getLogger(CTAItem.class);

    @SlingObject
    ResourceResolver resolver;

    @ValueMapValue
    private String ctaText;

    @ValueMapValue
    private String ctaUrl;

    @ValueMapValue
    private String ctaType;

    @ValueMapValue
    private String videoUrl;

    @ValueMapValue
    private String phoneNumber;

    @ValueMapValue
    private String ctaTextUnder;

    @ValueMapValue
    private String videoID; // will be overridden by init()

    @ValueMapValue
    private String ctaStyle;

    @ValueMapValue
    private String ctaPlacement;

    @ValueMapValue
    private String ctaSize;

    @ValueMapValue
    private String ctaOpensIn;

    @ValueMapValue
    private String ctaAdditionalText;

    @ValueMapValue
    private String fontAwesomeIcon;

    @ValueMapValue
    private String thirdPartyLink;

    @ValueMapValue
    private String appendPhoneNumber;

    @JsonProperty
    public String getAppendPhoneNumber() {
        return appendPhoneNumber;
    }

    // --------- Derived values ----------
    private String extractedVideoID;

    @PostConstruct
    protected void init() {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            extractedVideoID = extractVideoIDFromPath(videoUrl);

            // If author filled "videoID" manually, keep it.
            // If not, use derived one.
            if (videoID == null || videoID.isEmpty()) {
                videoID = extractedVideoID;
                logger.info("videoID {}", videoID);
            }
        }
    }

    @JsonProperty
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

    // Getters

    @JsonProperty
    public String getCtaText() {
        String updatedPhone = (request != null) ? (String) request.getAttribute("phoneNumber") : null;
        if (ctaText != null && updatedPhone != null && ctaText.contains("{{phoneNumber}}")) {
            return ctaText.replace("{{phoneNumber}}", updatedPhone);
        }
        return ctaText;
    }

    @JsonProperty
    public String getCtaUrl() {
        return UrlUtils.getNormalizedUrl(ctaUrl, resolver);
    }

    @JsonProperty
    public String getCtaType() {
        return ctaType;
    }

    @JsonProperty
    public String getVideoUrl() {
        return videoUrl;
    }

    @Self
    private SlingHttpServletRequest request;

    @JsonProperty
    public String getPhoneNumber() {
        if (request != null && request.getAttribute("phoneNumber") != null) {
            return (String) request.getAttribute("phoneNumber");
        }
        return phoneNumber;
    }

    @JsonProperty
    public String getCtaTextUnder() {
        return ctaTextUnder;
    }

    @JsonProperty
    public String getVideoID() {
        return videoID;
    }

    @JsonProperty

    public String getCtaStyle() {
        return ctaStyle;
    }

    @JsonProperty
    public String getCtaPlacement() {
        return ctaPlacement;
    }

    @JsonProperty
    public String getCtaSize() {
        return ctaSize;
    }

    @JsonProperty
    public String getCtaOpensIn() {
        return ctaOpensIn;
    }

    @JsonProperty
    public String getCtaAdditionalText() {
        return ctaAdditionalText;
    }

    @JsonProperty
    public String getFontAwesomeIcon() {
        return fontAwesomeIcon;
    }

    @JsonProperty
    public String getThirdPartyLink() {
        return thirdPartyLink;
    }

    @JsonProperty
    // The derived one, if you want it directly
    public String getExtractedVideoID() {
        return extractedVideoID;
    }

    

}
