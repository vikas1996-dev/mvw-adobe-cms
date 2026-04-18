package com.mvw.core.models;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Getter
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewClubVideo {

    @ValueMapValue
    private String videoUrl;

    @ValueMapValue
    private String videoAutoplay;

    @ValueMapValue
    private String videoControls;

    private String videoId;

    public String getVideoId() {
      videoId = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(videoUrl, "/"), ".");
      return videoId;
    }

}
