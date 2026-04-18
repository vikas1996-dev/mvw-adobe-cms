package com.mvw.core.services.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.mvw.core.services.GlobalConfigService;
import com.mvw.core.services.GlobalConfigs;

/**
 * Implementation of the GlobalConfigService interface.
 *
 * This OSGi component manages site-specific configurations such as:
 * - Adobe Launch script URLs
 * - Google Tag Manager container IDs
 * - Cookie Consent scripts
 *
 * The service supports multiple site configurations using a factory OSGi configuration.
 * Each configuration instance is stored in a shared map keyed by siteId.
 */
@Component(service = GlobalConfigService.class, immediate = true)
@Designate(ocd = GlobalConfigs.class, factory = true)
public class GlobalConfigsServiceImpl implements GlobalConfigService {

    /**
     * Thread-safe map that holds all site-specific configurations.
     * Key   → site_id (e.g., "newclub", "tmvc", "legal")
     * Value → GlobalConfigs object for that site
     *
     * Using ConcurrentHashMap ensures that multiple OSGi threads can
     * safely update or read this configuration data.
     */
    private static final Map<String, GlobalConfigs> CONFIG_MAP = new ConcurrentHashMap<>();

    /**
     * Holds the current configuration instance.
     * Each OSGi factory configuration creates a new instance of this service.
     */
    private GlobalConfigs config;

    /**
     * Called when the OSGi configuration is activated or modified.
     *
     * @param config The configuration instance automatically injected by OSGi.
     *
     * The factory setting (`factory = true`) in @Designate allows multiple
     * configurations (e.g. GlobalConfigs~newclub, GlobalConfigs~tmvc, GlobalConfigs~legal).
     *
     * Each configuration activation creates a new service instance,
     * and we store it in CONFIG_MAP for later retrieval.
     */
    @Activate
    @Modified
    protected void activate(GlobalConfigs config) {
        this.config = config;
        CONFIG_MAP.put(config.site_id(), config);
    }

    /**
     * Returns the configuration object for a given site ID.
     *
     * @param siteId The unique identifier for the site.
     * @return The GlobalConfigs instance or null if not found.
     */
    public GlobalConfigs getConfigForSite(String siteId) {
        return CONFIG_MAP.get(siteId);
    }

    /**
     * Returns the Adobe Launch script URL for the current configuration.
     */
    @Override
    public String getAdobeLaunchConfig() {
        return config.adobe_launch_url();
    }

    /**
     * Returns the Google Tag Manager script snippet for the current configuration.
     * The script includes the GTM container ID configured for the site.
     */
    @Override
    public String getGtmContainerId() {
        return "<script async src=\"https://www.googletagmanager.com/gtm.js?id=" 
                + config.gtm_container_id() + "\"></script>";
    }

    /**
     * Returns the cookie consent script configured for the current site.
     */
    @Override
    public String getCookieConsentScript() {
        return config.cookie_consent_script();
    }

    /**
     * Returns the site ID associated with this configuration.
     */
    @Override
    public String getSiteId() {
        return config.site_id();
    }

    /**/

    @Override
    public String getPrivacyNotice() {
        return config.privacy_notice();
    }
}