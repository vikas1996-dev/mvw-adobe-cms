package com.mvw.core.services.impl;

import com.mvw.core.config.TripAdvisorConfig;
import lombok.Getter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Component(service = TripAdvisorConfigServiceImpl.class, immediate = true)
@Designate(ocd = TripAdvisorConfig.class)
public class TripAdvisorConfigServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(TripAdvisorConfigServiceImpl.class);

    private String apiUrl;

    private String apiKey;

    @Activate
    protected void activate(TripAdvisorConfig config) {
        log.info("Activate method called from class: {}", this.getClass().getName());
        this.apiUrl = config.apiUrl();
        this.apiKey = config.apiKey();
    }

}
