package com.mvw.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import com.adobe.acs.commons.models.injectors.annotation.HierarchicalPageProperty;
import com.mvw.core.services.GlobalConfigService;
import com.mvw.core.services.GlobalConfigs;

/**
 * Sling Model to fetch and expose site-specific global configurations
 * such as Adobe Launch, GTM, and Cookie Consent scripts.
 *
 * This model adapts from a Resource and uses the GlobalConfigService
 * to fetch the correct OSGi configuration based on the current site ID.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GlobalConfigModel {

    // Logger for debugging and tracing
    private static final Logger LOG = LoggerFactory.getLogger(GlobalConfigModel.class);

    /**
     * Injects the current resource (the component or page being rendered).
     * We use this to derive the site ID from the path if it's not explicitly set.
     */
    @Inject
    private Resource resource;

    /**
     * Injects our custom OSGi service (GlobalConfigService).
     * This service is responsible for providing site-specific configuration data.
     */
    @OSGiService
    private GlobalConfigService globalConfigService;

    /**
     * Attempts to read the "site_id" property from the page or its ancestors
     * using ACS Commons' @HierarchicalPageProperty annotation.
     * 
     * This allows inheritance of the site ID property across page hierarchy.
     */
    @HierarchicalPageProperty(value = "site_id")
    String site_id;

    /**
     * Holds the configuration object for the current site, once loaded.
     */
    private GlobalConfigs currentConfig;

    /**
     * Lifecycle method that runs after all injections are complete.
     * Responsible for determining the site ID and fetching its corresponding
     * config.
     */
    @PostConstruct
    protected void init() {
        try {
            // Fallback: If site_id is not defined on the page, derive from the resource
            // path.
            // Example: /content/projecta/en/home → "projecta"
            LOG.info("Initializing GlobalConfigModel:" + globalConfigService.toString());
            if (site_id == null) {
                String[] pathParts = resource.getPath().split("/");
                site_id = (pathParts.length > 2) ? pathParts[2] : "newclub"; // default fallback site
            }
            LOG.info("Site ID: " + site_id);

            // Fetch the configuration for the identified site
            currentConfig = globalConfigService.getConfigForSite(site_id);

            if (currentConfig == null) {
                LOG.warn("No GlobalConfig found for siteId: {}", site_id);
            } else {
                LOG.info("Loaded GlobalConfig for siteId: {}", site_id);
            }

        } catch (Exception e) {
            LOG.error("Error initializing GlobalConfigModel", e);
        }
    }

    public String getSiteIdDetails() {
        return currentConfig != null ? currentConfig.site_id() : "";
    }

    /**
     * Returns the Adobe Launch script URL for the current site.
     * 
     * @return Adobe Launch URL or empty string if not configured.
     */
    public String getAdobeLaunchScript() {
        return currentConfig != null ? currentConfig.adobe_launch_url() : "";
    }

    /**
     * Returns the Google Tag Manager container ID for the current site.
     * 
     * @return GTM ID or empty string if not configured.
     */
    public String getGtmContainerId() {
        return currentConfig != null ? currentConfig.gtm_container_id() : "";
    }

    /**
     * Returns the Cookie Consent script for the current site.
     * 
     * @return Cookie Consent script or empty string if not configured.
     */
    public String getCookieConsentScript() {
        return currentConfig != null ? currentConfig.cookie_consent_script() : "";
    }

    /**
     * Returns the Brightcove Account ID for the current site.
     *
     * @return Brightcove Account ID or empty string if not configured.
     */
    public String getBrightcoveAccountId() {
        return Optional.ofNullable(currentConfig).map(GlobalConfigs::brightcove_account_id).orElse(StringUtils.EMPTY);
    }

    /**
     * Returns the Brightcove Player ID for the current site.
     *
     * @return Brightcove Player ID or empty string if not configured.
     */
    public String getBrightcovePlayerId() {
        return Optional.ofNullable(currentConfig).map(GlobalConfigs::brightcove_player_id).orElse(StringUtils.EMPTY);
    }

    /**
     * Returns the Privacy Notice script for the current site.
     *
     * @return Privacy Notice script or empty string if not configured.
     */
    public String getPrivacyNotice() {
        return Optional.ofNullable(currentConfig).map(GlobalConfigs::privacy_notice).orElse(StringUtils.EMPTY);
    }

    /**
     * Returns the Google Map Key for the current site.
     *
     * @return Google Map Key or empty string if not configured.
     */
    public String getGoogleMapKey() {
        return Optional.ofNullable(currentConfig).map(GlobalConfigs::google_map_key).orElse(StringUtils.EMPTY);
    }

}
