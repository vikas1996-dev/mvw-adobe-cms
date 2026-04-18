package com.mvw.core.services.impl;

import com.mvw.core.config.JahiaApiConfig;
import lombok.Getter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Component(service = JahiaApiConfigServiceImpl.class, immediate = true)
@Designate(ocd = JahiaApiConfig.class)
public class JahiaApiConfigServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(JahiaApiConfigServiceImpl.class);

    private String apiEndPoint;

    private String apiAuthToken;

    private String apiImagePath;

    @Activate
    protected void activate(JahiaApiConfig config) {
        log.info("Activate method called from class: {}", this.getClass().getName());
        this.apiEndPoint = config.endpoint();
        this.apiAuthToken = config.authToken();
        this.apiImagePath = config.imagePath();
    }

}
