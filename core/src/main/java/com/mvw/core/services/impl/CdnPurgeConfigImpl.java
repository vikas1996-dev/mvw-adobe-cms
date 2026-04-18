package com.mvw.core.services.impl;

import com.mvw.core.config.CdnPurgeConfig;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Component(service = CdnPurgeConfigImpl.class, immediate = true)
@Designate(ocd = CdnPurgeConfig.class)
public class CdnPurgeConfigImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdnPurgeConfigImpl.class);

    private String cdnPurgeKey1;
    private String cdnPurgeKey2;
    private String cdnPurgeDomain;

    /**
     * Activate method reads OSGi config
     */
    @Activate
    protected void activate(CdnPurgeConfig config) {

        this.cdnPurgeKey1 = StringUtils.trimToEmpty(config.cdnPurgeKey1());
        this.cdnPurgeKey2 = StringUtils.trimToEmpty(config.cdnPurgeKey2());
        this.cdnPurgeDomain = StringUtils.trimToEmpty(config.cdnPurgeDomain());

        LOGGER.info("CDN Purge Configuration Loaded");
        LOGGER.info("CDN Purge Key1: {}", cdnPurgeKey1);
        LOGGER.info("CDN Purge Key2: {}", cdnPurgeKey2);
        LOGGER.info("CDN Purge Domain: {}", cdnPurgeDomain);
    }
}