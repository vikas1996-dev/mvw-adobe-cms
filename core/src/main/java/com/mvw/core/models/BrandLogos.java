package com.mvw.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BrandLogos {
   
   @Inject
    private List<LogoItem> logos;
 
 
    public List<LogoItem> getLogos() {
    return logos;
   }
   
}

