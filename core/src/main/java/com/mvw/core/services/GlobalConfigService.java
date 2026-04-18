package com.mvw.core.services;

public interface GlobalConfigService {

    /**
     * Returns the configuration for a specific site.
     */
    GlobalConfigs getConfigForSite(String siteId);

    /**
     * Returns Adobe Launch URL for the current config.
     */
    String getAdobeLaunchConfig();

    /**
     * Returns GTM script tag or ID.
     */
    String getGtmContainerId();

    /**
     * Returns Cookie Consent script.
     */
    String getCookieConsentScript();

    /**
     * Returns Site ID associated with this configuration.
     */
    String getSiteId();

    /**/
    String getPrivacyNotice();
}
