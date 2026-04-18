package com.mvw.core.models;

import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContactUsItem {
     @ValueMapValue
    private String contactUsCopy;

    @ValueMapValue
    private String contactUsUrl;

     @ValueMapValue
    private String contactUsTab;

    @ValueMapValue
    private String contactCtaStyle;

     @ValueMapValue
    private String contactCtaAlignment;

    @ValueMapValue
    private String contactCtaSize;

     @ValueMapValue
    private String contactUsIcon;

     public String getContactUsCopy() {
         return contactUsCopy;
     }

     public String getContactUsUrl() {
         return contactUsUrl;
     }

     public String getContactUsTab() {
         return contactUsTab;
     }

     public String getContactCtaStyle() {
         return contactCtaStyle;
     }

     public String getContactCtaAlignment() {
         return contactCtaAlignment;
     }

     public String getContactCtaSize() {
         return contactCtaSize;
     }

     public String getContactUsIcon() {
         return contactUsIcon;
     }

   
}
