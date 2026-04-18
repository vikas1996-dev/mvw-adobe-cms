package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MVCLogo {
  @ValueMapValue
  private String title;

  @ValueMapValue

  private String copySectionBorderControl;

  @ValueMapValue

  private String copySectionBgColorTransparency;

  
  public String getTitle() {
    return title;
  }

  public String getCopySectionBorderControl() {
    return copySectionBorderControl;
  }

  public String getCopySectionBgColorTransparency() {
    return copySectionBgColorTransparency;
  }

}
