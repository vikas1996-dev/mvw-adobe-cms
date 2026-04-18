package com.mvw.core.services.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ImageBasePathServiceImpl.class)
public class ImageBasePathServiceImpl {
    @Reference
    private JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    public String getBasePath(){
        return jahiaApiConfigServiceImpl.getApiImagePath();
    }

}
