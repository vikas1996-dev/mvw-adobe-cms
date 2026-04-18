package com.mvw.core.services.impl;

import lombok.Getter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mvw.core.config.MuleApiConfig;


@Getter
@Component(service = MuleConfigServiceImpl.class, immediate = true)
@Designate(ocd = MuleApiConfig.class)
public class MuleConfigServiceImpl {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MuleConfigServiceImpl.class);

    private String apiUrl;
    private String jwtToken;
    private String caseUrl;
    private String leadUrl;
   
   
    /**
     * Activate method reads OSGi config
     */
    @Activate
    protected void activate(MuleApiConfig config) {

        this.apiUrl = config.apiUrl();
        this.jwtToken = config.jwtToken();
        this.caseUrl = config.caseUrl();
        this.leadUrl=config.leadUrl();

        LOGGER.info("Mule API Configuration Loaded");
        LOGGER.info("Country API URL: {}", apiUrl);
        LOGGER.info("Case API URL: {}", caseUrl);
        LOGGER.info("Case API URL: {}", leadUrl);
    }
}