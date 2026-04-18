package com.mvw.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class LoginItem {
     @ValueMapValue
    private String loginHeaderText;

    @ValueMapValue
    private String loginText;

     @ValueMapValue
    private String loginIcon;

    @ValueMapValue
    private String loginAlt;

     @ValueMapValue
    private String loginHeadline;

     public String getLoginHeaderText() {
         return loginHeaderText;
     }

     public String getLoginText() {
         return loginText;
     }

     public String getLoginIcon() {
         return loginIcon;
     }

     public String getLoginAlt() {
         return loginAlt;
     }

     public String getLoginHeadline() {
         return loginHeadline;
     }

    
   
}
