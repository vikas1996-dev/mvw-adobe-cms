package com.mvw.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi configuration interface that defines global settings 
 * used across the AEM project.
 * 
 * <p>This configuration can be created and managed via the OSGi Configuration 
 * Console (Web Console) or through AEM’s OSGi configuration management.</p>
 * 
 * <p>Typical use case: storing environment-specific URLs, identifiers, or 
 * JavaScript integration parameters that are required globally throughout 
 * the site.</p>
 */
@ObjectClassDefinition(
        name = "Global Configurations",
        description = "Global configuration parameters for the application"
)
public @interface GlobalConfigs {

    /**
     * Specifies the unique site identifier.
     * 
     * <p>This value helps differentiate between multiple site instances 
     * or configurations within the same AEM environment.</p>
     *
     * @return the unique site ID
     */
    @AttributeDefinition(
        name = "Site ID",
        description = "Unique identifier for the site"
    )
    String site_id();

    /**
     * Defines the Adobe Launch script URL used for analytics and tag management.
     * 
     * <p>Example: <code>https://assets.adobedtm.com/launch-XYZ.js</code></p>
     * 
     * @return the Adobe Launch script URL
     */
    @AttributeDefinition(
        name = "Adobe Launch Script URL",
        description = "Example: https://assets.adobedtm.com/launch-XYZ.js"
    )
    String adobe_launch_url() default "";

    /**
     * Defines the Google Tag Manager (GTM) Container ID for tag management.
     * 
     * <p>Example: <code>GTM-XXXXXX</code></p>
     * 
     * @return the GTM container ID
     */
    @AttributeDefinition(
        name = "Google Tag Manager Container ID",
        description = "Example: GTM-XXXXXX"
    )
    String gtm_container_id() default "";

    /**
     * Specifies the Cookie Consent script to be embedded in the site.
     * 
     * <p>This can include the full JavaScript code or a reference path 
     * to the consent management script.</p>
     * 
     * <p>Example: <code>&lt;script&gt;cookie consent script&lt;/script&gt;</code></p>
     *
     * @return the cookie consent script
     */
    @AttributeDefinition(
        name = "Cookie Consent JS Path",
        description = "<script> cookie consent script </script>"
    )
    String cookie_consent_script() default "";

    /**
     * Specifies the Brightcove Account ID to be embedded in the site.
     *
     * Example: 123456789 (numeric code)
     *
     * @return the brightcove a/c id
     */
    @AttributeDefinition(
        name = "Brightcove Account ID",
        description = "Provide Brightcove Account ID, eg: 12345"
    )
    String brightcove_account_id() default "";

    /**
     * Specifies the Brightcove Player ID to be embedded in the site.
     *
     * Example: 123456789 (alphanumeric code)
     *
     * @return the brightcove a/c id
     */
    @AttributeDefinition(
        name = "Brightcove Player ID",
        description = "Provide Brightcove Player ID, eg: abc123kh"
    )
    String brightcove_player_id() default "";

       @AttributeDefinition(
        name = "Google Map API Key",
        description = "Provide Google Map API Key, eg: ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    )
    String google_map_key() default "";

       @AttributeDefinition(
        name = "Privacy Notice Script",
        description = "Provide Privacy Notice Script, eg: <script>...</script>"
    )
    String privacy_notice() default "";
}
