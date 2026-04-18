package com.mvw.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import lombok.Getter;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewClubHeroBanner {

        @ValueMapValue(name = "bannerImage/fileReference")
        private String bannerImageFileReference;

        @ValueMapValue
        private String bannerImageAltText;

        @ValueMapValue(name = "mbBannerImage/fileReference")
        private String mbBannerImageFileReference;

        @ValueMapValue
        private String disableLazyLoading;

        @ValueMapValue
        private String fetchPriority;

        @ValueMapValue
        private String bgType;

        @ValueMapValue
        private String overlayTitle;

        @ValueMapValue
        private String overlayDescription;

        @ValueMapValue(name = "overlay/fileReference")
        private String overlayImagePath;

        @ValueMapValue(name = "overlay/overlayImageAltText")
        private String overlayImageAltText;

        @ValueMapValue(name = "mboverlay/fileReference")
        private String mobileOverlayImagePath;

        @ValueMapValue
        private String videoUrl;

        private String videoId;

        public String getVideoId() {
                videoId = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(videoUrl, "/"), ".");
                return videoId;
        }

}
