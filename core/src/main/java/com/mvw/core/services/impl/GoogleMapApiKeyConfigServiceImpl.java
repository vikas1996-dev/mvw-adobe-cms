package com.mvw.core.services.impl;

import com.mvw.core.config.GoogleMapApiConfig;
import lombok.Getter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Component(service = GoogleMapApiKeyConfigServiceImpl.class, immediate = true)
@Designate(ocd = GoogleMapApiConfig.class)
public class GoogleMapApiKeyConfigServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(GoogleMapApiKeyConfigServiceImpl.class);

    private String googleMapApiKey;

    @Activate
    protected void activate(GoogleMapApiConfig config) {
        log.info("Activate method called from class: {}", this.getClass().getName());
        this.googleMapApiKey = config.googleMapApiKey();
    }

}
